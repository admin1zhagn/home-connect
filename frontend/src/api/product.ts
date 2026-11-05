import request from './request'

export const productPage = (params: any) =>
  request.get('/products/page', { params })

export const getProduct = (id: number) =>
  request.get(`/products/${id}`)

export const saveProduct = (data: any) =>
  data.id ? request.put('/products', data) : request.post('/products', data)

export const deleteProduct = (id: number) =>
  request.delete(`/products/${id}`)
