import request from './request'

export const dashboardStats = () =>
  request.get('/dashboard/stats')
