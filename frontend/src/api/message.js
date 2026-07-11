import request from '@/utils/request'

export function getConversations() {
  return request({ url: '/message/conversations', method: 'get' })
}

export function getConversation(userId) {
  return request({ url: '/message/conversation/' + userId, method: 'get' })
}

export function sendMessage(data) {
  return request({ url: '/message/send', method: 'post', data })
}

export function getUnreadCount() {
  return request({ url: '/message/unread', method: 'get' })
}
