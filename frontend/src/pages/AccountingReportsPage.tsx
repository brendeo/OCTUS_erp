import { useCallback, useEffect, useMemo, useState, type ReactNode } from 'react'
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
import { Download, Lock } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { api, ApiClientError } from '../lib/api'
import {
  mergeTransactions,
  monthlyCashFlow,
  openPayablesTotal,
  openReceivablesTotal,
  payablesByStatus,
} from '../lib/accountingAnalytics'
import { formatCurrency, formatDate } from '../lib/format'
import type {
  CashFlowResponse,
  PayableResponse,
  ReceivableResponse,
  UserRole,
} from '../types/api'

const PIE_COLORS = ['#3182ce', '#48bb78', '#f56565']
const REPORT_ROLES: UserRole[] = ['GESTOR', 'CONTABIL']

function firstDayOfMonth(): string {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-01`
}

function todayIso(): string {
  return new Date().toISOString().slice(0, 10)
}

function statusBadgeClass(status: string): string {
  if (status === 'PAGA' || status === 'RECEBIDA') return 'bg-emerald-100 text-emerald-700'
  if (status === 'VENCIDA') return 'bg-red-100 text-red-700'
  return 'bg-amber-100 text-amber-800'
}

function statusLabel(status: string): string {
  const map: Record<string, string> = {
    PENDENTE: 'Pendente',
    PAGA: 'Pago',
    RECEBIDA: 'Recebido',
    VENCIDA: 'Vencido',
  }
  return map[status] ?? status
}

function exportCsv(
  payables: PayableResponse[],
  receivables: ReceivableResponse[],
  cashFlow: CashFlowResponse | null,
) {
  const lines = [
    'Relatório de Contabilidade - OCTUS',
    cashFlow
      ? `Período;${cashFlow.from};${cashFlow.to}`
      : '',
    `Entradas;${cashFlow?.totalEntradas ?? 0}`,
    `Saídas;${cashFlow?.totalSaidas ?? 0}`,
    `Saldo;${cashFlow?.saldo ?? 0}`,
    '',
    'CONTAS A PAGAR',
    'ID;Descrição;Valor;Vencimento;Status;Data Pagamento',
    ...payables.map(
      (p) =>
        `${p.id};${p.descricao};${p.valor};${p.vencimento};${p.status};${p.dataPagamento ?? ''}`,
    ),
    '',
    'CONTAS A RECEBER',
    'ID;Descrição;Valor;Vencimento;Status;Data Recebimento',
    ...receivables.map(
      (r) =>
        `${r.id};${r.descricao};${r.valor};${r.vencimento};${r.status};${r.dataRecebimento ?? ''}`,
    ),
  ]
  const blob = new Blob([lines.join('\n')], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `relatorio-contabilidade-${todayIso()}.csv`
  a.click()
  URL.revokeObjectURL(url)
}

export function AccountingReportsPage() {
  const { user } = useAuth()
  const canAccess = user && REPORT_ROLES.includes(user.perfil)

  const [from, setFrom] = useState(firstDayOfMonth())
  const [to, setTo] = useState(todayIso())
  const [cashFlow, setCashFlow] = useState<CashFlowResponse | null>(null)
  const [payables, setPayables] = useState<PayableResponse[]>([])
  const [receivables, setReceivables] = useState<ReceivableResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = useCallback(async () => {
    if (!canAccess) return
    setLoading(true)
    setError('')
    try {
      const [cf, pay, rec] = await Promise.all([
        api.reportCashFlow(from, to),
        api.reportPayables({ from, to }),
        api.reportReceivables({ from, to }),
      ])
      setCashFlow(cf)
      setPayables(pay)
      setReceivables(rec)
    } catch (err) {
      const msg =
        err instanceof ApiClientError
          ? err.status === 403
            ? 'Sem permissão para relatórios. Use perfil GESTOR ou CONTABIL.'
            : err.message
          : 'Erro ao carregar relatórios'
      setError(msg)
      setCashFlow(null)
      setPayables([])
      setReceivables([])
    } finally {
      setLoading(false)
    }
  }, [canAccess, from, to])

  useEffect(() => {
    load()
  }, [load])

  const monthData = useMemo(
    () => monthlyCashFlow(payables, receivables),
    [payables, receivables],
  )
  const statusPie = useMemo(() => payablesByStatus(payables), [payables])
  const transactions = useMemo(
    () => mergeTransactions(payables, receivables).slice(0, 20),
    [payables, receivables],
  )

  const totalAbertoPagar = openPayablesTotal(payables)
  const totalAbertoReceber = openReceivablesTotal(receivables)

  if (!canAccess) {
    return (
      <div className="flex flex-col items-center justify-center rounded-xl bg-white py-16 shadow-sm">
        <Lock className="mb-4 h-12 w-12 text-gray-400" />
        <h2 className="text-lg font-bold text-gray-900">Acesso restrito</h2>
        <p className="mt-2 max-w-md text-center text-sm text-gray-500">
          Relatórios de contabilidade exigem perfil <strong>GESTOR</strong> ou{' '}
          <strong>CONTABIL</strong>.
          <br />
          Ex.: contabil@gestorpyme.local / contabil123
        </p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <h2 className="text-2xl font-bold text-gray-900">Finanças — Contabilidade</h2>
        <button
          type="button"
          onClick={() => exportCsv(payables, receivables, cashFlow)}
          disabled={loading || Boolean(error)}
          className="flex items-center justify-center gap-2 rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 disabled:opacity-50"
        >
          <Download className="h-4 w-4" />
          Exportar relatório
        </button>
      </div>

      <div className="flex flex-wrap items-end gap-3 rounded-xl bg-white p-4 shadow-sm">
        <label className="text-sm font-medium text-gray-600">
          De
          <input
            type="date"
            value={from}
            onChange={(e) => setFrom(e.target.value)}
            className="mt-1 block rounded-lg border border-gray-300 px-3 py-2 text-sm"
          />
        </label>
        <label className="text-sm font-medium text-gray-600">
          Até
          <input
            type="date"
            value={to}
            onChange={(e) => setTo(e.target.value)}
            className="mt-1 block rounded-lg border border-gray-300 px-3 py-2 text-sm"
          />
        </label>
        <button
          type="button"
          onClick={load}
          className="rounded-lg bg-action px-4 py-2 text-sm font-medium text-white hover:bg-action-hover"
        >
          Atualizar
        </button>
      </div>

      {error && (
        <p className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>
      )}

      {loading ? (
        <p className="text-gray-500">Carregando relatórios...</p>
      ) : (
        <>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <SummaryCard
              label="Receita no período"
              value={formatCurrency(cashFlow?.totalEntradas)}
              trend="+"
              trendClass="text-emerald-600"
            />
            <SummaryCard
              label="Despesas no período"
              value={formatCurrency(cashFlow?.totalSaidas)}
              trend="−"
              trendClass="text-red-500"
            />
            <SummaryCard
              label="Saldo do período"
              value={formatCurrency(cashFlow?.saldo)}
              trend={Number(cashFlow?.saldo ?? 0) >= 0 ? '+' : '−'}
              trendClass={
                Number(cashFlow?.saldo ?? 0) >= 0 ? 'text-emerald-600' : 'text-red-500'
              }
            />
            <SummaryCard
              label="A receber (em aberto)"
              value={formatCurrency(totalAbertoReceber)}
              sub={`A pagar: ${formatCurrency(totalAbertoPagar)}`}
            />
          </div>

          <div className="grid gap-6 lg:grid-cols-2">
            <ChartCard title="Fluxo de caixa mensal (realizado)">
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={monthData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#eee" />
                  <XAxis dataKey="month" tick={{ fontSize: 12 }} />
                  <YAxis tick={{ fontSize: 11 }} />
                  <Tooltip formatter={(v) => formatCurrency(Number(v))} />
                  <Legend />
                  <Bar dataKey="receitas" name="Receitas" fill="#48bb78" radius={[4, 4, 0, 0]} />
                  <Bar dataKey="despesas" name="Despesas" fill="#f56565" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </ChartCard>

            <ChartCard title="Contas a pagar por status (R$)">
              {statusPie.length === 0 ? (
                <p className="py-16 text-center text-sm text-gray-400">Sem dados no período.</p>
              ) : (
                <ResponsiveContainer width="100%" height={260}>
                  <PieChart>
                    <Pie
                      data={statusPie}
                      dataKey="valor"
                      nameKey="status"
                      cx="50%"
                      cy="50%"
                      outerRadius={90}
                    >
                      {statusPie.map((_, i) => (
                        <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip formatter={(v) => formatCurrency(Number(v))} />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              )}
            </ChartCard>
          </div>

          <ReportTable
            title="Contas a pagar"
            emptyMessage="Nenhuma conta a pagar no período."
            rows={payables.map((p) => ({
              key: p.id,
              data: p.vencimento,
              descricao: p.descricao,
              valor: p.valor,
              categoria: 'Despesa',
              status: p.status,
            }))}
          />

          <ReportTable
            title="Contas a receber"
            emptyMessage="Nenhuma conta a receber no período."
            rows={receivables.map((r) => ({
              key: r.id,
              data: r.vencimento,
              descricao: r.descricao,
              valor: r.valor,
              categoria: 'Receita',
              status: r.status,
            }))}
          />

          <section className="rounded-xl bg-white p-6 shadow-sm">
            <h3 className="mb-4 text-lg font-bold text-gray-900">
              Últimas transações financeiras
            </h3>
            {transactions.length === 0 ? (
              <p className="py-8 text-center text-sm text-gray-400">
                Nenhuma transação no período selecionado.
              </p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full min-w-[640px] text-left text-sm">
                  <thead>
                    <tr className="border-b border-gray-200 text-gray-500">
                      <th className="pb-3 pr-4 font-medium">Data</th>
                      <th className="pb-3 pr-4 font-medium">Descrição</th>
                      <th className="pb-3 pr-4 font-medium">Valor</th>
                      <th className="pb-3 pr-4 font-medium">Categoria</th>
                      <th className="pb-3 font-medium">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {transactions.map((t, i) => (
                      <tr
                        key={t.id}
                        className={`border-b border-gray-100 ${i % 2 === 1 ? 'bg-gray-50/80' : ''}`}
                      >
                        <td className="py-3 pr-4">{formatDate(t.data)}</td>
                        <td className="py-3 pr-4 font-medium">{t.descricao}</td>
                        <td
                          className={`py-3 pr-4 font-medium ${
                            t.tipo === 'ENTRADA' ? 'text-emerald-600' : 'text-red-600'
                          }`}
                        >
                          {t.tipo === 'SAIDA' ? '−' : '+'}
                          {formatCurrency(t.valor)}
                        </td>
                        <td className="py-3 pr-4 text-gray-600">{t.categoria}</td>
                        <td className="py-3">
                          <span
                            className={`rounded-full px-2 py-0.5 text-xs font-medium ${statusBadgeClass(t.status)}`}
                          >
                            {statusLabel(t.status)}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </>
      )}
    </div>
  )
}

function SummaryCard({
  label,
  value,
  trend,
  trendClass,
  sub,
}: {
  label: string
  value: string
  trend?: string
  trendClass?: string
  sub?: string
}) {
  return (
    <div className="rounded-xl bg-white p-5 shadow-sm">
      <p className="text-sm text-gray-500">{label}</p>
      <div className="mt-1 flex items-baseline gap-2">
        <p className="text-2xl font-bold text-gray-900">{value}</p>
        {trend && (
          <span className={`text-sm font-semibold ${trendClass ?? ''}`}>{trend}</span>
        )}
      </div>
      {sub && <p className="mt-2 text-xs text-gray-500">{sub}</p>}
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

function ReportTable({
  title,
  emptyMessage,
  rows,
}: {
  title: string
  emptyMessage: string
  rows: {
    key: number
    data: string
    descricao: string
    valor: number
    categoria: string
    status: string
  }[]
}) {
  return (
    <section className="rounded-xl bg-white p-6 shadow-sm">
      <h3 className="mb-4 text-lg font-bold text-gray-900">{title}</h3>
      {rows.length === 0 ? (
        <p className="py-6 text-center text-sm text-gray-400">{emptyMessage}</p>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full min-w-[600px] text-left text-sm">
            <thead>
              <tr className="border-b border-gray-200 text-gray-500">
                <th className="pb-3 pr-4 font-medium">Vencimento</th>
                <th className="pb-3 pr-4 font-medium">Descrição</th>
                <th className="pb-3 pr-4 font-medium">Valor</th>
                <th className="pb-3 pr-4 font-medium">Tipo</th>
                <th className="pb-3 font-medium">Status</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((row, i) => (
                <tr
                  key={row.key}
                  className={`border-b border-gray-100 ${i % 2 === 1 ? 'bg-gray-50/80' : ''}`}
                >
                  <td className="py-3 pr-4">{formatDate(row.data)}</td>
                  <td className="py-3 pr-4 font-medium">{row.descricao}</td>
                  <td className="py-3 pr-4">{formatCurrency(row.valor)}</td>
                  <td className="py-3 pr-4 text-gray-600">{row.categoria}</td>
                  <td className="py-3">
                    <span
                      className={`rounded-full px-2 py-0.5 text-xs font-medium ${statusBadgeClass(row.status)}`}
                    >
                      {statusLabel(row.status)}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
