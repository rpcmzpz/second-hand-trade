import request from '@/utils/request'

export const login = (data) => request.post('/user/login', data)

export const register = (data) => request.post('/user/register', data)

export const getUserInfo = () => request.get('/user/info')

export const updateUserInfo = (data) => request.put('/user/info', data)

export const changePassword = (data) => request.put('/user/password', data)
