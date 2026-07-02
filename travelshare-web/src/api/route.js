import request from './request'

// 路线列表
export function getRouteList() {
  return request.get('/route/list')
}

// 路线详情
export function getRouteDetail(id) {
  return request.get(`/route/detail/${id}`)
}

// 按省份搜索路线
export function searchRouteByProvince(province) {
  return request.get('/route/province', { params: { province } })
}

// 按城市搜索路线
export function searchRouteByCity(city) {
  return request.get('/route/city', { params: { city } })
}

// 发布路线
export function addRoute(data) {
  return request.post('/route/add', data)
}

// 待审核路线（管理员）
export function getPendingRoutes() {
  return request.get('/route/pending')
}

// 审核路线
export function approveRoute(id, result, reason, adminId) {
  return request.post(`/route/approve/${id}`, null, { params: { result, reason, adminId } })
}

// 我的路线
export function getMyRoutes(userId) {
  return request.get('/route/my', { params: { userId } })
}

// 删除路线
export function deleteRoute(id, userId) {
  return request.delete(`/route/delete/${id}`, { params: { userId } })
}
