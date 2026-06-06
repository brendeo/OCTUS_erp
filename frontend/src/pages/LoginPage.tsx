import { useState, type FormEvent } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { OctopusLogo } from '../components/OctopusLogo'
import { useAuth } from '../context/AuthContext'
import { ApiClientError } from '../lib/api'
import { isAuthenticated } from '../lib/auth'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('admin@gestorpyme.local')
  const [senha, setSenha] = useState('admin123')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  if (isAuthenticated()) {
    return <Navigate to="/dashboard" replace />
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(email, senha)
      navigate('/dashboard')
    } catch (err) {
      setError(
        err instanceof ApiClientError
          ? err.message
          : 'Não foi possível entrar. Verifique o backend (perfil dev com JWT).',
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-white px-4">
      <div className="mb-8 flex flex-col items-center text-center">
        <OctopusLogo className="h-24 w-24" />
        <h1 className="mt-4 text-4xl font-bold tracking-tight text-navy">OCTUS</h1>
        <p className="mt-1 text-sm text-slate-500">sistema de controle de estoque</p>
      </div>

      <div className="w-full max-w-sm rounded-xl bg-white px-8 py-8 shadow-[0_8px_30px_rgb(0,0,0,0.08)]">
        <h2 className="mb-6 text-center font-display text-2xl font-bold text-black">LOGIN</h2>

        {error && (
          <p className="mb-4 rounded-lg bg-red-50 px-3 py-2 text-center text-sm text-red-700">
            {error}
          </p>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="email"
            required
            placeholder="E-mail"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full rounded-sm bg-navy px-4 py-3 text-white placeholder:text-white/80 focus:outline-none focus:ring-2 focus:ring-action"
          />
          <input
            type="password"
            required
            placeholder="Senha"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            className="w-full rounded-sm bg-navy px-4 py-3 text-white placeholder:text-white/80 focus:outline-none focus:ring-2 focus:ring-action"
          />
          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-sm bg-action py-3 font-semibold text-white transition hover:bg-action-hover disabled:opacity-60"
          >
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        <p className="mt-6 text-center text-xs text-gray-400">
          Demo: admin@gestorpyme.local / admin123 (perfil dev)
        </p>
      </div>
    </div>
  )
}
