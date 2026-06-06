import type { ProductResponse, StockMovementResponse } from '../types/api'

const MONTHS = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez']

export interface MonthValue {
  month: string
  entradas: number
  saidas: number
}

export interface CategoryValue {
  categoria: string
  valor: number
}

export interface TopProduct {
  nome: string
  valor: number
  categoria: string
}

function movementValue(m: StockMovementResponse): number {
  const price = m.produto?.precoReferencia ?? 0
  return m.quantidade * price
}

export function valuesByMonth(movements: StockMovementResponse[]): MonthValue[] {
  const map = new Map<number, { entradas: number; saidas: number }>()
  for (const m of movements) {
    const month = Number(m.data.slice(5, 7)) - 1
    const row = map.get(month) ?? { entradas: 0, saidas: 0 }
    const val = movementValue(m)
    if (m.tipo === 'ENTRADA') row.entradas += val
    else row.saidas += val
    map.set(month, row)
  }
  return MONTHS.map((month, i) => ({
    month,
    entradas: map.get(i)?.entradas ?? 0,
    saidas: map.get(i)?.saidas ?? 0,
  }))
}

export function valuesByYear(movements: StockMovementResponse[]): { year: string; valor: number }[] {
  const map = new Map<string, number>()
  for (const m of movements) {
    const year = m.data.slice(0, 4)
    map.set(year, (map.get(year) ?? 0) + movementValue(m))
  }
  return [...map.entries()]
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([year, valor]) => ({ year, valor }))
}

export function valuesByCategory(products: ProductResponse[]): CategoryValue[] {
  const map = new Map<string, number>()
  for (const p of products) {
    if (!p.ativo) continue
    const val = p.saldoAtual * p.precoReferencia
    map.set(p.categoria, (map.get(p.categoria) ?? 0) + val)
  }
  return [...map.entries()]
    .map(([categoria, valor]) => ({ categoria, valor }))
    .sort((a, b) => b.valor - a.valor)
}

export function topProfitableProducts(products: ProductResponse[]): TopProduct[] {
  return products
    .filter((p) => p.ativo)
    .map((p) => ({
      nome: p.nome,
      categoria: p.categoria,
      valor: p.saldoAtual * p.precoReferencia,
    }))
    .sort((a, b) => b.valor - a.valor)
    .slice(0, 8)
}
