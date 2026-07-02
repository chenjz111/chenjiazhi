import request from './request'

// 实时动态列表
export function getNoticeList() {
  return request.get('/travel-notice/list')
}

// 动态详情
export function getNoticeDetail(id) {
  return request.get(`/travel-notice/detail/${id}`)
}

// 按城市搜索动态
export function searchNoticeByCity(city) {
  return request.get('/travel-notice/search/city', { params: { city } })
}

// 按景点搜索动态
export function searchNoticeByAttraction(attractionName) {
  return request.get('/travel-notice/search/attraction', { params: { attractionName } })
}

// 按分类搜索动态
export function searchNoticeByCategory(category) {
  return request.get('/travel-notice/search/category', { params: { category } })
}

// 发布动态
export function addNotice(data) {
  return request.post('/travel-notice/add', data)
}

// 智能预警-城市
export function getWarningByCity(city) {
  return request.get('/travel-notice/warning/city', { params: { city } })
}

// 智能预警-景点
export function getWarningByAttraction(attractionName) {
  return request.get('/travel-notice/warning/attraction', { params: { attractionName } })
}

// 待审核动态（管理员）
export function getPendingNotices() {
  return request.get('/travel-notice/pending')
}

// 审核动态
export function approveNotice(id, result, reason, adminId) {
  return request.post(`/travel-notice/approve/${id}`, null, { params: { result, reason, adminId } })
}

// 我的动态
export function getMyNotices(userId) {
  return request.get('/travel-notice/my', { params: { userId } })
}

// 删除动态
export function deleteNotice(id, userId) {
  return request.delete(`/travel-notice/delete/${id}`, { params: { userId } })
}
