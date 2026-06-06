import { Zap, LogOut } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { getApiUrl } from '../../lib/api'

export function Header() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <header className="bg-navy-dark text-white shadow-md">
      <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-4 sm:px-6 lg:px-8">
        <div className="flex items-start gap-3">
          <Zap className="mt-1 h-7 w-7 shrink-0 text-orange-400" fill="currentColor" />
          <div>
            <h1 className="text-lg font-bold tracking-wide sm:text-xl">OCTUS - sistema de controle de estoque</h1>
            <p className="text-[10px] font-medium uppercase tracking-wider text-gray-400 sm:text-xs">
              Projeto de projetos de inovações tecnológicas
            </p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <span className="hidden rounded-full bg-gray-700/80 px-3 py-1 text-xs text-gray-300 sm:inline">
            {getApiUrl()}
          </span>
          {user && (
            <span className="hidden text-sm text-gray-300 md:inline">{user.nome}</span>
          )}
          <button
            type="button"
            onClick={handleLogout}
            className="flex items-center gap-1 rounded-lg border border-gray-600 px-3 py-1.5 text-sm text-gray-200 transition hover:bg-gray-800"
            title="Sair"
          >
            <LogOut className="h-4 w-4" />
            <span className="hidden sm:inline">Sair</span>
          </button>
        </div>
      </div>
    </header>
  )
}
