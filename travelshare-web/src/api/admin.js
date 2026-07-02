import request from './request'

// 城市列表
export function getCityList() {
  return request.get('/city/list')
}

// 城市详情
export function getCityDetail(id) {
  return request.get(`/city/detail/${id}`)
}

// 按省份搜索城市
export function searchCityByProvince(province) {
  return request.get('/city/search', { params: { province } })
}

// 新增城市
export function addCity(data) {
  return request.post('/city/add', data)
}

// 更新城市
export function updateCity(data) {
  return request.put('/city/update', data)
}

// 删除城市
export function deleteCity(id) {
  return request.delete(`/city/delete/${id}`)
}

// 景点列表
export function getAttractionList(cityId) {
  return request.get('/attraction/list', { params: { cityId } })
}

// 景点详情
export function getAttractionDetail(id) {
  return request.get(`/attraction/detail/${id}`)
}

// 搜索景点
export function searchAttraction(name) {
  return request.get('/attraction/search', { params: { name } })
}

// 新增景点
export function addAttraction(data) {
  return request.post('/attraction/add', data)
}

// 更新景点
export function updateAttraction(data) {
  return request.put('/attraction/update', data)
}

// 删除景点
export function deleteAttraction(id) {
  return request.delete(`/attraction/delete/${id}`)
}

// 首页统计数据
export function getHomeStats() {
  return request.get('/home')
}