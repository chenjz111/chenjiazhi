<template>
  <div class="partner-page">
    <!-- 搜索栏 -->
    <el-card shadow="hover" class="search-card">
      <el-row :gutter="12" align="middle">
        <el-col :span="6">
          <el-input v-model="searchCity" placeholder="按城市搜索搭子" clearable />
        </el-col>
        <el-col :span="3">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-col>
        <el-col :span="3">
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
        <el-col :span="12" style="text-align:right">
          <el-button type="success" @click="showPublishDialog = true">👥 发布搭子</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 搭子列表 -->
    <el-row :gutter="20">
      <el-col :span="8" v-for="item in partners" :key="item.id">
        <el-card shadow="hover" class="partner-card" @click="viewDetail(item.id)">
          <div v-if="item.images" class="card-img">
            <img :src="item.images.split(',')[0]" alt="" />
          </div>
          <h3 class="card-title">{{ item.title }}</h3>
          <div class="partner-route">
            <span>{{ item.startCity }}</span>
            <span class="arrow">→</span>
            <span>{{ item.targetCity }}</span>
          </div>
          <div class="partner-meta">
            <span>📅 {{ item.departureDate }}</span>
            <span>👥 {{ item.currentPeople || 1 }}/{{ item.requiredPeople }}人</span>
          </div>
          <p class="card-content">{{ (item.description || '').substring(0, 60) }}...</p>
          <div class="card-footer">
            <span>{{ formatDate(item.createTime) }}</span>
            <el-button v-if="canDelete(item)" size="small" type="danger" link @click.stop="handleDeletePartner(item.id)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="partners.length === 0" description="暂无搭子信息" />

    <!-- 搭子详情弹窗 -->
    <el-dialog v-model="showDetailDialog" title="搭子详情" width="600px">
      <div v-if="currentPartner">
        <h2>{{ currentPartner.title }}</h2>
        <div class="partner-route-large">
          <span>{{ currentPartner.startCity }}</span>
          <span class="arrow">→</span>
          <span>{{ currentPartner.targetCity }}</span>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="出发日期">{{ currentPartner.departureDate }}</el-descriptions-item>
          <el-descriptions-item label="人数">{{ currentPartner.currentPeople || 1 }}/{{ currentPartner.requiredPeople }}</el-descriptions-item>
        </el-descriptions>
        <!-- 图片展示 -->
        <div v-if="currentPartner.images" class="detail-images">
          <el-image v-for="(img, idx) in currentPartner.images.split(',')" :key="idx"
            :src="img" fit="cover" class="detail-img"
            :preview-src-list="currentPartner.images.split(',')" />
        </div>
        <p class="detail-desc">{{ currentPartner.description }}</p>
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

    <!-- 发布搭子弹窗 -->
    <el-dialog v-model="showPublishDialog" title="发布旅游搭子" width="500px">
      <el-form :model="publishForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="publishForm.title" placeholder="如：广东三城游找搭子" />
        </el-form-item>
        <el-form-item label="出发城市">
          <el-input v-model="publishForm.startCity" placeholder="如广州" />
        </el-form-item>
        <el-form-item label="目的城市">
          <el-input v-model="publishForm.targetCity" placeholder="如深圳" />
        </el-form-item>
        <el-form-item label="出发日期">
          <el-date-picker v-model="publishForm.departureDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="需要人数">
          <el-input-number v-model="publishForm.requiredPeople" :min="2" :max="20" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="publishForm.description" type="textarea" :rows="3" placeholder="搭子描述" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            action=""
            :auto-upload="false"
            :on-change="handleImageChange"
            list-type="picture-card"
            :limit="3"
            :file-list="imageFileList"
            :on-remove="handleImageRemove"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">可选，最多3张</div>
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
import { Plus } from '@element-plus/icons-vue'
import { getPartnerList, searchPartnerByCity, getPartnerDetail, addPartner, deletePartner } from '../api/partner'
import { getCommentList, addComment, uploadImage } from '../api/comment'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const isAdmin = userInfo.role === 'ADMIN'

const canDelete = (item) => {
  return isAdmin || item.publisherId === userInfo.id
}

const partners = ref([])
const searchCity = ref('')
const showDetailDialog = ref(false)
const showPublishDialog = ref(false)
const currentPartner = ref(null)
const comments = ref([])
const commentContent = ref('')
const imageFileList = ref([])
const uploadedUrls = ref([])
const publishForm = ref({
  title: '', startCity: '', targetCity: '', departureDate: '', requiredPeople: 2,
  description: '', publisherId: userInfo.id, images: ''
})

const handleImageChange = async (file) => {
  try {
    const res = await uploadImage(file.raw)
    uploadedUrls.value.push(res.data)
  } catch (e) {
    ElMessage.error('图片上传失败')
  }
}

const handleImageRemove = (file) => {
  const idx = imageFileList.value.indexOf(file)
  if (idx > -1 && uploadedUrls.value[idx]) {
    uploadedUrls.value.splice(idx, 1)
  }
}

const loadData = async () => {
  try {
    const res = await getPartnerList()
    partners.value = res.data || []
  } catch (e) { console.error('加载搭子失败', e) }
}

const handleSearch = async () => {
  if (searchCity.value) {
    try {
      const res = await searchPartnerByCity(searchCity.value)
      partners.value = res.data || []
    } catch (e) { console.error('搜索搭子失败', e) }
  } else {
    loadData()
  }
}

const resetSearch = () => {
  searchCity.value = ''
  loadData()
}

const viewDetail = async (id) => {
  try {
    const res = await getPartnerDetail(id)
    currentPartner.value = res.data
    showDetailDialog.value = true
    const cr = await getCommentList('partner', id)
    comments.value = cr.data || []
  } catch (e) { console.error('加载搭子详情失败', e) }
}

const postComment = async () => {
  if (!commentContent.value) return
  try {
    await addComment({ targetType: 'partner', targetId: currentPartner.value.id, userId: userInfo.id, content: commentContent.value })
    ElMessage.success('评论成功')
    commentContent.value = ''
    const cr = await getCommentList('partner', currentPartner.value.id)
    comments.value = cr.data || []
  } catch (e) { console.error('评论失败', e) }
}

const handlePublish = async () => {
  if (!publishForm.value.title || !publishForm.value.startCity || !publishForm.value.targetCity) {
    ElMessage.warning('请填写完整信息')
    return
  }
  publishForm.value.publisherId = userInfo.id
  publishForm.value.images = uploadedUrls.value.join(',')
  try {
    await addPartner(publishForm.value)
    ElMessage.success('搭子发布成功，等待审核')
    showPublishDialog.value = false
    publishForm.value = {
      title: '', startCity: '', targetCity: '', departureDate: '', requiredPeople: 2,
      description: '', publisherId: userInfo.id, images: ''
    }
    imageFileList.value = []
    uploadedUrls.value = []
    loadData()
  } catch (e) { console.error('发布搭子失败', e) }
}

const formatDate = (dateStr) => dateStr ? new Date(dateStr).toLocaleDateString('zh-CN') : ''

const handleDeletePartner = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此搭子信息？', '确认', { type: 'warning' })
    await deletePartner(id, userInfo.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* 取消或失败 */ }
}

onMounted(loadData)
</script>

<style scoped>
.partner-page {
  max-width: 1200px;
}
.search-card {
  margin-bottom: 20px;
}
.partner-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.2s;
}
.partner-card:hover {
  transform: translateY(-4px);
}
.card-img {
  width: 100%;
  height: 140px;
  overflow: hidden;
  border-radius: 6px;
  margin-bottom: 10px;
}
.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.card-title {
  font-size: 16px;
  margin-bottom: 8px;
}
.partner-route {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}
.arrow {
  color: #999;
}
.partner-meta {
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 13px;
  margin-bottom: 8px;
}
.card-content {
  color: #999;
  font-size: 13px;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}
.partner-route-large {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  margin: 16px 0;
}
.detail-images {
  margin: 12px 0;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.detail-img {
  width: 120px;
  height: 120px;
  border-radius: 6px;
}
.detail-desc {
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
.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>
