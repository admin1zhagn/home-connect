import request from './request'

export const recordList = (customerId: number) =>
  request.get('/records', { params: { customerId } })

export const saveRecord = (data: any) =>
  data.id ? request.put('/records', data) : request.post('/records', data)

export const deleteRecord = (id: number) =>
  request.delete(`/records/${id}`)
