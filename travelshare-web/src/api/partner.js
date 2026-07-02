import request from './request'

// 搭子列表
export function getPartnerList() {
  return request.get('/partner/list')
}

// 搭子详情
export function getPartnerDetail(id) {
  return request.get(`/partner/detail/${id}`)
}

// 按城市搜索搭子
export function searchPartnerByCity(city) {
  return request.get('/partner/city', { params: { city } })
}

// 发布搭子
export function addPartner(data) {
  return request.post('/partner/add', data)
}

// 待审核搭子（管理员）
export function getPendingPartners() {
  return request.get('/partner/pending')
}

// 审核搭子
export function approvePartner(id, result, reason, adminId) {
  return request.post(`/partner/approve/${id}`, null, { params: { result, reason, adminId } })
}

// 我的搭子
export function getMyPartners(userId) {
  return request.get('/partner/my', { params: { userId } })
}

// 删除搭子
export function deletePartner(id, userId) {
  return request.delete(`/partner/delete/${id}`, { params: { userId } })
}
