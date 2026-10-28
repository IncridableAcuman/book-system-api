import React from 'react';
import { GENRE_LABELS, LANGUAGE_LABELS, type Book, type Genres } from '../../types/book';
import { Calendar, User, Building, Eye, Edit3, Trash2, DollarSign } from 'lucide-react';

interface BookCardProps {
  book: Book;
  onView: (book: Book) => void;
  onEdit: (book: Book) => void;
  onDelete: (book: Book) => void;
}

const genreColors: Record<Genres, string> = {
  SCIENCE: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
  ARTISTIC: 'bg-purple-500/10 text-purple-400 border-purple-500/20',
  MOTIVATION: 'bg-amber-500/10 text-amber-400 border-amber-500/20',
  ROMANCE: 'bg-pink-500/10 text-pink-400 border-pink-500/20',
  FANTASY: 'bg-indigo-500/10 text-indigo-400 border-indigo-500/20',
  HISTORICAL: 'bg-orange-500/10 text-orange-400 border-orange-500/20',
};

export const BookCard: React.FC<BookCardProps> = ({ book, onView, onEdit, onDelete }) => {
  return (
    <div className="group relative rounded-2xl bg-slate-900 border border-slate-800 hover:border-slate-700 p-5 flex flex-col justify-between transition-all duration-200 hover:shadow-xl hover:shadow-indigo-500/5 hover:-translate-y-1">
      {/* Top Section */}
      <div>
        <div className="flex items-center justify-between gap-2 mb-3">
          <span
            className={`text-xs font-semibold px-2.5 py-1 rounded-lg border ${
              genreColors[book.genres] || 'bg-slate-800 text-slate-300 border-slate-700'
            }`}
          >
            {GENRE_LABELS[book.genres] || book.genres}
          </span>
          <span className="text-xs font-medium px-2 py-0.5 rounded bg-slate-800 text-slate-400 border border-slate-700">
            {LANGUAGE_LABELS[book.languages] || book.languages}
          </span>
        </div>

        <h3 className="font-bold text-lg text-white group-hover:text-indigo-400 transition-colors line-clamp-1 mb-1">
          {book.title}
        </h3>

        <p className="text-xs text-slate-400 line-clamp-2 mb-4 leading-relaxed">
          {book.content}
        </p>

        {/* Metadata grid */}
        <div className="space-y-1.5 text-xs text-slate-400 mb-4 bg-slate-950/40 p-3 rounded-xl border border-slate-800/60">
          <div className="flex items-center gap-2">
            <User className="w-3.5 h-3.5 text-slate-400" />
            <span className="text-slate-300 font-medium truncate">{book.author}</span>
          </div>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Building className="w-3.5 h-3.5 text-slate-400" />
              <span className="truncate">{book.publisher}</span>
            </div>
            <div className="flex items-center gap-1.5 text-slate-400">
              <Calendar className="w-3.5 h-3.5" />
              <span>{book.publishedYear}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom price and actions */}
      <div className="pt-3 border-t border-slate-800/80 flex items-center justify-between gap-2">
        <div className="flex items-center text-emerald-400 font-bold text-base">
          <DollarSign className="w-4 h-4 -mr-0.5" />
          <span>{Number(book.price).toFixed(2)}</span>
        </div>

        <div className="flex items-center gap-1">
          <button
            onClick={() => onView(book)}
            title="Batafsil ko'rish"
            className="p-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <Eye className="w-4 h-4" />
          </button>
          <button
            onClick={() => onEdit(book)}
            title="Tahrirlash"
            className="p-2 rounded-lg text-slate-400 hover:text-indigo-400 hover:bg-indigo-500/10 transition-colors"
          >
            <Edit3 className="w-4 h-4" />
          </button>
          <button
            onClick={() => onDelete(book)}
            title="O'chirish"
            className="p-2 rounded-lg text-slate-400 hover:text-red-400 hover:bg-red-500/10 transition-colors"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};
