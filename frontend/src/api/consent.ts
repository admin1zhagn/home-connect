import request from './request'

export const consentList = (customerId: number) =>
  request.get('/consents', { params: { customerId } })

export const saveConsent = (data: any) =>
  request.post('/consents', data)

export const revokeConsent = (id: number) =>
  request.put(`/consents/${id}/revoke`)

export const deleteConsent = (id: number) =>
  request.delete(`/consents/${id}`)
