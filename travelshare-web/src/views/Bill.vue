<template>
  <div class="bill-page">
    <!-- 顶部标题 -->
    <div class="page-header">
      <h2>💰 旅游记账本</h2>
      <div class="header-actions">
        <el-button type="primary" @click="showCreateDialog = true">➕ 创建房间</el-button>
        <el-button type="success" @click="showJoinDialog = true">🔗 加入房间</el-button>
      </div>
    </div>

    <!-- 房间列表 -->
    <el-row :gutter="16" v-if="!currentRoom">
      <el-col :span="8" v-for="room in rooms" :key="room.id">
        <el-card shadow="hover" class="room-card" @click="enterRoom(room)">
          <div class="room-card-header">
            <h3>{{ room.name }}</h3>
            <el-tag size="small" :type="room.status === 'ACTIVE' ? 'success' : 'info'">{{ room.status === 'ACTIVE' ? '进行中' : '已关闭' }}</el-tag>
          </div>
          <div class="room-number">
            房间号：<strong>{{ room.roomNumber }}</strong>
          </div>
          <div class="room-footer">
            <span>创建于 {{ formatDate(room.createTime) }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!currentRoom && rooms.length === 0" description="还没有记账房间，快去创建一个吧！" />

    <!-- 房间详情（进入房间后） -->
    <div v-if="currentRoom" class="room-detail">
      <!-- 返回 + 房间信息 -->
      <div class="room-topbar">
        <el-button @click="currentRoom = null; currentMembers = []; records = []">← 返回房间列表</el-button>
        <div class="room-info">
          <span class="room-name">{{ currentRoom.name }}</span>
          <el-tag size="small" type="primary" style="margin-left:8px">房间号：{{ currentRoom.roomNumber }}</el-tag>
        </div>
        <el-button type="warning" @click="showSettleDialog = true" :disabled="records.length === 0">📊 统计结算</el-button>
      </div>

      <el-row :gutter="16">
        <!-- 左侧：成员 + 账单 -->
        <el-col :span="16">
          <!-- 成员列表 -->
          <el-card shadow="hover" class="section-card">
            <template #header>
              <div class="card-header">
                <span>👥 房间成员（{{ approvedMembers.length }}人）</span>
                <el-button v-if="pendingMembers.length > 0 && isCreator" type="primary" size="small" @click="showPendingDialog = true">
                  🔔 待审批 ({{ pendingMembers.length }})
                </el-button>
              </div>
            </template>
            <div class="member-list">
              <el-tag v-for="m in approvedMembers" :key="m.id" :type="m.role === 'CREATOR' ? 'danger' : ''" size="large" class="member-tag">
                {{ getUserName(m.userId) }} {{ m.role === 'CREATOR' ? '👑' : '' }}
              </el-tag>
            </div>
          </el-card>

          <!-- 添加账单 -->
          <el-card shadow="hover" class="section-card">
            <template #header>
              <span>📝 录入消费</span>
            </template>
            <el-form :model="billForm" label-width="100px" size="default">
              <el-row :gutter="12">
                <el-col :span="10">
                  <el-form-item label="消费日期">
                    <el-date-picker v-model="billForm.billDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
                  </el-form-item>
                </el-col>
                <el-col :span="7">
                  <el-form-item label="金额 ¥">
                    <el-input-number v-model="billForm.amount" :min="0" :precision="2" :step="1" controls-position="right" style="width:100%" />
                  </el-form-item>
                </el-col>
                <el-col :span="7">
                  <el-form-item label="付款人">
                    <el-select v-model="billForm.payerId" placeholder="选择付款人" style="width:100%">
                      <el-option v-for="m in approvedMembers" :key="m.userId" :label="getUserName(m.userId)" :value="m.userId" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="消费描述">
                <el-input v-model="billForm.description" placeholder="如：火锅晚餐、景区门票" />
              </el-form-item>
              <el-form-item label="分摊人员">
                <el-checkbox-group v-model="billForm.splitUserIds">
                  <el-checkbox v-for="m in approvedMembers" :key="m.userId" :value="m.userId" :label="getUserName(m.userId)" />
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="图片凭证">
                <el-upload
                  action=""
                  :auto-upload="false"
                  :on-change="handleBillImageChange"
                  list-type="picture-card"
                  :limit="3"
                  :file-list="billImageFileList"
                  :on-remove="handleBillImageRemove"
                >
                  <el-icon><Plus /></el-icon>
                </el-upload>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleAddBill">确认录入</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- 账单列表 -->
          <el-card shadow="hover" class="section-card">
            <template #header>
              <span>📋 消费记录（{{ records.length }}条）</span>
            </template>
            <el-table :data="records" stripe style="width:100%">
              <el-table-column prop="billDate" label="日期" width="110">
                <template #default="{ row }">{{ row.billDate }}</template>
              </el-table-column>
              <el-table-column prop="description" label="消费描述" min-width="140" />
              <el-table-column label="金额" width="100">
                <template #default="{ row }">¥{{ row.amount?.toFixed(2) }}</template>
              </el-table-column>
              <el-table-column label="付款人" width="90">
                <template #default="{ row }">{{ getUserName(row.payerId) }}</template>
              </el-table-column>
              <el-table-column label="分摊" width="160">
                <template #default="{ row }">
                  <el-tag v-for="su in getSplitUsers(row.id)" :key="su" size="small" class="split-tag">{{ getUserName(su) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="凭证" width="70">
                <template #default="{ row }">
                  <el-button v-if="row.images" size="small" link type="primary" @click="previewImages(row.images)">查看</el-button>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="70">
                <template #default="{ row }">
                  <el-button v-if="canDelete(row)" size="small" type="danger" link @click="handleDeleteBill(row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <!-- 右侧：实时统计 -->
        <el-col :span="8">
          <el-card shadow="hover" class="section-card">
            <template #header>
              <span>📊 实时统计</span>
            </template>
            <div v-if="memberStats.length > 0">
              <div v-for="stat in memberStats" :key="stat.userId" class="stat-item">
                <div class="stat-user">
                  <span class="stat-name">{{ stat.username }}</span>
                  <el-tag v-if="stat.isCreator" size="small" type="danger">👑</el-tag>
                </div>
                <div class="stat-row">
                  <span class="stat-label">已付</span>
                  <span class="stat-paid">¥{{ stat.paid.toFixed(2) }}</span>
                </div>
                <div class="stat-row">
                  <span class="stat-label">应付</span>
                  <span class="stat-should">¥{{ stat.shouldPay.toFixed(2) }}</span>
                </div>
                <div class="stat-row stat-balance">
                  <span class="stat-label">余额</span>
                  <span :class="stat.balance >= 0 ? 'balance-positive' : 'balance-negative'">
                    {{ stat.balance >= 0 ? '应收 ¥' + stat.balance.toFixed(2) : '应付 ¥' + Math.abs(stat.balance).toFixed(2) }}
                  </span>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无数据" :image-size="60" />
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 创建房间弹窗 -->
    <el-dialog v-model="showCreateDialog" title="创建记账房间" width="400px">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="房间名称">
          <el-input v-model="createForm.name" placeholder="如：广州三日游" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateRoom">创建</el-button>
      </template>
    </el-dialog>

    <!-- 加入房间弹窗 -->
    <el-dialog v-model="showJoinDialog" title="加入记账房间" width="400px">
      <el-form :model="joinForm" label-width="80px">
        <el-form-item label="房间号">
          <el-input v-model="joinForm.roomNumber" placeholder="输入6位房间号" maxlength="6" style="text-transform:uppercase" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showJoinDialog = false">取消</el-button>
        <el-button type="primary" @click="handleJoinRoom">申请加入</el-button>
      </template>
    </el-dialog>

    <!-- 待审批成员弹窗 -->
    <el-dialog v-model="showPendingDialog" title="待审批成员" width="400px">
      <div v-if="pendingMembers.length === 0">暂无待审批成员</div>
      <div v-for="m in pendingMembers" :key="m.id" class="pending-item">
        <span>{{ getUserName(m.userId) }}</span>
        <div>
          <el-button size="small" type="success" @click="handleApprove(m.id)">通过</el-button>
          <el-button size="small" type="danger" @click="handleReject(m.id)">拒绝</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 结算弹窗 -->
    <el-dialog v-model="showSettleDialog" title="📊 统计结算" width="650px" @opened="loadSettle">
      <div v-if="settleData">
        <el-alert :title="'总消费金额：¥' + settleData.totalAmount?.toFixed(2)" type="success" :closable="false" class="settle-alert" />

        <h4>👤 成员汇总</h4>
        <el-table :data="settleData.memberSummaries" stripe size="small">
          <el-table-column prop="username" label="成员" width="100" />
          <el-table-column label="已支付">
            <template #default="{ row }">¥{{ row.paid?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="应支付">
            <template #default="{ row }">¥{{ row.shouldPay?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="余额">
            <template #default="{ row }">
              <span :class="row.balance >= 0 ? 'balance-positive' : 'balance-negative'">
                {{ row.balance >= 0 ? '应收 ¥' + row.balance.toFixed(2) : '应付 ¥' + Math.abs(row.balance).toFixed(2) }}
              </span>
            </template>
          </el-table-column>
        </el-table>

        <h4 v-if="settleData.transfers && settleData.transfers.length > 0" style="margin-top:20px">💸 转账明细（最优路径）</h4>
        <el-empty v-if="!settleData.transfers || settleData.transfers.length === 0" description="已结清，无需转账" :image-size="60" />
        <div v-else class="transfer-list">
          <el-alert v-for="(t, idx) in settleData.transfers" :key="idx" type="info" :closable="false" class="transfer-item">
            <template #title>
              <strong>{{ t.fromUsername }}</strong> → <strong>{{ t.toUsername }}</strong>：¥{{ t.amount?.toFixed(2) }}
            </template>
          </el-alert>
        </div>
      </div>
    </el-dialog>

    <!-- 图片预览弹窗 -->
    <el-dialog v-model="showImagePreview" title="图片凭证" width="500px">
      <el-image v-for="(img, idx) in previewImageList" :key="idx" :src="img" fit="contain" style="width:100%;margin-bottom:8px" :preview-src-list="previewImageList" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createRoom, joinRoom, approveMember, rejectMember, getMyRooms, getRoomDetail, getRoomMembers, addBillRecord, getBillRecords, deleteBillRecord, settleRoom } from '../api/bill'
import { uploadImage } from '../api/comment'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

// ========== 状态 ==========
const rooms = ref([])
const currentRoom = ref(null)
const currentMembers = ref([])
const records = ref([])
const splitMap = ref({})  // billId -> [userId]

const showCreateDialog = ref(false)
const showJoinDialog = ref(false)
const showPendingDialog = ref(false)
const showSettleDialog = ref(false)
const showImagePreview = ref(false)
const previewImageList = ref([])

const createForm = ref({ name: '' })
const joinForm = ref({ roomNumber: '' })

const billForm = ref({
  billDate: new Date().toISOString().slice(0, 10),
  amount: 0,
  payerId: null,
  description: '',
  splitUserIds: [],
  roomId: null
})
const billImageFileList = ref([])
const billUploadedUrls = ref({})
const settleData = ref(null)

// ========== 计算属性 ==========
const approvedMembers = computed(() => currentMembers.value.filter(m => m.status === 'APPROVED'))
const pendingMembers = computed(() => currentMembers.value.filter(m => m.status === 'PENDING'))
const isCreator = computed(() => {
  const me = currentMembers.value.find(m => m.userId === userInfo.id)
  return me && me.role === 'CREATOR'
})

// 实时统计
const memberStats = computed(() => {
  if (!currentRoom.value || records.value.length === 0) return []
  const stats = {}
  approvedMembers.value.forEach(m => {
    stats[m.userId] = { userId: m.userId, username: getUserName(m.userId), paid: 0, shouldPay: 0, balance: 0, isCreator: m.role === 'CREATOR' }
  })

  records.value.forEach(record => {
    const payerId = record.payerId
    if (stats[payerId]) stats[payerId].paid += record.amount

    const splits = record.splitUserIds || []
    if (splits.length > 0) {
      const perPerson = record.amount / splits.length
      splits.forEach(uid => {
        if (stats[uid]) stats[uid].shouldPay += perPerson
      })
    }
  })

  Object.values(stats).forEach(s => {
    s.balance = s.paid - s.shouldPay
  })

  return Object.values(stats)
})

// ========== 工具函数 ==========
const getUserName = (userId) => {
  if (userId === userInfo.id) return userInfo.username || '我'
  // 从房间成员列表中查找用户名
  const member = currentMembers.value.find(m => m.userId === userId)
  if (member && member.username) return member.username
  // 从实时统计中查找（结算弹窗也用得到）
  const roomsMember = rooms.value.flatMap(r => r.members || [])
  const found = roomsMember.find(m => m.userId === userId)
  if (found && found.username) return found.username
  return '用户' + userId
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const getSplitUsers = (billId) => {
  const record = records.value.find(r => r.id === billId)
  return record?.splitUserIds || []
}

const canDelete = (row) => {
  return row.payerId === userInfo.id || isCreator.value
}

// ========== 图片上传 ==========
const handleBillImageChange = async (file) => {
  try {
    const res = await uploadImage(file.raw)
    billUploadedUrls.value[file.uid] = res.data
  } catch (e) {
    ElMessage.error('图片上传失败')
  }
}

const handleBillImageRemove = (file) => {
  delete billUploadedUrls.value[file.uid]
}

const previewImages = (images) => {
  previewImageList.value = images.split(',')
  showImagePreview.value = true
}

// ========== 房间操作 ==========
const loadRooms = async () => {
  try {
    const res = await getMyRooms(userInfo.id)
    rooms.value = res.data || []
  } catch (e) { console.error('加载房间失败', e) }
}

const handleCreateRoom = async () => {
  if (!createForm.value.name) {
    ElMessage.warning('请输入房间名称')
    return
  }
  try {
    const res = await createRoom({ name: createForm.value.name, creatorId: userInfo.id })
    ElMessage.success('房间创建成功！房间号：' + res.data.roomNumber)
    showCreateDialog.value = false
    createForm.value.name = ''
    loadRooms()
  } catch (e) { ElMessage.error('创建失败: ' + (e.message || '')) }
}

const handleJoinRoom = async () => {
  if (!joinForm.value.roomNumber) {
    ElMessage.warning('请输入房间号')
    return
  }
  try {
    await joinRoom({ roomNumber: joinForm.value.roomNumber.toUpperCase(), userId: userInfo.id })
    ElMessage.success('申请已提交，等待房间创建者审核')
    showJoinDialog.value = false
    joinForm.value.roomNumber = ''
    loadRooms()
  } catch (e) { ElMessage.error('加入失败: ' + (e.message || '')) }
}

const enterRoom = async (room) => {
  currentRoom.value = room
  billForm.value.roomId = room.id
  billForm.value.payerId = userInfo.id
  await loadRoomData()
}

const loadRoomData = async () => {
  try {
    const mr = await getRoomMembers(currentRoom.value.id)
    currentMembers.value = mr.data || []

    const rr = await getBillRecords(currentRoom.value.id)
    records.value = rr.data || []

    // 加载每条账单的分摊人员（从后端返回的数据中获取）
    splitMap.value = {}
    for (const record of records.value) {
      splitMap.value[record.id] = record.splitUserIds || []
    }
  } catch (e) { console.error('加载房间数据失败', e) }
}

const handleApprove = async (memberId) => {
  try {
    await approveMember(currentRoom.value.id, memberId, userInfo.id)
    ElMessage.success('已通过')
    loadRoomData()
  } catch (e) { ElMessage.error('审批失败') }
}

const handleReject = async (memberId) => {
  try {
    await rejectMember(currentRoom.value.id, memberId, userInfo.id)
    ElMessage.success('已拒绝')
    loadRoomData()
  } catch (e) { ElMessage.error('拒绝失败') }
}

// ========== 账单操作 ==========
const handleAddBill = async () => {
  if (!billForm.value.description) {
    ElMessage.warning('请输入消费描述')
    return
  }
  if (!billForm.value.amount || billForm.value.amount <= 0) {
    ElMessage.warning('请输入有效金额')
    return
  }
  if (!billForm.value.payerId) {
    ElMessage.warning('请选择付款人')
    return
  }
  if (!billForm.value.splitUserIds || billForm.value.splitUserIds.length === 0) {
    ElMessage.warning('请选择至少一位分摊人员')
    return
  }

  try {
    const data = {
      ...billForm.value,
      images: Object.values(billUploadedUrls.value).join(',')
    }
    await addBillRecord(data)
    ElMessage.success('账单录入成功')
    billForm.value.description = ''
    billForm.value.amount = 0
    billForm.value.splitUserIds = []
    billImageFileList.value = []
    billUploadedUrls.value = {}
    loadRoomData()
  } catch (e) { ElMessage.error('录入失败: ' + (e.message || '')) }
}

const handleDeleteBill = async (recordId) => {
  try {
    await ElMessageBox.confirm('确定删除此账单？', '确认', { type: 'warning' })
    await deleteBillRecord(recordId, userInfo.id)
    ElMessage.success('删除成功')
    loadRoomData()
  } catch (e) { /* 取消删除 */ }
}

// ========== 结算 ==========
const loadSettle = async () => {
  try {
    const res = await settleRoom(currentRoom.value.id)
    settleData.value = res.data
  } catch (e) { ElMessage.error('结算失败') }
}

onMounted(loadRooms)
</script>

<style scoped>
.bill-page {
  max-width: 1200px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  font-size: 22px;
}
.header-actions {
  display: flex;
  gap: 8px;
}
.room-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s;
}
.room-card:hover {
  transform: translateY(-3px);
}
.room-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.room-card-header h3 {
  margin: 0;
  font-size: 16px;
}
.room-number {
  color: #666;
  margin-bottom: 8px;
}
.room-footer {
  color: #999;
  font-size: 12px;
}
.room-detail {
  /* full width */
}
.room-topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  background: #fff;
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.room-info {
  display: flex;
  align-items: center;
}
.room-name {
  font-size: 18px;
  font-weight: 600;
}
.section-card {
  margin-bottom: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.member-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.member-tag {
  font-size: 14px;
  padding: 8px 14px;
}
.split-tag {
  margin-right: 4px;
  margin-bottom: 2px;
}

/* 实时统计 */
.stat-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.stat-item:last-child {
  border-bottom: none;
}
.stat-user {
  font-weight: 600;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.stat-name {
  font-size: 15px;
}
.stat-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 3px;
  font-size: 13px;
}
.stat-label {
  color: #999;
}
.stat-paid { color: #409eff; }
.stat-should { color: #e6a23c; }
.stat-balance {
  font-weight: 600;
  margin-top: 4px;
  padding-top: 4px;
  border-top: 1px dashed #eee;
}
.balance-positive { color: #67c23a; }
.balance-negative { color: #f56c6c; }

/* 结算 */
.settle-alert {
  margin-bottom: 16px;
}
.transfer-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.transfer-item {
  font-size: 15px;
}

/* 待审批 */
.pending-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}
</style>
