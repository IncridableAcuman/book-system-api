import React from 'react';
import { Menu, Plus, User as UserIcon, LogOut } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

interface TopbarProps {
  onToggleSidebar: () => void;
  onOpenAddBookModal?: () => void;
}

export const Topbar: React.FC<TopbarProps> = ({ onToggleSidebar, onOpenAddBookModal }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-30 h-18 bg-slate-900/80 backdrop-blur-md border-b border-slate-800 px-4 sm:px-6 flex items-center justify-between">
      <div className="flex items-center gap-3">
        <button
          onClick={onToggleSidebar}
          className="lg:hidden p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          aria-label="Toggle sidebar"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div>
          <h2 className="text-base font-semibold text-white tracking-tight">
            Xush kelibsiz, <span className="text-indigo-400">{user?.username || 'Foydalanuvchi'}</span>!
          </h2>
          <p className="text-xs text-slate-400 hidden sm:block">
            Kitoblar boshqaruv tizimingiz tayyor
          </p>
        </div>
      </div>

      <div className="flex items-center gap-3">
        {onOpenAddBookModal && (
          <button
            onClick={onOpenAddBookModal}
            className="flex items-center gap-2 px-3.5 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-medium text-sm transition-all shadow-md shadow-indigo-600/20 active:scale-95"
          >
            <Plus className="w-4 h-4" />
            <span className="hidden sm:inline">Kitob qo'shish</span>
          </button>
        )}

        <div className="flex items-center gap-2 pl-3 border-l border-slate-800">
          <button
            onClick={() => navigate('/dashboard/profile')}
            className="flex items-center gap-2 p-1.5 rounded-xl hover:bg-slate-800 text-slate-300 hover:text-white transition-colors"
          >
            <div className="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-indigo-400">
              <UserIcon className="w-4 h-4" />
            </div>
            <span className="text-xs font-medium hidden md:inline px-2 py-0.5 rounded-full bg-slate-800 border border-slate-700 text-slate-300">
              {user?.role}
            </span>
          </button>

          <button
            onClick={handleLogout}
            title="Tizimdan chiqish"
            className="p-2 text-slate-400 hover:text-red-400 hover:bg-red-500/10 rounded-xl transition-colors"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </header>
  );
};
