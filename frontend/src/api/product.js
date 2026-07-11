import request from '@/utils/request'

export const getProductList = (params) => request.get('/product/list', { params })

export const getProductDetail = (id) => request.get('/product/' + id)

export const createProduct = (data) => request.post('/product', data)

export const getCategoryList = () => request.get('/product/categories')
