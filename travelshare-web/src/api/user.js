import request from './request'

// 用户注册
export function register(data) {
  return request.post('/user/register', data)
}

// 用户登录
export function login(data) {
  return request.post('/user/login', data)
}

// 获取当前用户信息
export function getCurrentUser() {
  return request.get('/user/current')
}

// 用户列表
export function getUserList() {
  return request.get('/user/list')
}

// 禁用/启用用户
export function toggleUserStatus(id) {
  return request.put(`/user/toggle-status/${id}`)
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/user/delete/${id}`)
}