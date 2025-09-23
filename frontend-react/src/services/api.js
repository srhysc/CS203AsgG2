import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL;
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});
// Add request interceptor for auth tokens if needed
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

export const countryService = {
  getAll: () => api.get('/countries').then(res => res.data),
  getByCode: (iso6code) => api.get(`/countries/${iso6code}`).then(res => res.data),
  create: (data) => api.post('/countries', data).then(res => res.data),
};

export default api;