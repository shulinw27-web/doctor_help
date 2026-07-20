import axios from 'axios'

export const http = axios.create({
  baseURL: '/api/v1',
  timeout: 10_000,
})

http.interceptors.request.use((config) => {
  const token = window.localStorage.getItem('doctor-help-access-token')
  if (token) config.headers.set('Authorization', `Bearer ${token}`)
  return config
})

http.interceptors.response.use((response) => response, (error) => {
  if (error.response?.status === 401) {
    window.localStorage.removeItem('doctor-help-access-token')
    window.localStorage.removeItem('doctor-help-current-user')
    window.dispatchEvent(new Event('doctor-help-session-expired'))
  }
  return Promise.reject(error)
})
