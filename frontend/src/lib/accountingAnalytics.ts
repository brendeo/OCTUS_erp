import type { PayableResponse, ReceivableResponse } from '../types/api'

const MONTHS = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez']

export interface MonthFlow {
  month: string
  receitas: number
  despesas: number
}

export function monthlyCashFlow(
  payables: PayableResponse[],
  receivables: ReceivableResponse[],
): MonthFlow[] {
  const map = new Map<number, { receitas: number; despesas: number }>()

  for (const r of receivables) {
    if (r.status !== 'RECEBIDA' || !r.dataRecebimento) continue
    const month = Number(r.dataRecebimento.slice(5, 7)) - 1
    const row = map.get(month) ?? { receitas: 0, despesas: 0 }
    row.receitas += r.valor
    map.set(month, row)
  }

  for (const p of payables) {
    if (p.status !== 'PAGA' || !p.dataPagamento) continue
    const month = Number(p.dataPagamento.slice(5, 7)) - 1
    const row = map.get(month) ?? { receitas: 0, despesas: 0 }
    row.despesas += p.valor
    map.set(month, row)
  }

  return MONTHS.map((month, i) => ({
    month,
    receitas: map.get(i)?.receitas ?? 0,
    despesas: map.get(i)?.despesas ?? 0,
  }))
}

export function payablesByStatus(payables: PayableResponse[]) {
  const counts = { PENDENTE: 0, PAGA: 0, VENCIDA: 0 }
  const values = { PENDENTE: 0, PAGA: 0, VENCIDA: 0 }
  for (const p of payables) {
    counts[p.status] += 1
    values[p.status] += p.valor
  }
  return [
    { status: 'Pendente', valor: values.PENDENTE, qtd: counts.PENDENTE },
    { status: 'Paga', valor: values.PAGA, qtd: counts.PAGA },
    { status: 'Vencida', valor: values.VENCIDA, qtd: counts.VENCIDA },
  ].filter((x) => x.valor > 0 || x.qtd > 0)
}

export function openPayablesTotal(payables: PayableResponse[]): number {
  return payables
    .filter((p) => p.status === 'PENDENTE' || p.status === 'VENCIDA')
    .reduce((s, p) => s + p.valor, 0)
}

export function openReceivablesTotal(receivables: ReceivableResponse[]): number {
  return receivables
    .filter((r) => r.status === 'PENDENTE' || r.status === 'VENCIDA')
    .reduce((s, r) => s + r.valor, 0)
}

export interface FinanceTransaction {
  id: string
  data: string
  descricao: string
  valor: number
  categoria: string
  status: string
  tipo: 'ENTRADA' | 'SAIDA'
}

export function mergeTransactions(
  payables: PayableResponse[],
  receivables: ReceivableResponse[],
): FinanceTransaction[] {
  const rows: FinanceTransaction[] = []

  for (const p of payables) {
    rows.push({
      id: `p-${p.id}`,
      data: p.dataPagamento ?? p.vencimento,
      descricao: p.descricao,
      valor: p.valor,
      categoria: 'Contas a pagar',
      status: p.status,
      tipo: 'SAIDA',
    })
  }

  for (const r of receivables) {
    rows.push({
      id: `r-${r.id}`,
      data: r.dataRecebimento ?? r.vencimento,
      descricao: r.descricao,
      valor: r.valor,
      categoria: 'Contas a receber',
      status: r.status,
      tipo: 'ENTRADA',
    })
  }

  return rows.sort((a, b) => b.data.localeCompare(a.data))
}
