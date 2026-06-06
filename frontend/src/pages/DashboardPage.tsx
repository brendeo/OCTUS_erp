import { useEffect, useState, type ReactNode } from 'react'
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { api, fetchAllMovements } from '../lib/api'
import {
  topProfitableProducts,
  valuesByCategory,
  valuesByMonth,
  valuesByYear,
} from '../lib/analytics'
import { formatCurrency, formatDate } from '../lib/format'
import type { DashboardResponse, ProductResponse, StockMovementResponse } from '../types/api'

const PIE_COLORS = ['#3182ce', '#48bb78', '#63b3ed', '#f56565', '#ed8936', '#9f7aea']

export function DashboardPage() {
  const [dashboard, setDashboard] = useState<DashboardResponse | null>(null)
  const [products, setProducts] = useState<ProductResponse[]>([])
  const [movements, setMovements] = useState<StockMovementResponse[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      api.dashboard(),
      api.listProducts({ size: 200 }),
      fetchAllMovements(),
    ])
      .then(([dash, prodPage, movs]) => {
        setDashboard(dash)
        setProducts(prodPage.content)
        setMovements(movs)
      })
      .finally(() => setLoading(false))
  }, [])

  const monthData = valuesByMonth(movements)
  const yearData = valuesByYear(movements)
  const categoryData = valuesByCategory(products)
  const topProducts = topProfitableProducts(products)

  if (loading) {
    return <p className="text-gray-500">Carregando dashboard...</p>
  }

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-gray-900">DashBoard</h2>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <SummaryCard label="Total de produtos" value={String(dashboard?.totalProdutos ?? 0)} />
        <SummaryCard
          label="Estoque baixo"
          value={String(dashboard?.produtosEstoqueBaixo ?? 0)}
          badge="alerta"
        />
        <SummaryCard label="A receber" value={formatCurrency(dashboard?.totalAReceber)} />
        <SummaryCard label="Saldo de caixa" value={formatCurrency(dashboard?.saldoCaixa)} />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <ChartCard title="Movimentação por mês (R$)">
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={monthData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#eee" />
              <XAxis dataKey="month" tick={{ fontSize: 12 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip formatter={(v) => formatCurrency(Number(v))} />
              <Legend />
              <Bar dataKey="entradas" name="Entradas" fill="#48bb78" radius={[4, 4, 0, 0]} />
              <Bar dataKey="saidas" name="Saídas" fill="#f56565" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Valor movimentado por ano">
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={yearData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#eee" />
              <XAxis dataKey="year" />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip formatter={(v) => formatCurrency(Number(v))} />
              <Bar dataKey="valor" name="Valor" fill="#3182ce" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Estoque por categoria (R$)">
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie
                data={categoryData}
                dataKey="valor"
                nameKey="categoria"
                cx="50%"
                cy="50%"
                outerRadius={90}
              >
                {categoryData.map((_, i) => (
                  <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />
                ))}
              </Pie>
              <Tooltip formatter={(v) => formatCurrency(Number(v))} />
            </PieChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard title="Produtos mais valiosos em estoque">
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={topProducts} layout="vertical" margin={{ left: 80 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#eee" />
              <XAxis type="number" tick={{ fontSize: 11 }} />
              <YAxis type="category" dataKey="nome" width={75} tick={{ fontSize: 10 }} />
              <Tooltip formatter={(v) => formatCurrency(Number(v))} />
              <Bar dataKey="valor" name="Valor" fill="#ed8936" radius={[0, 4, 4, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>
      </div>

      <section className="rounded-xl bg-white p-6 shadow-sm">
        <h3 className="mb-4 text-lg font-bold text-gray-900">
          Histórico de entrada e saída de produtos
        </h3>
        {movements.length === 0 ? (
          <p className="py-8 text-center text-sm text-gray-400">
            Nenhum registro de movimentação encontrado.
          </p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[640px] text-left text-sm">
              <thead>
                <tr className="border-b border-gray-200 text-gray-500">
                  <th className="pb-3 pr-4 font-medium">Data</th>
                  <th className="pb-3 pr-4 font-medium">Produto</th>
                  <th className="pb-3 pr-4 font-medium">Tipo</th>
                  <th className="pb-3 pr-4 font-medium">Quantidade</th>
                  <th className="pb-3 pr-4 font-medium">Valor ref.</th>
                  <th className="pb-3 font-medium">Observação</th>
                </tr>
              </thead>
              <tbody>
                {movements.map((m, i) => (
                  <tr
                    key={m.id}
                    className={`border-b border-gray-100 ${i % 2 === 1 ? 'bg-gray-50/80' : ''}`}
                  >
                    <td className="py-3 pr-4">{formatDate(m.data)}</td>
                    <td className="py-3 pr-4 font-medium">
                      {m.produto?.nome ?? `Produto #${m.productId}`}
                    </td>
                    <td className="py-3 pr-4">
                      <span
                        className={`rounded-full px-2 py-0.5 text-xs font-medium ${
                          m.tipo === 'ENTRADA'
                            ? 'bg-emerald-100 text-emerald-700'
                            : 'bg-orange-100 text-orange-700'
                        }`}
                      >
                        {m.tipo}
                      </span>
                    </td>
                    <td className="py-3 pr-4">{m.quantidade}</td>
                    <td className="py-3 pr-4">
                      {formatCurrency(
                        m.quantidade * (m.produto?.precoReferencia ?? 0),
                      )}
                    </td>
                    <td className="py-3 text-gray-500">{m.observacao ?? '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  )
}

function SummaryCard({
  label,
  value,
  badge,
}: {
  label: string
  value: string
  badge?: string
}) {
  return (
    <div className="rounded-xl bg-white p-5 shadow-sm">
      <p className="text-sm text-gray-500">{label}</p>
      <p className="mt-1 text-2xl font-bold text-gray-900">{value}</p>
      {badge && (
        <span className="mt-2 inline-block rounded-full bg-orange-100 px-2 py-0.5 text-xs text-orange-700">
          Atenção
        </span>
      )}
    </div>
  )
}

function ChartCard({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className="rounded-xl bg-white p-5 shadow-sm">
      <h3 className="mb-4 text-sm font-semibold text-gray-700">{title}</h3>
      {children}
    </div>
  )
}
