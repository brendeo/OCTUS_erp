import { useCallback, useEffect, useMemo, useState, type MouseEvent } from 'react'
import { Minus, Pencil, Plus, Search, Trash2 } from 'lucide-react'
import { api, ApiClientError } from '../lib/api'
import { formatCurrency } from '../lib/format'
import type { ProductResponse } from '../types/api'
import { MovementModal } from '../components/MovementModal'
import { ProductDetailModal } from '../components/ProductDetailModal'
import { ProductFormModal } from '../components/ProductFormModal'

export function ProductsPage() {
  const [products, setProducts] = useState<ProductResponse[]>([])
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState('')
  const [loading, setLoading] = useState(true)
  const [selected, setSelected] = useState<ProductResponse | null>(null)
  const [showForm, setShowForm] = useState(false)
  const [editProduct, setEditProduct] = useState<ProductResponse | undefined>()
  const [decreaseProduct, setDecreaseProduct] = useState<ProductResponse | null>(null)
  const [error, setError] = useState('')

  const load = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const res = await api.listProducts({
        search: search || undefined,
        category: category || undefined,
        size: 100,
      })
      setProducts(res.content.filter((p) => p.ativo))
    } catch (err) {
      setError(err instanceof ApiClientError ? err.message : 'Erro ao carregar produtos')
    } finally {
      setLoading(false)
    }
  }, [search, category])

  useEffect(() => {
    const t = setTimeout(load, 300)
    return () => clearTimeout(t)
  }, [load])

  const categories = useMemo(
    () => [...new Set(products.map((p) => p.categoria))].sort(),
    [products],
  )

  async function handleDeactivate(p: ProductResponse, e: MouseEvent) {
    e.stopPropagation()
    if (!confirm(`Desativar "${p.nome}"?`)) return
    try {
      await api.deactivateProduct(p.id)
      load()
    } catch (err) {
      alert(err instanceof ApiClientError ? err.message : 'Erro ao desativar')
    }
  }

  function qtyBadgeClass(saldo: number, minimo: number): string {
    if (saldo <= minimo) return 'bg-orange-100 text-orange-700'
    return 'bg-emerald-100 text-emerald-700'
  }

  return (
    <div className="space-y-4">
      <div className="rounded-xl bg-white p-6 shadow-sm">
        <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <h2 className="text-xl font-bold text-gray-900">Lista de Estoque de Produtos</h2>
          <button
            type="button"
            onClick={() => {
              setEditProduct(undefined)
              setShowForm(true)
            }}
            className="flex items-center justify-center gap-1 rounded-lg bg-slate-200 px-4 py-2 text-sm font-medium text-slate-800 hover:bg-slate-300"
          >
            Adicionar Novo Produto <Plus className="h-4 w-4" />
          </button>
        </div>

        <div className="mb-4 flex flex-col gap-3 sm:flex-row">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
            <input
              type="search"
              placeholder="Buscar produto..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full rounded-full border border-gray-200 py-2 pl-10 pr-4 text-sm focus:border-action focus:outline-none focus:ring-1 focus:ring-action"
            />
          </div>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="rounded-full border border-gray-200 px-4 py-2 text-sm text-gray-600"
          >
            <option value="">Categorias</option>
            {categories.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
        </div>

        {error && <p className="mb-4 text-sm text-red-600">{error}</p>}

        {loading ? (
          <p className="py-12 text-center text-gray-400">Carregando...</p>
        ) : products.length === 0 ? (
          <p className="py-12 text-center text-gray-400">Nenhum produto encontrado.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[800px] text-left text-sm">
              <thead>
                <tr className="border-b border-gray-200 text-gray-500">
                  <th className="pb-3 pr-3 font-medium">ID</th>
                  <th className="pb-3 pr-3 font-medium">Nome do Produto</th>
                  <th className="pb-3 pr-3 font-medium">Categoria</th>
                  <th className="pb-3 pr-3 font-medium">Quantidade</th>
                  <th className="pb-3 pr-3 font-medium">Preço compra</th>
                  <th className="pb-3 pr-3 font-medium">Preço venda ref.</th>
                  <th className="pb-3 font-medium">Ações</th>
                </tr>
              </thead>
              <tbody>
                {products.map((p, i) => (
                  <tr
                    key={p.id}
                    onClick={() => setSelected(p)}
                    className={`cursor-pointer border-b border-gray-100 transition hover:bg-blue-50/50 ${
                      i % 2 === 1 ? 'bg-gray-50/60' : ''
                    }`}
                  >
                    <td className="py-3 pr-3 text-gray-500">{p.id}</td>
                    <td className="py-3 pr-3 font-medium text-gray-900">{p.nome}</td>
                    <td className="py-3 pr-3 text-gray-600">{p.categoria}</td>
                    <td className="py-3 pr-3">
                      <span
                        className={`rounded-md px-2 py-0.5 text-xs font-semibold ${qtyBadgeClass(
                          p.saldoAtual,
                          p.estoqueMinimo,
                        )}`}
                      >
                        {p.saldoAtual}
                      </span>
                    </td>
                    <td className="py-3 pr-3">{formatCurrency(p.precoReferencia)}</td>
                    <td className="py-3 pr-3">
                      {formatCurrency(p.precoReferencia * 1.2)}
                    </td>
                    <td className="py-3">
                      <div className="flex gap-1" onClick={(e) => e.stopPropagation()}>
                        <button
                          type="button"
                          onClick={() => {
                            setEditProduct(p)
                            setShowForm(true)
                          }}
                          className="flex items-center gap-1 rounded-lg bg-gray-100 px-2 py-1 text-xs font-medium text-gray-700 hover:bg-gray-200"
                        >
                          <Pencil className="h-3.5 w-3.5" /> Editar
                        </button>
                        <button
                          type="button"
                          onClick={() => setDecreaseProduct(p)}
                          className="rounded-lg bg-orange-50 p-1.5 text-orange-600 hover:bg-orange-100"
                          title="Diminuir quantidade"
                        >
                          <Minus className="h-4 w-4" />
                        </button>
                        <button
                          type="button"
                          onClick={(e) => handleDeactivate(p, e)}
                          className="rounded-lg bg-red-50 p-1.5 text-red-600 hover:bg-red-100"
                          title="Desativar produto"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {selected && (
        <ProductDetailModal
          product={selected}
          onClose={() => setSelected(null)}
          onUpdated={load}
        />
      )}

      {showForm && (
        <ProductFormModal
          product={editProduct}
          onClose={() => {
            setShowForm(false)
            setEditProduct(undefined)
          }}
          onSuccess={() => {
            setShowForm(false)
            setEditProduct(undefined)
            load()
          }}
        />
      )}

      {decreaseProduct && (
        <MovementModal
          product={decreaseProduct}
          tipo="SAIDA"
          onClose={() => setDecreaseProduct(null)}
          onSuccess={() => {
            setDecreaseProduct(null)
            load()
          }}
        />
      )}
    </div>
  )
}
