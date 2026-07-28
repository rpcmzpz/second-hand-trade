import request from '@/utils/request'

export function createReview(data) {
  return request.post('/review', data)
}

export function getReviewList(params) {
  return request.get('/review/list', { params })
}

export function getReviewByOrder(orderId) {
  return request.get('/review/order/' + orderId)
}
