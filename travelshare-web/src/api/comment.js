import request from './request'

// 发表评论
export function addComment(data) {
  return request.post('/comment/add', data)
}

// 获取评论列表（含用户名）
export function getCommentList(type, targetId) {
  return request.get('/comment/list-with-username', { params: { type, targetId } })
}

// 上传图片
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
