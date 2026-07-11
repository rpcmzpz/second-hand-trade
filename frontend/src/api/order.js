import request from '@/utils/request'

export const createOrder = (data) => request.post('/order/create', data)

export const getOrderList = (params) => request.get('/order/list', { params })

export const getOrderDetail = (id) => request.get('/order/' + id)

export const updateOrderStatus = (id, data) => request.put('/order/' + id + '/status', data)
