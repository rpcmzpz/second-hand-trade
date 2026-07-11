import request from '@/utils/request'

export function getDashboard() {
  return request.get('/admin/dashboard')
}

export function getAdminUsers(params) {
  return request({ url: '/admin/users', method: 'get', params })
}

export function updateUserStatus(userId, data) {
  return request.put(`/admin/users/${userId}/status`, data)
}
