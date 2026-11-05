import request from './request'

export const previewAnalysis = (data: any) =>
  request.post('/analysis/preview', data)

export const saveAnalysis = (data: any) =>
  request.post('/analysis', data)

export const analysisPage = (params: any) =>
  request.get('/analysis/page', { params })

export const getAnalysis = (id: number) =>
  request.get(`/analysis/${id}`)

export const updateFeedback = (id: number, params: any) =>
  request.put(`/analysis/${id}/feedback`, null, { params })
