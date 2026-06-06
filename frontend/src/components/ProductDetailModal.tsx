import { useEffect, useState } from 'react'
import { X, Minus, Plus } from 'lucide-react'
import { api } from '../lib/api'
import { formatCurrency, formatDate } from '../lib/format'
import type { ProductResponse, StockMovementResponse } from '../types/api'
import { MovementModal } from './MovementModal'

interface Props {
  product: ProductResponse
  onClose: () => void
  onUpdated: () => void
}

export function ProductDetailModal({ product, onClose, onUpdated }: Props) {
  const [movements, setMovements] = useState<StockMovementResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [movementType, setMovementType] = useState<'ENTRADA' | 'SAIDA' | null>(null)

  useEffect(() => {
    setLoading(true)
    api
      .productMovements(product.id)
      .then(setMovements)
      .finally(() => setLoading(false))
  }, [product.id])

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-xl bg-white shadow-xl">
        <div className="flex items-start justify-between border-b border-gray-100 px-6 py-4">
          <div>
            <h2 className="text-xl font-bold text-gray-900">{product.nome}</h2>
            <p className="text-sm text-gray-500">{product.categoria} · {product.unidade}</p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg p-1 text-gray-400 hover:bg-gray-100"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="grid grid-cols-2 gap-4 px-6 py-4">
          <Detail label="Quantidade" value={String(product.saldoAtual)} highlight />
          <Detail label="Estoque mínimo" value={String(product.estoqueMinimo)} />
          <Detail label="Preço referência (compra)" value={formatCurrency(product.precoReferencia)} />
          <Detail
            label="Valor em estoque (venda ref.)"
            value={formatCurrency(product.saldoAtual * product.precoReferencia)}
          />
          <Detail label="Status" value={product.ativo ? 'Ativo' : 'Inativo'} />
          <Detail
            label="Alerta"
            value={product.estoqueBaixo ? 'Estoque baixo' : 'OK'}
          />
        </div>

        <div className="flex gap-2 border-t border-gray-100 px-6 py-4">
          <button
            type="button"
            onClick={() => setMovementType('ENTRADA')}
            className="flex flex-1 items-center justify-center gap-1 rounded-lg bg-emerald-600 px-3 py-2 text-sm font-medium text-white hover:bg-emerald-700"
          >
            <Plus className="h-4 w-4" /> Entrada
          </button>
          <button
            type="button"
            onClick={() => setMovementType('SAIDA')}
            className="flex flex-1 items-center justify-center gap-1 rounded-lg bg-orange-500 px-3 py-2 text-sm font-medium text-white hover:bg-orange-600"
          >
            <Minus className="h-4 w-4" /> Saída
          </button>
        </div>

        <div className="border-t border-gray-100 px-6 py-4">
          <h3 className="mb-3 text-sm font-semibold text-gray-700">Movimentações recentes</h3>
          {loading ? (
            <p className="text-sm text-gray-400">Carregando...</p>
          ) : movements.length === 0 ? (
            <p className="text-sm text-gray-400">Nenhuma movimentação registrada.</p>
          ) : (
            <ul className="max-h-48 space-y-2 overflow-y-auto">
              {movements.map((m) => (
                <li
                  key={m.id}
                  className="flex items-center justify-between rounded-lg bg-gray-50 px-3 py-2 text-sm"
                >
                  <span>
                    <span
                      className={
                        m.tipo === 'ENTRADA' ? 'text-emerald-600' : 'text-orange-600'
                      }
                    >
                      {m.tipo}
                    </span>
                    {' · '}
                    {m.quantidade} un.
                  </span>
                  <span className="text-gray-500">{formatDate(m.data)}</span>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      {movementType && (
        <MovementModal
          product={product}
          tipo={movementType}
          onClose={() => setMovementType(null)}
          onSuccess={() => {
            setMovementType(null)
            onUpdated()
            onClose()
          }}
        />
      )}
    </div>
  )
}

function Detail({
  label,
  value,
  highlight,
}: {
  label: string
  value: string
  highlight?: boolean
}) {
  return (
    <div>
      <p className="text-xs text-gray-500">{label}</p>
      <p className={`font-semibold ${highlight ? 'text-lg text-navy' : 'text-gray-900'}`}>
        {value}
      </p>
    </div>
  )
}
