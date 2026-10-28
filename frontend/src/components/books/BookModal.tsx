import React, { useEffect, useState } from 'react';
import { X, BookOpen, AlertCircle, Loader2 } from 'lucide-react';
import { GENRE_LABELS, LANGUAGE_LABELS, type Book, type BookRequest, type Genres, type Languages } from '../../types/book';

interface BookModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: BookRequest) => Promise<void>;
  initialData?: Book | null;
  title: string;
}

const defaultFormData: BookRequest = {
  title: '',
  content: '',
  author: '',
  publisher: '',
  publishedYear: new Date().getFullYear(),
  price: 19.99,
  genres: 'SCIENCE',
  languages: 'UZB',
};

export const BookModal: React.FC<BookModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  title,
}) => {
  const [formData, setFormData] = useState<BookRequest>(defaultFormData);
  const [errors, setErrors] = useState<Partial<Record<keyof BookRequest, string>>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);

  useEffect(() => {
    if (initialData) {
      setFormData({
        title: initialData.title,
        content: initialData.content,
        author: initialData.author,
        publisher: initialData.publisher,
        publishedYear: initialData.publishedYear,
        price: initialData.price,
        genres: initialData.genres,
        languages: initialData.languages,
      });
    } else {
      setFormData(defaultFormData);
    }
    setErrors({});
    setServerError(null);
  }, [initialData, isOpen]);

  if (!isOpen) return null;

  const validate = (): boolean => {
    const newErrors: Partial<Record<keyof BookRequest, string>> = {};

    if (!formData.title.trim()) {
      newErrors.title = 'Kitob sarlavhasi talab qilinadi';
    } else if (formData.title.length < 3 || formData.title.length > 150) {
      newErrors.title = 'Sarlavha 3 dan 150 tagacha belgidan iborat bo‘lishi kerak';
    }

    if (!formData.content.trim()) {
      newErrors.content = 'Kitob tavsifi/mazmuni talab qilinadi';
    } else if (formData.content.length < 5 || formData.content.length > 150) {
      newErrors.content = 'Mazmun 5 dan 150 tagacha belgidan iborat bo‘lishi kerak';
    }

    if (!formData.author.trim()) {
      newErrors.author = 'Muallif ismi talab qilinadi';
    } else if (formData.author.length < 3 || formData.author.length > 50) {
      newErrors.author = 'Muallif 3 dan 50 tagacha belgidan iborat bo‘lishi kerak';
    }

    if (!formData.publisher.trim()) {
      newErrors.publisher = 'Nashriyot talab qilinadi';
    } else if (formData.publisher.length < 3 || formData.publisher.length > 150) {
      newErrors.publisher = 'Nashriyot 3 dan 150 tagacha belgidan iborat bo‘lishi kerak';
    }

    if (!formData.publishedYear || formData.publishedYear < 1000 || formData.publishedYear > 2100) {
      newErrors.publishedYear = 'To‘g‘ri nashr yilini kiriting (1000 - 2100)';
    }

    if (formData.price == null || formData.price < 0) {
      newErrors.price = 'Narx musbat son bo‘lishi kerak';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setServerError(null);

    if (!validate()) return;

    setIsSubmitting(true);
    try {
      await onSubmit({
        ...formData,
        publishedYear: Number(formData.publishedYear),
        price: Number(formData.price),
      });
      onClose();
    } catch (err: unknown) {
      const errorMsg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ||
        'Kitobni saqlashda xatolik yuz berdi';
      setServerError(errorMsg);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm overflow-y-auto">
      <div className="relative w-full max-w-xl rounded-2xl bg-slate-900 border border-slate-800 shadow-2xl overflow-hidden my-8">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-800">
          <div className="flex items-center gap-2.5 text-white font-bold text-lg">
            <BookOpen className="w-5 h-5 text-indigo-400" />
            <span>{title}</span>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {serverError && (
            <div className="p-3.5 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-sm flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{serverError}</span>
            </div>
          )}

          {/* Title */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">
              Kitob sarlavhasi *
            </label>
            <input
              type="text"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              placeholder="Masalan: O'tkan Kunlar"
              className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 text-white text-sm focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
            />
            {errors.title && <p className="text-red-400 text-xs mt-1">{errors.title}</p>}
          </div>

          {/* Content */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">
              Mazmun / Qisqacha tavsif *
            </label>
            <textarea
              rows={3}
              value={formData.content}
              onChange={(e) => setFormData({ ...formData, content: e.target.value })}
              placeholder="Kitob haqida qisqacha ma'lumot (5-150 belgi)"
              className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 text-white text-sm focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
            />
            {errors.content && <p className="text-red-400 text-xs mt-1">{errors.content}</p>}
          </div>

          {/* Author & Publisher */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                Muallif *
              </label>
              <input
                type="text"
                value={formData.author}
                onChange={(e) => setFormData({ ...formData, author: e.target.value })}
                placeholder="Abdulla Qodiriy"
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 text-white text-sm focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
              />
              {errors.author && <p className="text-red-400 text-xs mt-1">{errors.author}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                Nashriyot *
              </label>
              <input
                type="text"
                value={formData.publisher}
                onChange={(e) => setFormData({ ...formData, publisher: e.target.value })}
                placeholder="Sharq nashriyoti"
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 text-white text-sm focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
              />
              {errors.publisher && <p className="text-red-400 text-xs mt-1">{errors.publisher}</p>}
            </div>
          </div>

          {/* Year & Price */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                Nashr yili *
              </label>
              <input
                type="number"
                value={formData.publishedYear}
                onChange={(e) => setFormData({ ...formData, publishedYear: Number(e.target.value) })}
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 text-white text-sm focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
              />
              {errors.publishedYear && (
                <p className="text-red-400 text-xs mt-1">{errors.publishedYear}</p>
              )}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                Narxi ($) *
              </label>
              <input
                type="number"
                step="0.01"
                value={formData.price}
                onChange={(e) => setFormData({ ...formData, price: Number(e.target.value) })}
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 text-white text-sm focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
              />
              {errors.price && <p className="text-red-400 text-xs mt-1">{errors.price}</p>}
            </div>
          </div>

          {/* Genre & Language */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                Janr *
              </label>
              <select
                value={formData.genres}
                onChange={(e) => setFormData({ ...formData, genres: e.target.value as Genres })}
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 text-white text-sm focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
              >
                {(Object.keys(GENRE_LABELS) as Genres[]).map((g) => (
                  <option key={g} value={g}>
                    {GENRE_LABELS[g]}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                Til *
              </label>
              <select
                value={formData.languages}
                onChange={(e) =>
                  setFormData({ ...formData, languages: e.target.value as Languages })
                }
                className="w-full px-3.5 py-2.5 rounded-xl bg-slate-950 border border-slate-800 text-white text-sm focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
              >
                {(Object.keys(LANGUAGE_LABELS) as Languages[]).map((l) => (
                  <option key={l} value={l}>
                    {LANGUAGE_LABELS[l]}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Footer buttons */}
          <div className="pt-4 border-t border-slate-800 flex items-center justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="px-4 py-2 rounded-xl text-slate-300 hover:text-white hover:bg-slate-800 text-sm font-medium transition-colors"
            >
              Bekor qilish
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="flex items-center gap-2 px-5 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-sm transition-all shadow-md shadow-indigo-600/20 active:scale-95 disabled:opacity-50"
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Saqlanmoqda...</span>
                </>
              ) : (
                <span>Saqlash</span>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
