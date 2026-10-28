import api, { setStoredToken } from './axios';
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
} from '../types/auth';

export const authApi = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/api/auth/login', credentials);
    if (response.data.accessToken) {
      setStoredToken(response.data.accessToken);
    }
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/api/auth/register', data);
    if (response.data.accessToken) {
      setStoredToken(response.data.accessToken);
    }
    return response.data;
  },

  refresh: async (): Promise<AuthResponse> => {
    const response = await api.get<AuthResponse>('/api/auth/refresh');
    if (response.data.accessToken) {
      setStoredToken(response.data.accessToken);
    }
    return response.data;
  },

  logout: async (): Promise<void> => {
    try {
      await api.post('/api/auth/logout');
    } finally {
      setStoredToken(null);
      localStorage.removeItem('user_info');
    }
  },

  forgotPassword: async (data: ForgotPasswordRequest): Promise<string> => {
    const response = await api.post<string>('/api/auth/forgot-password', data);
    return response.data;
  },

  resetPassword: async (data: ResetPasswordRequest): Promise<string> => {
    const response = await api.put<string>('/api/auth/reset-password', data);
    return response.data;
  },
};
