import request from './request'

// ========== 房间操作 ==========

// 创建房间
export function createRoom(data) {
  return request.post('/bill/room/create', data)
}

// 加入房间
export function joinRoom(data) {
  return request.post('/bill/room/join', data)
}

// 审批成员
export function approveMember(roomId, memberId, approverId) {
  return request.post('/bill/room/approve', null, { params: { roomId, memberId, approverId } })
}

// 拒绝成员
export function rejectMember(roomId, memberId, approverId) {
  return request.post('/bill/room/reject', null, { params: { roomId, memberId, approverId } })
}

// 我的房间列表
export function getMyRooms(userId) {
  return request.get('/bill/room/my', { params: { userId } })
}

// 房间详情
export function getRoomDetail(id) {
  return request.get(`/bill/room/detail/${id}`)
}

// 房间成员列表
export function getRoomMembers(roomId) {
  return request.get('/bill/room/members', { params: { roomId } })
}

// ========== 账单操作 ==========

// 添加账单
export function addBillRecord(data) {
  return request.post('/bill/record/add', data)
}

// 房间账单列表
export function getBillRecords(roomId) {
  return request.get('/bill/record/list', { params: { roomId } })
}

// 删除账单
export function deleteBillRecord(id, userId) {
  return request.delete(`/bill/record/delete/${id}`, { params: { userId } })
}

// ========== 结算 ==========

// 统计结算
export function settleRoom(roomId) {
  return request.get('/bill/settle', { params: { roomId } })
}
