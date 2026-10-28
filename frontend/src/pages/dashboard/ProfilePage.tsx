import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { User, Mail, Shield, LogOut, CheckCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export const ProfilePage: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="max-w-3xl space-y-6">
      <div>
        <h1 className="text-2xl font-extrabold text-white tracking-tight">Foydalanuvchi profili</h1>
        <p className="text-xs text-slate-400 mt-1">Akkauntingiz ma'lumotlari va xavfsizlik holati</p>
      </div>

      <div className="rounded-2xl bg-slate-900 border border-slate-800 p-6 sm:p-8 shadow-xl">
        {/* Profile Header */}
        <div className="flex flex-col sm:flex-row items-center sm:items-start gap-5 pb-6 border-b border-slate-800">
          <div className="w-20 h-20 rounded-2xl bg-gradient-to-tr from-indigo-600 via-indigo-500 to-cyan-400 flex items-center justify-center text-white text-3xl font-black shadow-xl shadow-indigo-500/20">
            {user?.username?.charAt(0).toUpperCase() || 'U'}
          </div>

          <div className="text-center sm:text-left flex-1">
            <div className="flex flex-col sm:flex-row sm:items-center gap-2 mb-1">
              <h2 className="text-xl font-bold text-white">{user?.username || 'Foydalanuvchi'}</h2>
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 w-fit mx-auto sm:mx-0">
                {user?.role}
              </span>
            </div>
            <p className="text-sm text-slate-400">{user?.email}</p>
          </div>
        </div>

        {/* Details list */}
        <div className="py-6 space-y-4">
          <div className="flex items-center justify-between p-4 rounded-xl bg-slate-950/60 border border-slate-800/80">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-lg bg-indigo-500/10 text-indigo-400 flex items-center justify-center">
                <User className="w-4 h-4" />
              </div>
              <div>
                <p className="text-xs text-slate-400 font-medium">Foydalanuvchi nomi</p>
                <p className="text-sm font-semibold text-white">{user?.username}</p>
              </div>
            </div>
            <span className="text-xs text-emerald-400 flex items-center gap-1 font-medium">
              <CheckCircle className="w-3.5 h-3.5" />
              Faol
            </span>
          </div>

          <div className="flex items-center justify-between p-4 rounded-xl bg-slate-950/60 border border-slate-800/80">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-lg bg-indigo-500/10 text-indigo-400 flex items-center justify-center">
                <Mail className="w-4 h-4" />
              </div>
              <div>
                <p className="text-xs text-slate-400 font-medium">Elektron pochta</p>
                <p className="text-sm font-semibold text-white">{user?.email}</p>
              </div>
            </div>
            <span className="text-xs text-indigo-400 font-medium px-2 py-0.5 rounded bg-indigo-500/10 border border-indigo-500/20">
              Tasdiqlangan
            </span>
          </div>

          <div className="flex items-center justify-between p-4 rounded-xl bg-slate-950/60 border border-slate-800/80">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-lg bg-indigo-500/10 text-indigo-400 flex items-center justify-center">
                <Shield className="w-4 h-4" />
              </div>
              <div>
                <p className="text-xs text-slate-400 font-medium">Tizimdagi roli</p>
                <p className="text-sm font-semibold text-white">{user?.role}</p>
              </div>
            </div>
            <span className="text-xs text-slate-400">ID: #{user?.id}</span>
          </div>
        </div>

        {/* Actions */}
        <div className="pt-6 border-t border-slate-800 flex justify-end">
          <button
            onClick={handleLogout}
            className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-red-500/10 hover:bg-red-500/20 text-red-400 font-semibold text-sm transition-colors border border-red-500/20"
          >
            <LogOut className="w-4 h-4" />
            <span>Tizimdan chiqish</span>
          </button>
        </div>
      </div>
    </div>
  );
};
