<template>
  <div class="notice-page">
    <!-- 搜索栏 -->
    <el-card shadow="hover" class="search-card">
      <el-row :gutter="12" align="middle">
        <el-col :span="4">
          <el-input v-model="searchCity" placeholder="按城市搜索" clearable />
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchCategory" placeholder="按分类筛选" clearable>
            <el-option label="景区关闭" value="景区关闭" />
            <el-option label="交通异常" value="交通异常" />
            <el-option label="游客拥堵" value="游客拥堵" />
            <el-option label="临时活动" value="临时活动" />
          </el-select>
        </el-col>
        <el-col :span="3">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-col>
        <el-col :span="3">
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
        <el-col :span="10" style="text-align:right">
          <el-button type="success" @click="showPublishDialog = true">📢 发布动态</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 智能预警 -->
    <el-alert v-if="cityWarnings.length > 0 && searchCity" type="warning" :closable="false" class="warning-section">
      <template #title>⚠️ 「{{ searchCity }}」当前有 {{ cityWarnings.length }} 条预警</template>
      <div v-for="w in cityWarnings" :key="w.id" class="warning-item">
        {{ w.category }} - {{ w.title }}: {{ w.content?.substring(0, 50) }}
      </div>
    </el-alert>

    <!-- 动态列表 -->
    <el-row :gutter="20">
      <el-col :span="8" v-for="item in notices" :key="item.id">
        <el-card shadow="hover" class="notice-card" @click="viewDetail(item.id)">
          <div v-if="item.images" class="card-img">
            <img :src="item.images.split(',')[0]" alt="" />
          </div>
          <div class="notice-header">
            <el-tag :type="getCategoryType(item.category)" size="small">{{ item.category || '未分类' }}</el-tag>
            <el-tag :type="getLevelType(item.level)" size="small" style="margin-left:8px">{{ item.level || 'info' }}</el-tag>
          </div>
          <h3 class="card-title">{{ item.title }}</h3>
          <p class="card-content">{{ (item.content || '').substring(0, 80) }}...</p>
          <div class="card-footer">
            <el-tag size="small" type="info">{{ item.city }}</el-tag>
            <span v-if="item.attractionName" style="margin-left:8px;color:#999">景点: {{ item.attractionName }}</span>
            <span style="margin-left:8px">{{ formatDateTime(item.createTime) }}</span>
            <el-button v-if="canDelete(item)" size="small" type="danger" link @click.stop="handleDeleteNotice(item.id)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="notices.length === 0" description="暂无动态数据" />

    <!-- 动态详情弹窗 -->
    <el-dialog v-model="showDetailDialog" title="动态详情" width="600px">
      <div v-if="currentNotice">
        <div class="detail-tags">
          <el-tag :type="getCategoryType(currentNotice.category)">{{ currentNotice.category }}</el-tag>
          <el-tag :type="getLevelType(currentNotice.level)">{{ currentNotice.level }}</el-tag>
          <el-tag type="info">{{ currentNotice.city }}</el-tag>
          <el-tag v-if="currentNotice.attractionName" type="primary">{{ currentNotice.attractionName }}</el-tag>
        </div>
        <h2>{{ currentNotice.title }}</h2>
        <!-- 图片展示 -->
        <div v-if="currentNotice.images" class="detail-images">
          <el-image v-for="(img, idx) in currentNotice.images.split(',')" :key="idx"
            :src="img" fit="cover" class="detail-img"
            :preview-src-list="currentNotice.images.split(',')" />
        </div>
        <p class="detail-content">{{ currentNotice.content }}</p>
        <div class="detail-meta">
          <span>发布者ID: {{ currentNotice.publisherId }}</span>
          <span>{{ formatDateTime(currentNotice.createTime) }}</span>
        </div>
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

    <!-- 发布动态弹窗 -->
    <el-dialog v-model="showPublishDialog" title="发布实时动态" width="500px">
      <el-form :model="publishForm" label-width="80px">
        <el-form-item label="城市">
          <el-input v-model="publishForm.city" placeholder="城市名称，如广州" />
        </el-form-item>
        <el-form-item label="景点">
          <el-input v-model="publishForm.attractionName" placeholder="景点名称（可选）" />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="publishForm.title" placeholder="动态标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="publishForm.category">
            <el-option label="景区关闭" value="景区关闭" />
            <el-option label="交通异常" value="交通异常" />
            <el-option label="游客拥堵" value="游客拥堵" />
            <el-option label="临时活动" value="临时活动" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警级别">
          <el-select v-model="publishForm.level">
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="危险" value="danger" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="publishForm.content" type="textarea" :rows="4" placeholder="动态详细内容" />
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
import { getNoticeList, searchNoticeByCity, searchNoticeByCategory, getNoticeDetail, addNotice, getWarningByCity, deleteNotice } from '../api/notice'
import { getCommentList, addComment, uploadImage } from '../api/comment'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const isAdmin = userInfo.role === 'ADMIN'

const canDelete = (item) => {
  return isAdmin || item.publisherId === userInfo.id
}

const notices = ref([])
const searchCity = ref('')
const searchCategory = ref('')
const cityWarnings = ref([])
const showDetailDialog = ref(false)
const showPublishDialog = ref(false)
const currentNotice = ref(null)
const comments = ref([])
const commentContent = ref('')
const imageFileList = ref([])
const uploadedUrls = ref({})  // key: file.uid, value: url
const publishForm = ref({
  city: '', attractionName: '', title: '', content: '', category: '景区关闭', level: 'info', publisherId: userInfo.id, images: ''
})

const getCategoryType = (cat) => {
  const map = { '景区关闭': 'danger', '交通异常': 'warning', '游客拥堵': 'warning', '临时活动': 'success' }
  return map[cat] || 'info'
}
const getLevelType = (level) => {
  const map = { 'info': 'info', 'warning': 'warning', 'danger': 'danger' }
  return map[level] || 'info'
}
const formatDate = (dateStr) => dateStr ? new Date(dateStr).toLocaleDateString('zh-CN') : ''
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleDateString('zh-CN') + ' ' + d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
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
    const res = await getNoticeList()
    notices.value = res.data || []
  } catch (e) { console.error('加载动态失败', e) }
}

const handleSearch = async () => {
  try {
    if (searchCategory.value) {
      const res = await searchNoticeByCategory(searchCategory.value)
      notices.value = res.data || []
    } else if (searchCity.value) {
      const res = await searchNoticeByCity(searchCity.value)
      notices.value = res.data || []
    } else {
      loadData()
    }
    if (searchCity.value) {
      const wr = await getWarningByCity(searchCity.value)
      cityWarnings.value = wr.data || []
    }
  } catch (e) { console.error('搜索动态失败', e) }
}

const resetSearch = () => {
  searchCity.value = ''
  searchCategory.value = ''
  cityWarnings.value = []
  loadData()
}

const viewDetail = async (id) => {
  try {
    const res = await getNoticeDetail(id)
    currentNotice.value = res.data
    showDetailDialog.value = true
    const cr = await getCommentList('travel_notice', id)
    comments.value = cr.data || []
  } catch (e) { console.error('加载动态详情失败', e) }
}

const postComment = async () => {
  if (!commentContent.value) return
  try {
    await addComment({ targetType: 'travel_notice', targetId: currentNotice.value.id, userId: userInfo.id, content: commentContent.value })
    ElMessage.success('评论成功')
    commentContent.value = ''
    const cr = await getCommentList('travel_notice', currentNotice.value.id)
    comments.value = cr.data || []
  } catch (e) { console.error('评论失败', e) }
}

const handlePublish = async () => {
  if (!publishForm.value.title || !publishForm.value.city || !publishForm.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  publishForm.value.publisherId = userInfo.id
  publishForm.value.images = Object.values(uploadedUrls.value).join(',')
  try {
    await addNotice(publishForm.value)
    ElMessage.success('动态发布成功，等待审核')
    showPublishDialog.value = false
    publishForm.value = { city: '', attractionName: '', title: '', content: '', category: '景区关闭', level: 'info', publisherId: userInfo.id, images: '' }
    imageFileList.value = []
    uploadedUrls.value = {}
    loadData()
  } catch (e) { console.error('发布动态失败', e) }
}

const handleDeleteNotice = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此动态？', '确认', { type: 'warning' })
    await deleteNotice(id, userInfo.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* 取消或失败 */ }
}

onMounted(loadData)
</script>

<style scoped>
.notice-page {
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
  margin-top: 4px;
}
.notice-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.2s;
}
.notice-card:hover {
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
.notice-header {
  margin-bottom: 8px;
}
.card-title {
  margin: 8px 0;
  font-size: 16px;
}
.card-content {
  color: #999;
  font-size: 13px;
}
.card-footer {
  margin-top: 10px;
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #999;
}
.detail-tags {
  margin-bottom: 16px;
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
  margin: 16px 0;
}
.detail-meta {
  color: #999;
  margin-top: 12px;
  display: flex;
  gap: 20px;
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
