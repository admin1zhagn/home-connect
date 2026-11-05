import request from './request'

export const followUpList = (scope: string) =>
  request.get('/follow-ups', { params: { scope } })
