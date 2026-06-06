import { useState, type FormEvent } from 'react'
import { X } from 'lucide-react'
import { api, ApiClientError } from '../lib/api'
import { todayIso } from '../lib/format'
import type { MovementType, ProductResponse } from '../types/api'

interface Props {
  product: ProductResponse
  tipo: MovementType
  onClose: () => void
  onSuccess: () => void
}

export function MovementModal({ product, tipo, onClose, onSuccess }: Props) {
  const [quantidade, setQuantidade] = useState(1)
  const [data, setData] = useState(todayIso())
  const [observacao, setObservacao] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setSaving(true)
    try {
      await api.createMovement(product.id, {
        tipo,
        quantidade,
        data,
        observacao: observacao || undefined,
      })
      onSuccess()
    } catch (err) {
      setError(err instanceof ApiClientError ? err.message : 'Erro ao registrar movimentação')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center bg-black/50 p-4">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-md rounded-xl bg-white p-6 shadow-xl"
      >
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-lg font-bold">
            {tipo === 'ENTRADA' ? 'Registrar entrada' : 'Registrar saída'}
          </h3>
          <button type="button" onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X className="h-5 w-5" />
          </button>
        </div>
        <p className="mb-4 text-sm text-gray-500">{product.nome}</p>

        {error && (
          <p className="mb-3 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>
        )}

        <label className="mb-3 block text-sm font-medium text-gray-700">
          Quantidade
          <input
            type="number"
            min={1}
            required
            value={quantidade}
            onChange={(e) => setQuantidade(Number(e.target.value))}
            className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
          />
        </label>
        <label className="mb-3 block text-sm font-medium text-gray-700">
          Data
          <input
            type="date"
            required
            value={data}
            onChange={(e) => setData(e.target.value)}
            className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
          />
        </label>
        <label className="mb-4 block text-sm font-medium text-gray-700">
          Observação
          <input
            type="text"
            value={observacao}
            onChange={(e) => setObservacao(e.target.value)}
            className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
          />
        </label>

        <div className="flex gap-2">
          <button
            type="button"
            onClick={onClose}
            className="flex-1 rounded-lg border border-gray-300 py-2 text-sm font-medium"
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={saving}
            className="flex-1 rounded-lg bg-action py-2 text-sm font-medium text-white hover:bg-action-hover disabled:opacity-60"
          >
            {saving ? 'Salvando...' : 'Confirmar'}
          </button>
        </div>
      </form>
    </div>
  )
}
