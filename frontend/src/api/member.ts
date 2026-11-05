import request from './request'

export const memberList = (customerId: number) =>
  request.get('/members', { params: { customerId } })

export const saveMember = (data: any) =>
  data.id ? request.put('/members', data) : request.post('/members', data)

export const deleteMember = (id: number) =>
  request.delete(`/members/${id}`)
