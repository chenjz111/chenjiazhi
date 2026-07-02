<template>
  <div class="route-page">
    <!-- 搜索栏 -->
    <el-card shadow="hover" class="search-card">
      <el-row :gutter="12" align="middle">
        <el-col :span="5">
          <el-input v-model="searchProvince" placeholder="按省份搜索，如广东" clearable />
        </el-col>
        <el-col :span="5">
          <el-input v-model="searchCity" placeholder="按城市搜索，如广州" clearable />
        </el-col>
        <el-col :span="3">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-col>
        <el-col :span="3">
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
        <el-col :span="8" style="text-align:right">
          <el-button type="success" @click="showPublishDialog = true">🛤️ 发布路线</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 智能预警 -->
    <el-alert v-if="cityWarnings.length > 0" type="warning" :closable="false" class="warning-section">
      <template #title>⚠️ 搜索城市存在实时动态提醒</template>
      <div v-for="w in cityWarnings" :key="w.id" class="warning-item">
        {{ w.city }} - {{ w.title }}
      </div>
    </el-alert>

    <!-- 路线列表 -->
    <el-card v-for="item in routes" :key="item.id" shadow="hover" class="route-card" @click="viewDetail(item.id)">
      <div class="route-path">
        <span class="city-node">{{ item.startCity }}</span>
        <span class="arrow">→</span>
        <span class="city-node" v-if="item.middleCity">{{ item.middleCity }}</span>
        <span class="arrow" v-if="item.middleCity">→</span>
        <span class="city-node">{{ item.endCity }}</span>
      </div>
      <div class="route-info">
        <el-tag type="primary">{{ item.transportType }}</el-tag>
        <span class="info-item">⏱ {{ item.duration }}</span>
        <span class="info-item">💰 ¥{{ item.price }}</span>
      </div>
      <p class="route-desc">{{ item.description }}</p>
      <div class="route-footer">
        <el-tag type="info" size="small">{{ item.province }}</el-tag>
        <span>{{ formatDate(item.createTime) }}</span>
        <el-button v-if="canDelete(item)" size="small" type="danger" link @click.stop="handleDeleteRoute(item.id)">删除</el-button>
      </div>
    </el-card>

    <el-empty v-if="routes.length === 0" description="暂无路线数据，试试搜索吧" />

    <!-- 路线详情弹窗 -->
    <el-dialog v-model="showDetailDialog" title="路线详情" width="600px">
      <div v-if="currentRoute">
        <div class="route-path-large">
          <div class="city-step">{{ currentRoute.startCity }}</div>
          <div class="step-arrow">↓</div>
          <div class="city-step" v-if="currentRoute.middleCity">{{ currentRoute.middleCity }}</div>
          <div class="step-arrow" v-if="currentRoute.middleCity">↓</div>
          <div class="city-step">{{ currentRoute.endCity }}</div>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="交通方式">{{ currentRoute.transportType }}</el-descriptions-item>
          <el-descriptions-item label="耗时">{{ currentRoute.duration }}</el-descriptions-item>
          <el-descriptions-item label="费用">¥{{ currentRoute.price }}</el-descriptions-item>
          <el-descriptions-item label="省份">{{ currentRoute.province }}</el-descriptions-item>
        </el-descriptions>
        <p class="route-desc-detail">{{ currentRoute.description }}</p>
        <!-- 评论 -->
        <div class="comment-section">
          <h4>💬 评论</h4>
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <span class="comment-user">{{ c.username || ('用户' + c.userId) }}：</span>
            <span>{{ c.content }}</span>
          </div>
          <el-input v-model="commentContent" placeholder="发表评论...">
            <template #append>
              <el-button @click="postComment">发送</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </el-dialog>

    <!-- 发布路线弹窗 -->
    <el-dialog v-model="showPublishDialog" title="发布路线" width="500px">
      <el-form :model="publishForm" label-width="80px">
        <el-form-item label="省份">
          <el-input v-model="publishForm.province" placeholder="如广东" />
        </el-form-item>
        <el-form-item label="出发城市">
          <el-input v-model="publishForm.startCity" placeholder="如广州" />
        </el-form-item>
        <el-form-item label="途经城市">
          <el-input v-model="publishForm.middleCity" placeholder="如深圳（可选）" />
        </el-form-item>
        <el-form-item label="终点城市">
          <el-input v-model="publishForm.endCity" placeholder="如珠海" />
        </el-form-item>
        <el-form-item label="交通方式">
          <el-select v-model="publishForm.transportType">
            <el-option label="高铁" value="高铁" />
            <el-option label="大巴" value="大巴" />
            <el-option label="打车" value="打车" />
            <el-option label="自驾" value="自驾" />
          </el-select>
        </el-form-item>
        <el-form-item label="耗时">
          <el-input v-model="publishForm.duration" placeholder="如3小时" />
        </el-form-item>
        <el-form-item label="费用(元)">
          <el-input-number v-model="publishForm.price" :min="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="publishForm.description" type="textarea" :rows="3" placeholder="路线描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPublishDialog = false">取消</el-button>
        <el-button type="primary" @click="handlePublish">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRouteList, searchRouteByProvince, searchRouteByCity, getRouteDetail, addRoute, deleteRoute } from '../api/route'
import { getWarningByCity } from '../api/notice'
import { getCommentList, addComment } from '../api/comment'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const isAdmin = userInfo.role === 'ADMIN'

const canDelete = (item) => {
  return isAdmin || item.publisherId === userInfo.id
}

const routes = ref([])
const searchProvince = ref('')
const searchCity = ref('')
const cityWarnings = ref([])
const showDetailDialog = ref(false)
const showPublishDialog = ref(false)
const currentRoute = ref(null)
const comments = ref([])
const commentContent = ref('')
const publishForm = ref({
  province: '', startCity: '', middleCity: '', endCity: '', transportType: '高铁',
  duration: '', price: 0, description: '', publisherId: userInfo.id
})

const formatDate = (dateStr) => dateStr ? new Date(dateStr).toLocaleDateString('zh-CN') : ''

const loadData = async () => {
  try {
    const res = await getRouteList()
    routes.value = res.data || []
  } catch (e) { console.error('加载路线失败', e) }
}

const handleSearch = async () => {
  try {
    if (searchProvince.value) {
      const res = await searchRouteByProvince(searchProvince.value)
      routes.value = res.data || []
    } else if (searchCity.value) {
      const res = await searchRouteByCity(searchCity.value)
      routes.value = res.data || []
      const wr = await getWarningByCity(searchCity.value)
      cityWarnings.value = wr.data || []
    } else {
      loadData()
    }
  } catch (e) { console.error('搜索路线失败', e) }
}

const resetSearch = () => {
  searchProvince.value = ''
  searchCity.value = ''
  cityWarnings.value = []
  loadData()
}

const viewDetail = async (id) => {
  try {
    const res = await getRouteDetail(id)
    currentRoute.value = res.data
    showDetailDialog.value = true
    const cr = await getCommentList('route', id)
    comments.value = cr.data || []
    const cities = [res.data.startCity, res.data.middleCity, res.data.endCity].filter(Boolean)
    for (const city of cities) {
      const wr = await getWarningByCity(city)
      if (wr.data && wr.data.length > 0) {
        cityWarnings.value = [...cityWarnings.value, ...wr.data]
      }
    }
  } catch (e) { console.error('加载路线详情失败', e) }
}

const postComment = async () => {
  if (!commentContent.value) return
  try {
    await addComment({ targetType: 'route', targetId: currentRoute.value.id, userId: userInfo.id, content: commentContent.value })
    ElMessage.success('评论成功')
    commentContent.value = ''
    const cr = await getCommentList('route', currentRoute.value.id)
    comments.value = cr.data || []
  } catch (e) { console.error('评论失败', e) }
}

const handlePublish = async () => {
  if (!publishForm.value.startCity || !publishForm.value.endCity) {
    ElMessage.warning('请填写出发和终点城市')
    return
  }
  publishForm.value.publisherId = userInfo.id
  try {
    await addRoute(publishForm.value)
    ElMessage.success('路线发布成功，等待审核')
    showPublishDialog.value = false
    loadData()
  } catch (e) { console.error('发布路线失败', e) }
}

const handleDeleteRoute = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此路线？', '确认', { type: 'warning' })
    await deleteRoute(id, userInfo.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* 取消或失败 */ }
}

onMounted(loadData)
</script>

<style scoped>
.route-page {
  max-width: 1200px;
}
.search-card {
  margin-bottom: 20px;
}
.warning-section {
  margin-bottom: 16px;
}
.warning-item {
  font-size: 13px;
  color: #666;
}
.route-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s;
}
.route-card:hover {
  transform: translateY(-2px);
}
.route-path {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 12px;
}
.city-node {
  background: #409eff;
  color: #fff;
  padding: 6px 16px;
  border-radius: 20px;
}
.arrow {
  color: #999;
  font-size: 20px;
}
.route-info {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 8px;
}
.info-item {
  color: #666;
  font-size: 14px;
}
.route-desc {
  color: #999;
  font-size: 13px;
  margin: 8px 0;
}
.route-footer {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #999;
  font-size: 12px;
}
.route-path-large {
  text-align: center;
  margin: 20px 0;
}
.city-step {
  background: #409eff;
  color: #fff;
  padding: 12px 24px;
  border-radius: 24px;
  font-size: 20px;
  font-weight: bold;
  display: inline-block;
  margin: 8px 0;
}
.step-arrow {
  font-size: 24px;
  color: #409eff;
}
.route-desc-detail {
  line-height: 1.8;
  margin: 16px 0;
}
.comment-section {
  margin-top: 24px;
  border-top: 1px solid #eee;
  padding-top: 16px;
}
.comment-item {
  margin-bottom: 8px;
  padding: 8px;
  background: #f5f5f5;
  border-radius: 6px;
}
.comment-user {
  color: #409eff;
  font-weight: bold;
}
</style>
