<template>
  <div class="admin-page">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 数据统计 -->
      <el-tab-pane label="📊 数据统计" name="stats">
        <el-row :gutter="20">
          <el-col :span="4">
            <el-statistic title="用户数量" :value="stats.userCount" />
          </el-col>
          <el-col :span="4">
            <el-statistic title="攻略数量" :value="stats.strategyCount" />
          </el-col>
          <el-col :span="4">
            <el-statistic title="路线数量" :value="stats.routeCount" />
          </el-col>
          <el-col :span="4">
            <el-statistic title="动态数量" :value="stats.noticeCount" />
          </el-col>
          <el-col :span="4">
            <el-statistic title="搭子数量" :value="stats.partnerCount" />
          </el-col>
          <el-col :span="4">
            <el-statistic title="城市数量" :value="stats.cityCount" />
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 用户管理 -->
      <el-tab-pane label="👥 用户管理" name="users">
        <el-table :data="users" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="role" label="角色" width="100">
            <template #default="{ row }">
              <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'DISABLED' ? 'warning' : 'success'">
                {{ row.role }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="toggleUser(row.id)">
                {{ row.role === 'DISABLED' ? '启用' : '禁用' }}
              </el-button>
              <el-button size="small" type="danger" @click="deleteUser(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 攻略审核 -->
      <el-tab-pane label="📝 攻略审核" name="strategies">
        <el-radio-group v-model="strategyFilter" style="margin-bottom:16px">
          <el-radio-button value="pending">待审核</el-radio-button>
          <el-radio-button value="approved">已通过</el-radio-button>
        </el-radio-group>

        <!-- 待审核 -->
        <el-table v-if="strategyFilter === 'pending'" :data="pendingStrategies" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="city" label="城市" width="100" />
          <el-table-column prop="content" label="内容" show-overflow-tooltip />
          <el-table-column prop="authorId" label="作者ID" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag type="warning">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="approveStrategy(row.id, 'APPROVED')">通过</el-button>
              <el-button size="small" type="danger" @click="approveStrategy(row.id, 'REJECTED')">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 已通过 -->
        <el-table v-if="strategyFilter === 'approved'" :data="approvedStrategies" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="city" label="城市" width="100" />
          <el-table-column prop="content" label="内容" show-overflow-tooltip />
          <el-table-column prop="authorId" label="作者ID" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag type="success">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="handleDeleteStrategy(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 动态审核 -->
      <el-tab-pane label="🔔 动态审核" name="notices">
        <el-radio-group v-model="noticeFilter" style="margin-bottom:16px">
          <el-radio-button value="pending">待审核</el-radio-button>
          <el-radio-button value="approved">已通过</el-radio-button>
        </el-radio-group>

        <!-- 待审核 -->
        <el-table v-if="noticeFilter === 'pending'" :data="pendingNotices" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="city" label="城市" width="100" />
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column prop="content" label="内容" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag type="warning">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="approveNotice(row.id, 'APPROVED')">通过</el-button>
              <el-button size="small" type="danger" @click="approveNotice(row.id, 'REJECTED')">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 已通过 -->
        <el-table v-if="noticeFilter === 'approved'" :data="approvedNotices" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="city" label="城市" width="100" />
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column prop="content" label="内容" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag type="success">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="handleDeleteNotice(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 路线审核 -->
      <el-tab-pane label="🛤️ 路线审核" name="routes">
        <el-radio-group v-model="routeFilter" style="margin-bottom:16px">
          <el-radio-button value="pending">待审核</el-radio-button>
          <el-radio-button value="approved">已通过</el-radio-button>
        </el-radio-group>

        <!-- 待审核 -->
        <el-table v-if="routeFilter === 'pending'" :data="pendingRoutes" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="startCity" label="出发城市" width="100" />
          <el-table-column prop="endCity" label="终点城市" width="100" />
          <el-table-column prop="transportType" label="交通方式" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag type="warning">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="approveRoute(row.id, 'APPROVED')">通过</el-button>
              <el-button size="small" type="danger" @click="approveRoute(row.id, 'REJECTED')">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 已通过 -->
        <el-table v-if="routeFilter === 'approved'" :data="approvedRoutes" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="startCity" label="出发城市" width="100" />
          <el-table-column prop="endCity" label="终点城市" width="100" />
          <el-table-column prop="transportType" label="交通方式" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag type="success">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="handleDeleteRoute(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 搭子审核 -->
      <el-tab-pane label="👥 搭子审核" name="partners">
        <el-radio-group v-model="partnerFilter" style="margin-bottom:16px">
          <el-radio-button value="pending">待审核</el-radio-button>
          <el-radio-button value="approved">已通过</el-radio-button>
        </el-radio-group>

        <!-- 待审核 -->
        <el-table v-if="partnerFilter === 'pending'" :data="pendingPartners" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="startCity" label="出发城市" width="100" />
          <el-table-column prop="targetCity" label="目的城市" width="100" />
          <el-table-column prop="departureDate" label="出发日期" width="120" />
          <el-table-column prop="currentPeople" label="已有人数" width="90" />
          <el-table-column prop="requiredPeople" label="需要人数" width="90" />
          <el-table-column prop="description" label="描述" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag type="warning">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="approvePartner(row.id, 'APPROVED')">通过</el-button>
              <el-button size="small" type="danger" @click="approvePartner(row.id, 'REJECTED')">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 已通过 -->
        <el-table v-if="partnerFilter === 'approved'" :data="approvedPartners" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="startCity" label="出发城市" width="100" />
          <el-table-column prop="targetCity" label="目的城市" width="100" />
          <el-table-column prop="departureDate" label="出发日期" width="120" />
          <el-table-column prop="currentPeople" label="已有人数" width="90" />
          <el-table-column prop="requiredPeople" label="需要人数" width="90" />
          <el-table-column prop="description" label="描述" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag type="success">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" type="danger" @click="handleDeletePartner(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 景点管理 -->
      <el-tab-pane label="🎯 景点管理" name="attractions">
        <el-button type="primary" @click="showAddAttractionDialog = true" style="margin-bottom:16px">新增景点</el-button>
        <el-table :data="attractions" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="景点名称" />
          <el-table-column prop="cityId" label="城市ID" width="80" />
          <el-table-column prop="openTime" label="开放时间" />
          <el-table-column prop="ticketPrice" label="门票价格" width="100" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="editAttraction(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteAttraction(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增景点弹窗 -->
    <el-dialog v-model="showAddAttractionDialog" title="新增景点" width="500px">
      <el-form :model="attractionForm" label-width="80px">
        <el-form-item label="景点名称">
          <el-input v-model="attractionForm.name" />
        </el-form-item>
        <el-form-item label="城市ID">
          <el-input-number v-model="attractionForm.cityId" :min="1" />
        </el-form-item>
        <el-form-item label="介绍">
          <el-input v-model="attractionForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="开放时间">
          <el-input v-model="attractionForm.openTime" placeholder="如09:00-22:00" />
        </el-form-item>
        <el-form-item label="门票价格">
          <el-input-number v-model="attractionForm.ticketPrice" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddAttractionDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddAttraction">新增</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getHomeStats, getAttractionList, addAttraction, deleteAttraction as deleteAttractionApi } from '../api/admin'
import { getUserList, toggleUserStatus, deleteUser as deleteUserApi } from '../api/user'
import { getStrategyList, getPendingStrategies, approveStrategy as approveStrategyApi, deleteStrategy } from '../api/strategy'
import { getNoticeList, getPendingNotices, approveNotice as approveNoticeApi, deleteNotice } from '../api/notice'
import { getPartnerList, getPendingPartners, approvePartner as approvePartnerApi, deletePartner } from '../api/partner'
import { getRouteList, getPendingRoutes, approveRoute as approveRouteApi, deleteRoute } from '../api/route'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const adminId = userInfo.id

const activeTab = ref('stats')
const stats = ref({})
const users = ref([])
const pendingStrategies = ref([])
const pendingNotices = ref([])
const pendingRoutes = ref([])
const pendingPartners = ref([])
const attractions = ref([])
const showAddAttractionDialog = ref(false)
const attractionForm = ref({ name: '', cityId: 1, description: '', openTime: '', ticketPrice: 0 })

// 审核子视图切换
const strategyFilter = ref('pending')
const noticeFilter = ref('pending')
const routeFilter = ref('pending')
const partnerFilter = ref('pending')

// 已通过列表
const approvedStrategies = ref([])
const approvedNotices = ref([])
const approvedRoutes = ref([])
const approvedPartners = ref([])

const loadStats = async () => {
  try {
    const res = await getHomeStats()
    stats.value = res.data
  } catch (e) {}
}

const loadUsers = async () => {
  try {
    const res = await getUserList()
    users.value = res.data || []
  } catch (e) {}
}

const loadPendingStrategies = async () => {
  try {
    const res = await getPendingStrategies()
    pendingStrategies.value = res.data || []
  } catch (e) {}
}

const loadPendingNotices = async () => {
  try {
    const res = await getPendingNotices()
    pendingNotices.value = res.data || []
  } catch (e) {}
}

const loadPendingRoutes = async () => {
  try {
    const res = await getPendingRoutes()
    pendingRoutes.value = res.data || []
  } catch (e) {}
}

const loadPendingPartners = async () => {
  try {
    const res = await getPendingPartners()
    pendingPartners.value = res.data || []
  } catch (e) {}
}

const loadAttractions = async () => {
  try {
    const res = await getAttractionList()
    attractions.value = res.data || []
  } catch (e) {}
}

const loadApprovedStrategies = async () => {
  try {
    const res = await getStrategyList()
    approvedStrategies.value = res.data || []
  } catch (e) {}
}

const loadApprovedNotices = async () => {
  try {
    const res = await getNoticeList()
    approvedNotices.value = res.data || []
  } catch (e) {}
}

const loadApprovedRoutes = async () => {
  try {
    const res = await getRouteList()
    approvedRoutes.value = res.data || []
  } catch (e) {}
}

const loadApprovedPartners = async () => {
  try {
    const res = await getPartnerList()
    approvedPartners.value = res.data || []
  } catch (e) {}
}

// 管理员删除已通过帖子
const handleDeleteStrategy = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此攻略？', '确认', { type: 'warning' })
    await deleteStrategy(id, adminId)
    ElMessage.success('删除成功')
    loadApprovedStrategies()
    loadStats()
  } catch (e) {}
}

const handleDeleteNotice = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此动态？', '确认', { type: 'warning' })
    await deleteNotice(id, adminId)
    ElMessage.success('删除成功')
    loadApprovedNotices()
    loadStats()
  } catch (e) {}
}

const handleDeleteRoute = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此路线？', '确认', { type: 'warning' })
    await deleteRoute(id, adminId)
    ElMessage.success('删除成功')
    loadApprovedRoutes()
    loadStats()
  } catch (e) {}
}

const handleDeletePartner = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此搭子信息？', '确认', { type: 'warning' })
    await deletePartner(id, adminId)
    ElMessage.success('删除成功')
    loadApprovedPartners()
    loadStats()
  } catch (e) {}
}

const toggleUser = async (id) => {
  try {
    await toggleUserStatus(id)
    ElMessage.success('操作成功')
    loadUsers()
  } catch (e) {}
}

const deleteUser = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此用户？此操作不可撤销', '警告', { type: 'warning' })
    await deleteUserApi(id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (e) {}
}

const approveStrategy = async (id, result) => {
  try {
    await approveStrategyApi(id, result, '', adminId)
    ElMessage.success(result === 'APPROVED' ? '已通过' : '已拒绝')
    loadPendingStrategies()
    loadStats()
  } catch (e) {}
}

const approveNotice = async (id, result) => {
  try {
    await approveNoticeApi(id, result, '', adminId)
    ElMessage.success(result === 'APPROVED' ? '已通过' : '已拒绝')
    loadPendingNotices()
    loadStats()
  } catch (e) {}
}

const approveRoute = async (id, result) => {
  try {
    await approveRouteApi(id, result, '', adminId)
    ElMessage.success(result === 'APPROVED' ? '已通过' : '已拒绝')
    loadPendingRoutes()
    loadStats()
  } catch (e) {}
}

const approvePartner = async (id, result) => {
  try {
    await approvePartnerApi(id, result, '', adminId)
    ElMessage.success(result === 'APPROVED' ? '已通过' : '已拒绝')
    loadPendingPartners()
    loadStats()
  } catch (e) {}
}

const editAttraction = (row) => {
  attractionForm.value = { ...row }
  showAddAttractionDialog.value = true
}

const handleAddAttraction = async () => {
  try {
    await addAttraction(attractionForm.value)
    ElMessage.success('景点添加成功')
    showAddAttractionDialog.value = false
    loadAttractions()
  } catch (e) {}
}

const deleteAttraction = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此景点？', '警告', { type: 'warning' })
    await deleteAttraction(id)
    ElMessage.success('删除成功')
    loadAttractions()
  } catch (e) {}
}

// 切换Tab时加载数据
watch(activeTab, (tab) => {
  if (tab === 'stats') loadStats()
  if (tab === 'users') loadUsers()
  if (tab === 'strategies') { loadPendingStrategies(); loadApprovedStrategies() }
  if (tab === 'notices') { loadPendingNotices(); loadApprovedNotices() }
  if (tab === 'routes') { loadPendingRoutes(); loadApprovedRoutes() }
  if (tab === 'partners') { loadPendingPartners(); loadApprovedPartners() }
  if (tab === 'attractions') loadAttractions()
})

onMounted(loadStats)
</script>

<style scoped>
.admin-page {
  max-width: 1200px;
}
</style>