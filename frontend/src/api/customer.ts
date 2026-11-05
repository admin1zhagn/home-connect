import request from './request'

export const customerPage = (params: any) =>
  request.get('/customers/page', { params })

export const getCustomer = (id: number) =>
  request.get(`/customers/${id}`)

export const saveCustomer = (data: any) =>
  data.id ? request.put('/customers', data) : request.post('/customers', data)

export const deleteCustomer = (id: number) =>
  request.delete(`/customers/${id}`)
