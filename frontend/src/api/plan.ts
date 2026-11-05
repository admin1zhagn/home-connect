import request from './request'

// 当前套餐（客户现有套餐）
export const planList = (customerId: number) =>
  request.get('/plans', { params: { customerId } })

export const savePlan = (data: any) =>
  data.id ? request.put('/plans', data) : request.post('/plans', data)

export const deletePlan = (id: number) =>
  request.delete(`/plans/${id}`)
