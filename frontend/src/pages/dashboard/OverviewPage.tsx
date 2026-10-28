import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Library,
  DollarSign,
  Users,
  Sparkles,
  ArrowRight,
  Plus,
  BookOpen,
} from 'lucide-react';
import { bookApi } from '../../api/bookApi';
import type { Book, BookRequest } from '../../types/book';
import { BookCard } from '../../components/books/BookCard';
import { BookModal } from '../../components/books/BookModal';
import { BookDetailModal } from '../../components/books/BookDetailModal';
import { DeleteConfirmModal } from '../../components/books/DeleteConfirmModal';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';

export const OverviewPage: React.FC = () => {
  const navigate = useNavigate();
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);

  // Modals state
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [editingBook, setEditingBook] = useState<Book | null>(null);
  const [selectedBook, setSelectedBook] = useState<Book | null>(null);
  const [deletingBook, setDeletingBook] = useState<Book | null>(null);

  const fetchBooks = async () => {
    try {
      setLoading(true);
      const data = await bookApi.getBooks();
      setBooks(data);
    } catch (error) {
      console.error('Kitoblarni yuklashda xatolik:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBooks();
  }, []);

  const handleCreateBook = async (data: BookRequest) => {
    await bookApi.createBook(data);
    await fetchBooks();
  };

  const handleUpdateBook = async (data: BookRequest) => {
    if (!editingBook) return;
    await bookApi.updateBook(editingBook.id, data);
    await fetchBooks();
  };

  const handleDeleteBook = async (id: number) => {
    await bookApi.deleteBook(id);
    await fetchBooks();
  };

  // Metrics
  const totalBooks = books.length;
  const totalPrice = books.reduce((acc, b) => acc + (Number(b.price) || 0), 0);
  const uniqueAuthors = new Set(books.map((b) => b.author)).size;

  // Most common genre
  const genreCounts: Record<string, number> = {};
  books.forEach((b) => {
    genreCounts[b.genres] = (genreCounts[b.genres] || 0) + 1;
  });
  const topGenre =
    Object.entries(genreCounts).sort((a, b) => b[1] - a[1])[0]?.[0] || 'Mavjud emas';

  if (loading) {
    return (
      <div className="py-20 flex justify-center">
        <LoadingSpinner size="lg" text="Asosiy panel ma'lumotlari yuklanmoqda..." />
      </div>
    );
  }

  return (
    <div className="space-y-8 animate-fadeIn">
      {/* Welcome Banner */}
      <div className="relative rounded-3xl bg-gradient-to-r from-indigo-900/60 via-slate-900 to-slate-900 border border-indigo-500/20 p-6 sm:p-8 overflow-hidden shadow-2xl">
        <div className="absolute right-0 top-0 bottom-0 w-1/3 bg-gradient-to-l from-indigo-600/10 to-transparent pointer-events-none" />
        <div className="relative z-10 max-w-2xl">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 text-xs font-semibold mb-4">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Shaxsiy kitoblar bazasi</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight leading-snug">
            Kitoblar boshqaruvi va tahlili tizimiga xush kelibsiz
          </h1>
          <p className="mt-2 text-sm text-slate-300 leading-relaxed">
            Kutubxonangizdagi barcha kitoblarni bir joyda saqlang, mualliflar bo'yicha qidiring va yangi adabiyotlar qo'shing.
          </p>

          <div className="mt-6 flex flex-wrap items-center gap-3">
            <button
              onClick={() => setIsAddModalOpen(true)}
              className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-sm transition-all shadow-lg shadow-indigo-600/25 active:scale-95"
            >
              <Plus className="w-4 h-4" />
              <span>Yangi kitob qo'shish</span>
            </button>
            <button
              onClick={() => navigate('/dashboard/books')}
              className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-slate-800/80 hover:bg-slate-800 text-slate-200 hover:text-white font-semibold text-sm transition-all border border-slate-700 active:scale-95"
            >
              <span>Kutubxonaga o'tish</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Metrics Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 rounded-2xl bg-slate-900 border border-slate-800 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
              Jami kitoblar
            </p>
            <p className="text-2xl font-black text-white mt-1">{totalBooks}</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-indigo-500/10 text-indigo-400 flex items-center justify-center border border-indigo-500/20">
            <Library className="w-6 h-6" />
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-slate-900 border border-slate-800 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
              Umumiy qiymat
            </p>
            <p className="text-2xl font-black text-emerald-400 mt-1">
              ${totalPrice.toFixed(2)}
            </p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-emerald-500/10 text-emerald-400 flex items-center justify-center border border-emerald-500/20">
            <DollarSign className="w-6 h-6" />
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-slate-900 border border-slate-800 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
              Mualliflar soni
            </p>
            <p className="text-2xl font-black text-white mt-1">{uniqueAuthors}</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-purple-500/10 text-purple-400 flex items-center justify-center border border-purple-500/20">
            <Users className="w-6 h-6" />
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-slate-900 border border-slate-800 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
              Yetakchi janr
            </p>
            <p className="text-lg font-black text-amber-400 mt-1 truncate max-w-[140px]">
              {topGenre}
            </p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-amber-500/10 text-amber-400 flex items-center justify-center border border-amber-500/20">
            <BookOpen className="w-6 h-6" />
          </div>
        </div>
      </div>

      {/* Recent Books Section */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold text-white tracking-tight">So'nggi kitoblar</h2>
            <p className="text-xs text-slate-400">Yaqinda qo'shilgan kitoblar ro'yxati</p>
          </div>
          <button
            onClick={() => navigate('/dashboard/books')}
            className="text-xs font-semibold text-indigo-400 hover:text-indigo-300 flex items-center gap-1"
          >
            <span>Barchasini ko'rish</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>

        {books.length === 0 ? (
          <div className="text-center py-12 bg-slate-900/60 rounded-2xl border border-slate-800 p-8">
            <div className="w-14 h-14 rounded-2xl bg-slate-800 flex items-center justify-center mx-auto mb-3 text-slate-500">
              <Library className="w-7 h-7" />
            </div>
            <h3 className="text-base font-semibold text-white mb-1">
              Hozircha hech qanday kitob qo'shilmagan
            </h3>
            <p className="text-xs text-slate-400 max-w-sm mx-auto mb-4">
              Birinchi kitobingizni qo'shib kutubxonangizni shakllantirishni boshlang.
            </p>
            <button
              onClick={() => setIsAddModalOpen(true)}
              className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-medium text-xs transition-colors"
            >
              <Plus className="w-4 h-4" />
              <span>Birinchi kitobni qo'shish</span>
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
            {books.slice(0, 3).map((book) => (
              <BookCard
                key={book.id}
                book={book}
                onView={(b) => setSelectedBook(b)}
                onEdit={(b) => setEditingBook(b)}
                onDelete={(b) => setDeletingBook(b)}
              />
            ))}
          </div>
        )}
      </div>

      {/* Modals */}
      <BookModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSubmit={handleCreateBook}
        title="Yangi kitob qo'shish"
      />

      <BookModal
        isOpen={!!editingBook}
        onClose={() => setEditingBook(null)}
        onSubmit={handleUpdateBook}
        initialData={editingBook}
        title="Kitobni tahrirlash"
      />

      <BookDetailModal
        book={selectedBook}
        isOpen={!!selectedBook}
        onClose={() => setSelectedBook(null)}
        onEdit={(b) => {
          setSelectedBook(null);
          setEditingBook(b);
        }}
      />

      <DeleteConfirmModal
        book={deletingBook}
        isOpen={!!deletingBook}
        onClose={() => setDeletingBook(null)}
        onConfirm={handleDeleteBook}
      />
    </div>
  );
};
