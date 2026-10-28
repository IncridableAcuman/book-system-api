import React, { useState } from 'react';
import { AlertTriangle, Loader2 } from 'lucide-react';
import type { Book } from '../../types/book';

interface DeleteConfirmModalProps {
  book: Book | null;
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (id: number) => Promise<void>;
}

export const DeleteConfirmModal: React.FC<DeleteConfirmModalProps> = ({
  book,
  isOpen,
  onClose,
  onConfirm,
}) => {
  const [isDeleting, setIsDeleting] = useState(false);

  if (!isOpen || !book) return null;

  const handleConfirm = async () => {
    setIsDeleting(true);
    try {
      await onConfirm(book.id);
      onClose();
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
      <div className="relative w-full max-w-md rounded-2xl bg-slate-900 border border-slate-800 shadow-2xl p-6 text-center">
        <div className="w-12 h-12 rounded-2xl bg-red-500/10 text-red-400 flex items-center justify-center mx-auto mb-4 border border-red-500/20">
          <AlertTriangle className="w-6 h-6" />
        </div>

        <h3 className="text-lg font-bold text-white mb-2">Kitobni o'chirishni tasdiqlaysizmi?</h3>
        <p className="text-sm text-slate-300 mb-6 leading-relaxed">
          <span className="font-semibold text-white">"{book.title}"</span> nomli kitob tizimdan butunlay o'chiriladi. Bu amalni orqaga qaytarib bo'lmaydi.
        </p>

        <div className="flex items-center justify-center gap-3">
          <button
            type="button"
            onClick={onClose}
            disabled={isDeleting}
            className="px-4 py-2 rounded-xl text-slate-300 hover:text-white hover:bg-slate-800 text-sm font-medium transition-colors"
          >
            Bekor qilish
          </button>
          <button
            type="button"
            onClick={handleConfirm}
            disabled={isDeleting}
            className="flex items-center gap-2 px-5 py-2 rounded-xl bg-red-600 hover:bg-red-500 text-white font-semibold text-sm transition-all shadow-md shadow-red-600/20 active:scale-95 disabled:opacity-50"
          >
            {isDeleting ? (
              <>
                <Loader2 className="w-4 h-4 animate-spin" />
                <span>O'chirilmoqda...</span>
              </>
            ) : (
              <span>Ha, o'chirilsin</span>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};
