import React, { createContext, useEffect, useState, useCallback } from 'react';
import type { User, LoginRequest, RegisterRequest } from '../types/auth';
import { authApi } from '../api/authApi';
import { getStoredToken, setStoredToken } from '../api/axios';

export interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const cached = localStorage.getItem('user_info');
    return cached ? JSON.parse(cached) : null;
  });
  const [isLoading, setIsLoading] = useState(true);

  const initAuth = useCallback(async () => {
    try {
      const token = getStoredToken();
      if (token) {
        // Try refreshing or verifying session
        const res = await authApi.refresh();
        const currentUser: User = {
          id: res.id,
          username: res.email.split('@')[0],
          email: res.email,
          role: res.role,
        };
        setUser(currentUser);
        localStorage.setItem('user_info', JSON.stringify(currentUser));
      }
    } catch {
      setUser(null);
      setStoredToken(null);
      localStorage.removeItem('user_info');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    initAuth();

    const handleUnauthorized = () => {
      setUser(null);
      setStoredToken(null);
      localStorage.removeItem('user_info');
    };

    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, [initAuth]);

  const login = async (credentials: LoginRequest) => {
    const res = await authApi.login(credentials);
    const currentUser: User = {
      id: res.id,
      username: res.email.split('@')[0],
      email: res.email,
      role: res.role,
    };
    setUser(currentUser);
    localStorage.setItem('user_info', JSON.stringify(currentUser));
  };

  const register = async (data: RegisterRequest) => {
    const res = await authApi.register(data);
    const currentUser: User = {
      id: res.id,
      username: data.username,
      email: res.email,
      role: res.role,
    };
    setUser(currentUser);
    localStorage.setItem('user_info', JSON.stringify(currentUser));
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } finally {
      setUser(null);
      setStoredToken(null);
      localStorage.removeItem('user_info');
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export { useAuth } from './useAuth';
