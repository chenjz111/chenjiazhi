import request from './request'

// 攻略列表（已审核通过的）
export function getStrategyList() {
  return request.get('/strategy/list')
}

// 攻略详情
export function getStrategyDetail(id) {
  return request.get(`/strategy/detail/${id}`)
}

// 攻略搜索
export function searchStrategy(city, keyword) {
  return request.get('/strategy/search', { params: { city, keyword } })
}

// 发布攻略
export function addStrategy(data) {
  return request.post('/strategy/add', data)
}

// 我的攻略
export function getMyStrategies(userId) {
  return request.get('/strategy/my', { params: { userId } })
}

// 待审核攻略（管理员）
export function getPendingStrategies() {
  return request.get('/strategy/pending')
}

// 审核攻略
export function approveStrategy(id, result, reason, adminId) {
  return request.post(`/strategy/approve/${id}`, null, { params: { result, reason, adminId } })
}

// 删除攻略
export function deleteStrategy(id, userId) {
  return request.delete(`/strategy/delete/${id}`, { params: { userId } })
}