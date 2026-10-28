import React, { useEffect, useState, useMemo } from 'react';
import {
  Search,
  Plus,
  Filter,
  LayoutGrid,
  List as ListIcon,
  BookOpen,
  DollarSign,
  Calendar,
  Eye,
  Edit3,
  Trash2,
  RefreshCw,
} from 'lucide-react';
import { bookApi } from '../../api/bookApi';
import { GENRE_LABELS, LANGUAGE_LABELS, type Book, type BookRequest, type Genres, type Languages } from '../../types/book';
import { BookCard } from '../../components/books/BookCard';
import { BookModal } from '../../components/books/BookModal';
import { BookDetailModal } from '../../components/books/BookDetailModal';
import { DeleteConfirmModal } from '../../components/books/DeleteConfirmModal';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';

export const BooksPage: React.FC = () => {
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [viewMode, setViewMode] = useState<'grid' | 'table'>('grid');

  // Filters & Search
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedGenre, setSelectedGenre] = useState<string>('ALL');
  const [selectedLanguage, setSelectedLanguage] = useState<string>('ALL');
  const [sortBy, setSortBy] = useState<'newest' | 'title' | 'price-asc' | 'price-desc' | 'year'>('newest');

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
    } catch (err) {
      console.error('Kitoblarni yuklashda xatolik:', err);
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

  // Filter & Sort Pipeline
  const filteredAndSortedBooks = useMemo(() => {
    return books
      .filter((b) => {
        const matchesSearch =
          b.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
          b.author.toLowerCase().includes(searchTerm.toLowerCase()) ||
          b.content.toLowerCase().includes(searchTerm.toLowerCase()) ||
          b.publisher.toLowerCase().includes(searchTerm.toLowerCase());

        const matchesGenre = selectedGenre === 'ALL' || b.genres === selectedGenre;
        const matchesLanguage = selectedLanguage === 'ALL' || b.languages === selectedLanguage;

        return matchesSearch && matchesGenre && matchesLanguage;
      })
      .sort((a, b) => {
        if (sortBy === 'title') return a.title.localeCompare(b.title);
        if (sortBy === 'price-asc') return Number(a.price) - Number(b.price);
        if (sortBy === 'price-desc') return Number(b.price) - Number(a.price);
        if (sortBy === 'year') return b.publishedYear - a.publishedYear;
        // Default newest
        return new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime();
      });
  }, [books, searchTerm, selectedGenre, selectedLanguage, sortBy]);

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight">
            Kitoblar kutubxonasi
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Barcha kitoblar ro'yxati, filtrlash va boshqarish
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <button
            onClick={fetchBooks}
            title="Yangilash"
            className="p-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-800 text-slate-400 hover:text-white transition-colors"
          >
            <RefreshCw className="w-4 h-4" />
          </button>

          <button
            onClick={() => setIsAddModalOpen(true)}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-sm transition-all shadow-md shadow-indigo-600/20 active:scale-95"
          >
            <Plus className="w-4 h-4" />
            <span>Kitob qo'shish</span>
          </button>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="p-4 rounded-2xl bg-slate-900 border border-slate-800 flex flex-col md:flex-row items-stretch md:items-center justify-between gap-3 shadow-sm">
        {/* Search */}
        <div className="relative flex-1">
          <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-500">
            <Search className="w-4 h-4" />
          </div>
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Nomi, muallifi yoki mazmuni bo'yicha qidirish..."
            className="w-full pl-10 pr-3.5 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white text-sm placeholder-slate-500 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
          />
        </div>

        {/* Filter Controls */}
        <div className="flex flex-wrap items-center gap-2.5">
          {/* Genre Filter */}
          <div className="flex items-center gap-1.5 bg-slate-950 border border-slate-800 px-3 py-1.5 rounded-xl text-xs">
            <Filter className="w-3.5 h-3.5 text-slate-500" />
            <select
              value={selectedGenre}
              onChange={(e) => setSelectedGenre(e.target.value)}
              className="bg-transparent text-slate-300 font-medium focus:outline-none cursor-pointer"
            >
              <option value="ALL" className="bg-slate-900">Barcha janrlar</option>
              {(Object.keys(GENRE_LABELS) as Genres[]).map((g) => (
                <option key={g} value={g} className="bg-slate-900">
                  {GENRE_LABELS[g]}
                </option>
              ))}
            </select>
          </div>

          {/* Language Filter */}
          <div className="flex items-center gap-1.5 bg-slate-950 border border-slate-800 px-3 py-1.5 rounded-xl text-xs">
            <select
              value={selectedLanguage}
              onChange={(e) => setSelectedLanguage(e.target.value)}
              className="bg-transparent text-slate-300 font-medium focus:outline-none cursor-pointer"
            >
              <option value="ALL" className="bg-slate-900">Barcha tillar</option>
              {(Object.keys(LANGUAGE_LABELS) as Languages[]).map((l) => (
                <option key={l} value={l} className="bg-slate-900">
                  {LANGUAGE_LABELS[l]}
                </option>
              ))}
            </select>
          </div>

          {/* Sort By */}
          <div className="flex items-center gap-1.5 bg-slate-950 border border-slate-800 px-3 py-1.5 rounded-xl text-xs">
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value as typeof sortBy)}
              className="bg-transparent text-slate-300 font-medium focus:outline-none cursor-pointer"
            >
              <option value="newest" className="bg-slate-900">Eng yangilar</option>
              <option value="title" className="bg-slate-900">Nomi bo'yicha (A-Z)</option>
              <option value="price-asc" className="bg-slate-900">Narxi (Arzondan)</option>
              <option value="price-desc" className="bg-slate-900">Narxi (Qimmatdan)</option>
              <option value="year" className="bg-slate-900">Yili bo'yicha</option>
            </select>
          </div>

          {/* View Mode Toggle */}
          <div className="flex items-center bg-slate-950 border border-slate-800 p-0.5 rounded-xl">
            <button
              onClick={() => setViewMode('grid')}
              title="Karta ko'rinishi"
              className={`p-1.5 rounded-lg transition-colors ${
                viewMode === 'grid'
                  ? 'bg-indigo-600 text-white shadow-sm'
                  : 'text-slate-500 hover:text-slate-300'
              }`}
            >
              <LayoutGrid className="w-4 h-4" />
            </button>
            <button
              onClick={() => setViewMode('table')}
              title="Jadval ko'rinishi"
              className={`p-1.5 rounded-lg transition-colors ${
                viewMode === 'table'
                  ? 'bg-indigo-600 text-white shadow-sm'
                  : 'text-slate-500 hover:text-slate-300'
              }`}
            >
              <ListIcon className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Main Books Content */}
      {loading ? (
        <div className="py-20 flex justify-center">
          <LoadingSpinner size="lg" text="Kitoblar yuklanmoqda..." />
        </div>
      ) : filteredAndSortedBooks.length === 0 ? (
        <div className="text-center py-16 bg-slate-900 rounded-2xl border border-slate-800 p-8">
          <div className="w-16 h-16 rounded-2xl bg-slate-800 flex items-center justify-center mx-auto mb-4 text-slate-500">
            <BookOpen className="w-8 h-8" />
          </div>
          <h3 className="text-lg font-bold text-white mb-1">
            Hech qanday kitob topilmadi
          </h3>
          <p className="text-xs text-slate-400 max-w-sm mx-auto mb-6">
            Qidiruv so'zini yoki filtrlarni o'zgartirib ko'ring yoki yangi kitob qo'shing.
          </p>
          <button
            onClick={() => setIsAddModalOpen(true)}
            className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-medium text-xs transition-colors"
          >
            <Plus className="w-4 h-4" />
            <span>Yangi kitob qo'shish</span>
          </button>
        </div>
      ) : viewMode === 'grid' ? (
        /* Grid View */
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredAndSortedBooks.map((book) => (
            <BookCard
              key={book.id}
              book={book}
              onView={(b) => setSelectedBook(b)}
              onEdit={(b) => setEditingBook(b)}
              onDelete={(b) => setDeletingBook(b)}
            />
          ))}
        </div>
      ) : (
        /* Table View */
        <div className="bg-slate-900 rounded-2xl border border-slate-800 overflow-hidden shadow-sm">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-300">
              <thead className="bg-slate-950/60 text-xs uppercase font-semibold text-slate-400 border-b border-slate-800">
                <tr>
                  <th className="px-6 py-4">Sarlavha</th>
                  <th className="px-6 py-4">Muallif</th>
                  <th className="px-6 py-4">Janr</th>
                  <th className="px-6 py-4">Til</th>
                  <th className="px-6 py-4">Yil</th>
                  <th className="px-6 py-4">Narx</th>
                  <th className="px-6 py-4 text-right">Amallar</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {filteredAndSortedBooks.map((book) => (
                  <tr key={book.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-6 py-4 font-semibold text-white">
                      <div className="max-w-xs truncate" title={book.title}>
                        {book.title}
                      </div>
                    </td>
                    <td className="px-6 py-4 truncate text-slate-300">{book.author}</td>
                    <td className="px-6 py-4">
                      <span className="text-xs px-2.5 py-1 rounded-md bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
                        {GENRE_LABELS[book.genres] || book.genres}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-xs font-medium text-slate-400">
                      {LANGUAGE_LABELS[book.languages] || book.languages}
                    </td>
                    <td className="px-6 py-4 text-xs text-slate-400">
                      <div className="flex items-center gap-1">
                        <Calendar className="w-3.5 h-3.5" />
                        <span>{book.publishedYear}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 font-bold text-emerald-400">
                      <div className="flex items-center">
                        <DollarSign className="w-3.5 h-3.5 -mr-0.5" />
                        <span>{Number(book.price).toFixed(2)}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <button
                          onClick={() => setSelectedBook(book)}
                          title="Ko'rish"
                          className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => setEditingBook(book)}
                          title="Tahrirlash"
                          className="p-1.5 rounded-lg text-slate-400 hover:text-indigo-400 hover:bg-indigo-500/10 transition-colors"
                        >
                          <Edit3 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => setDeletingBook(book)}
                          title="O'chirish"
                          className="p-1.5 rounded-lg text-slate-400 hover:text-red-400 hover:bg-red-500/10 transition-colors"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

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
