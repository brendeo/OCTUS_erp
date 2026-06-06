import { useState, type FormEvent } from 'react'
import { X } from 'lucide-react'
import { api, ApiClientError } from '../lib/api'
import type { ProductResponse } from '../types/api'

interface Props {
  product?: ProductResponse
  onClose: () => void
  onSuccess: () => void
}

export function ProductFormModal({ product, onClose, onSuccess }: Props) {
  const isEdit = Boolean(product)
  const [nome, setNome] = useState(product?.nome ?? '')
  const [categoria, setCategoria] = useState(product?.categoria ?? '')
  const [unidade, setUnidade] = useState(product?.unidade ?? 'UN')
  const [precoReferencia, setPrecoReferencia] = useState(
    product?.precoReferencia?.toString() ?? '',
  )
  const [estoqueMinimo, setEstoqueMinimo] = useState(
    product?.estoqueMinimo?.toString() ?? '0',
  )
  const [saldoAtual, setSaldoAtual] = useState('0')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setSaving(true)
    try {
      const body = {
        nome,
        categoria,
        unidade,
        precoReferencia: Number(precoReferencia),
        estoqueMinimo: Number(estoqueMinimo),
      }
      if (isEdit && product) {
        await api.updateProduct(product.id, body)
      } else {
        await api.createProduct({
          ...body,
          saldoAtual: Number(saldoAtual) || 0,
        })
      }
      onSuccess()
    } catch (err) {
      setError(err instanceof ApiClientError ? err.message : 'Erro ao salvar produto')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-md rounded-xl bg-white p-6 shadow-xl"
      >
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-lg font-bold">
            {isEdit ? 'Editar produto' : 'Adicionar novo produto'}
          </h3>
          <button type="button" onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X className="h-5 w-5" />
          </button>
        </div>

        {error && (
          <p className="mb-3 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p>
        )}

        <div className="space-y-3">
          <Field label="Nome" value={nome} onChange={setNome} required />
          <Field label="Categoria" value={categoria} onChange={setCategoria} required />
          <Field label="Unidade" value={unidade} onChange={setUnidade} required />
          <Field
            label="Preço de referência (R$)"
            value={precoReferencia}
            onChange={setPrecoReferencia}
            type="number"
            step="0.01"
            required
          />
          <Field
            label="Estoque mínimo"
            value={estoqueMinimo}
            onChange={setEstoqueMinimo}
            type="number"
            required
          />
          {!isEdit && (
            <Field
              label="Saldo inicial"
              value={saldoAtual}
              onChange={setSaldoAtual}
              type="number"
            />
          )}
        </div>

        <div className="mt-6 flex gap-2">
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
            {saving ? 'Salvando...' : 'Salvar'}
          </button>
        </div>
      </form>
    </div>
  )
}

function Field({
  label,
  value,
  onChange,
  type = 'text',
  step,
  required,
}: {
  label: string
  value: string
  onChange: (v: string) => void
  type?: string
  step?: string
  required?: boolean
}) {
  return (
    <label className="block text-sm font-medium text-gray-700">
      {label}
      <input
        type={type}
        step={step}
        required={required}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
      />
    </label>
  )
}
