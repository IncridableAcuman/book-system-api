import api from './axios';
import type { Book, BookRequest, Genres, Languages } from '../types/book';

export const bookApi = {
  getBooks: async (): Promise<Book[]> => {
    const response = await api.get<Book[]>('/api/book');
    return response.data;
  },

  getBookById: async (id: number): Promise<Book> => {
    const response = await api.get<Book>(`/api/book/list/${id}`);
    return response.data;
  },

  getBooksByAuthor: async (author: string): Promise<Book[]> => {
    const response = await api.get<Book[]>(`/api/book/list/authors/${encodeURIComponent(author)}`);
    return response.data;
  },

  getBooksByGenre: async (genre: Genres): Promise<Book[]> => {
    const response = await api.get<Book[]>(`/api/book/list/genres/${genre}`);
    return response.data;
  },

  getBooksByLanguage: async (language: Languages): Promise<Book[]> => {
    const response = await api.get<Book[]>(`/api/book/list/languages/${language}`);
    return response.data;
  },

  createBook: async (data: BookRequest): Promise<Book> => {
    const response = await api.post<Book>('/api/book', data);
    return response.data;
  },

  updateBook: async (id: number, data: BookRequest): Promise<Book> => {
    const response = await api.patch<Book>(`/api/book/edit/${id}`, data);
    return response.data;
  },

  deleteBook: async (id: number): Promise<string> => {
    const response = await api.delete<string>(`/api/book/${id}`);
    return response.data;
  },
};
