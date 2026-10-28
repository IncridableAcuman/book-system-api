import React from 'react';
import { Link } from 'react-router-dom';
import { BookOpen, Home, ArrowLeft } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center p-4 text-center">
      <div className="w-16 h-16 rounded-2xl bg-indigo-500/10 text-indigo-400 flex items-center justify-center mx-auto mb-6 border border-indigo-500/20">
        <BookOpen className="w-8 h-8" />
      </div>

      <h1 className="text-6xl font-black text-white tracking-tight mb-2">404</h1>
      <h2 className="text-2xl font-bold text-slate-200 mb-2">Sahifa topilmadi</h2>
      <p className="text-sm text-slate-400 max-w-sm mb-8 leading-relaxed">
        Siz qidirayotgan sahifa mavjud emas, nomi o‘zgargan yoki vaqtincha o‘chirilgan bo‘lishi mumkin.
      </p>

      <div className="flex items-center gap-3">
        <Link
          to="/dashboard"
          className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-sm transition-all shadow-lg shadow-indigo-600/25 active:scale-95"
        >
          <Home className="w-4 h-4" />
          <span>Asosiy panelga qaytish</span>
        </Link>
        <Link
          to="/login"
          className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 text-slate-300 font-semibold text-sm transition-colors border border-slate-800"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Kirish</span>
        </Link>
      </div>
    </div>
  );
};
