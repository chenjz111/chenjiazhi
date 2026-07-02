<template>
  <div class="strategy-page">
    <!-- 搜索栏 -->
    <el-card shadow="hover" class="search-card">
      <el-row :gutter="12" align="middle">
        <el-col :span="6">
          <el-input v-model="searchCity" placeholder="按城市搜索" clearable />
        </el-col>
        <el-col :span="6">
          <el-input v-model="searchKeyword" placeholder="按关键词搜索" clearable />
        </el-col>
        <el-col :span="3">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-col>
        <el-col :span="3">
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
        <el-col :span="6" style="text-align:right">
          <el-button type="success" @click="showPublishDialog = true">✏️ 发布攻略</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 智能预警提醒 -->
    <el-alert v-if="cityWarnings.length > 0 && searchCity" type="warning" :closable="false" class="warning-section">
      <template #title>⚠️ 当前城市「{{ searchCity }}」存在 {{ cityWarnings.length }} 条实时动态</template>
      <div v-for="w in cityWarnings" :key="w.id" class="warning-item">
        {{ w.title }} - {{ w.content?.substring(0, 60) }}
      </div>
    </el-alert>

    <!-- 攻略列表 -->
    <el-row :gutter="20">
      <el-col :span="8" v-for="item in strategies" :key="item.id">
        <el-card shadow="hover" class="strategy-card" @click="viewDetail(item.id)">
          <div v-if="item.images" class="card-img">
            <img :src="item.images.split(',')[0]" alt="" />
          </div>
          <h3 class="card-title">{{ item.title }}</h3>
          <el-tag size="small" type="primary">{{ item.city }}</el-tag>
          <el-tag size="small" type="info" style="margin-left:8px">{{ item.status }}</el-tag>
          <p class="card-content">{{ (item.content || '').substring(0, 100) }}...</p>
          <div class="card-footer">
            <span>作者ID: {{ item.authorId }}</span>
            <span>{{ formatDate(item.createTime) }}</span>
            <el-button v-if="canDelete(item)" size="small" type="danger" link @click.stop="handleDeleteStrategy(item.id)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="strategies.length === 0" description="暂无攻略数据" />

    <!-- 攻略详情弹窗 -->
    <el-dialog v-model="showDetailDialog" title="攻略详情" width="600px">
      <div v-if="currentStrategy">
        <h2>{{ currentStrategy.title }}</h2>
        <el-tag type="primary">{{ currentStrategy.city }}</el-tag>
        <div class="detail-meta">
          <span>作者ID: {{ currentStrategy.authorId }}</span>
          <span>发布时间: {{ formatDate(currentStrategy.createTime) }}</span>
        </div>
        <!-- 图片展示 -->
        <div v-if="currentStrategy.images" class="detail-images">
          <el-image v-for="(img, idx) in currentStrategy.images.split(',')" :key="idx"
            :src="img" fit="cover" class="detail-img"
            :preview-src-list="currentStrategy.images.split(',')" />
        </div>
        <div class="detail-content">{{ currentStrategy.content }}</div>
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

    <!-- 发布攻略弹窗 -->
    <el-dialog v-model="showPublishDialog" title="发布攻略" width="500px">
      <el-form :model="publishForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="publishForm.title" placeholder="攻略标题" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="publishForm.city" placeholder="旅游城市，如广州" />
        </el-form-item>
        <el-form-item label="正文">
          <el-input v-model="publishForm.content" type="textarea" :rows="6" placeholder="攻略正文内容" />
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
import { getStrategyList, searchStrategy, getStrategyDetail, addStrategy, deleteStrategy } from '../api/strategy'
import { getWarningByCity } from '../api/notice'
import { getCommentList, addComment, uploadImage } from '../api/comment'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const isAdmin = userInfo.role === 'ADMIN'

const canDelete = (item) => {
  return isAdmin || item.authorId === userInfo.id
}

const strategies = ref([])
const searchCity = ref('')
const searchKeyword = ref('')
const cityWarnings = ref([])
const showDetailDialog = ref(false)
const showPublishDialog = ref(false)
const currentStrategy = ref(null)
const comments = ref([])
const commentContent = ref('')
const imageFileList = ref([])
const uploadedUrls = ref({})  // key: file.uid, value: url
const publishForm = ref({ title: '', city: '', content: '', authorId: userInfo.id, images: '' })

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const handleImageChange = async (file) => {
  try {
    const res = await uploadImage(file.raw)
    uploadedUrls.value[file.uid] = res.data
  } catch (e) {
    ElMessage.error('图片上传失败')
  }
}

const handleImageRemove = (file) => {
  delete uploadedUrls.value[file.uid]
}

const loadData = async () => {
  try {
    const res = await getStrategyList()
    strategies.value = res.data || []
  } catch (e) { console.error('加载攻略失败', e) }
}

const handleSearch = async () => {
  if (searchCity.value || searchKeyword.value) {
    try {
      const res = await searchStrategy(searchCity.value, searchKeyword.value)
      strategies.value = res.data || []
      if (searchCity.value) {
        try {
          const wr = await getWarningByCity(searchCity.value)
          cityWarnings.value = wr.data || []
        } catch (e) { console.error('加载预警失败', e) }
      }
    } catch (e) { console.error('搜索失败', e) }
  } else {
    loadData()
  }
}

const resetSearch = () => {
  searchCity.value = ''
  searchKeyword.value = ''
  cityWarnings.value = []
  loadData()
}

const viewDetail = async (id) => {
  try {
    const res = await getStrategyDetail(id)
    currentStrategy.value = res.data
    showDetailDialog.value = true
    try {
      const cr = await getCommentList('strategy', id)
      comments.value = cr.data || []
    } catch (e) { console.error('加载评论失败', e) }
  } catch (e) { console.error('加载攻略详情失败', e) }
}

const postComment = async () => {
  if (!commentContent.value) return
  try {
    await addComment({ targetType: 'strategy', targetId: currentStrategy.value.id, userId: userInfo.id, content: commentContent.value })
    ElMessage.success('评论成功')
    commentContent.value = ''
    const cr = await getCommentList('strategy', currentStrategy.value.id)
    comments.value = cr.data || []
  } catch (e) { console.error('评论失败', e) }
}

const handlePublish = async () => {
  if (!publishForm.value.title || !publishForm.value.city || !publishForm.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  publishForm.value.authorId = userInfo.id
  publishForm.value.images = Object.values(uploadedUrls.value).join(',')
  try {
    await addStrategy(publishForm.value)
    ElMessage.success('攻略发布成功，等待审核')
    showPublishDialog.value = false
    publishForm.value = { title: '', city: '', content: '', authorId: userInfo.id, images: '' }
    imageFileList.value = []
    uploadedUrls.value = {}
    loadData()
  } catch (e) { console.error('发布攻略失败', e) }
}

const handleDeleteStrategy = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此攻略？', '确认', { type: 'warning' })
    await deleteStrategy(id, userInfo.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* 取消或失败 */ }
}

onMounted(loadData)
</script>

<style scoped>
.strategy-page {
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
.strategy-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.2s;
}
.strategy-card:hover {
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
  margin-bottom: 8px;
  font-size: 16px;
}
.card-content {
  color: #999;
  font-size: 13px;
  margin: 10px 0;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
}
.detail-meta {
  color: #999;
  margin: 12px 0;
  display: flex;
  gap: 20px;
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
.detail-content {
  line-height: 1.8;
  margin-top: 16px;
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
