import request from '@/utils/request'

export function getAdminCategories() {
  return request({ url: '/admin/categories', method: 'get' })
}

export function addCategory(data) {
  return request({ url: '/admin/categories', method: 'post', data })
}

export function updateCategory(id, data) {
  return request({ url: '/admin/categories/' + id, method: 'put', data })
}

export function deleteCategory(id) {
  return request({ url: '/admin/categories/' + id, method: 'delete' })
}
