import { clearAuth, getToken } from './auth'
import type {
  CashFlowResponse,
  CreateMovementRequest,
  CreateProductRequest,
  DashboardResponse,
  LoginResponse,
  PayableResponse,
  PayableStatus,
  ProductPageResponse,
  ProductResponse,
  ReceivableResponse,
  ReceivableStatus,
  StockMovementResponse,
  UpdateProductRequest,
  UserResponse,
} from '../types/api'

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export class ApiClientError extends Error {
  status: number

  constructor(message: string, status: number) {
    super(message)
    this.status = status
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  if (!headers.has('Content-Type') && options.body) {
    headers.set('Content-Type', 'application/json')
  }
  const token = getToken()
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const res = await fetch(`${API_URL}${path}`, { ...options, headers })

  if (res.status === 401) {
    clearAuth()
    window.location.href = '/login'
    throw new ApiClientError('Sessão expirada', 401)
  }

  if (!res.ok) {
    let message = res.statusText
    try {
      const body = (await res.json()) as { message?: string }
      if (body.message) message = body.message
    } catch {
      /* ignore */
    }
    throw new ApiClientError(message, res.status)
  }

  if (res.status === 204) return undefined as T
  return res.json() as Promise<T>
}

export const api = {
  login(email: string, senha: string) {
    return request<LoginResponse>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, senha }),
    })
  },

  me() {
    return request<UserResponse>('/api/auth/me')
  },

  dashboard() {
    return request<DashboardResponse>('/api/dashboard')
  },

  listProducts(params?: {
    search?: string
    category?: string
    page?: number
    size?: number
  }) {
    const q = new URLSearchParams()
    if (params?.search) q.set('search', params.search)
    if (params?.category) q.set('category', params.category)
    if (params?.page != null) q.set('page', String(params.page))
    if (params?.size != null) q.set('size', String(params.size))
    const qs = q.toString()
    return request<ProductPageResponse>(`/api/products${qs ? `?${qs}` : ''}`)
  },

  getProduct(id: number) {
    return request<ProductResponse>(`/api/products/${id}`)
  },

  createProduct(body: CreateProductRequest) {
    return request<ProductResponse>('/api/products', {
      method: 'POST',
      body: JSON.stringify(body),
    })
  },

  updateProduct(id: number, body: UpdateProductRequest) {
    return request<ProductResponse>(`/api/products/${id}`, {
      method: 'PUT',
      body: JSON.stringify(body),
    })
  },

  deactivateProduct(id: number) {
    return request<ProductResponse>(`/api/products/${id}/deactivate`, {
      method: 'PATCH',
    })
  },

  productMovements(productId: number) {
    return request<StockMovementResponse[]>(`/api/products/${productId}/movements`)
  },

  createMovement(productId: number, body: CreateMovementRequest) {
    return request<StockMovementResponse>(`/api/products/${productId}/movements`, {
      method: 'POST',
      body: JSON.stringify(body),
    })
  },

  reportStockMovements(from?: string, to?: string) {
    const q = new URLSearchParams()
    if (from) q.set('from', from)
    if (to) q.set('to', to)
    const qs = q.toString()
    return request<StockMovementResponse[]>(
      `/api/reports/stock-movements${qs ? `?${qs}` : ''}`,
    )
  },

  reportPayables(params?: { from?: string; to?: string; status?: PayableStatus }) {
    const q = new URLSearchParams()
    if (params?.from) q.set('from', params.from)
    if (params?.to) q.set('to', params.to)
    if (params?.status) q.set('status', params.status)
    const qs = q.toString()
    return request<PayableResponse[]>(`/api/reports/payables${qs ? `?${qs}` : ''}`)
  },

  reportReceivables(params?: { from?: string; to?: string; status?: ReceivableStatus }) {
    const q = new URLSearchParams()
    if (params?.from) q.set('from', params.from)
    if (params?.to) q.set('to', params.to)
    if (params?.status) q.set('status', params.status)
    const qs = q.toString()
    return request<ReceivableResponse[]>(`/api/reports/receivables${qs ? `?${qs}` : ''}`)
  },

  reportCashFlow(from?: string, to?: string) {
    const q = new URLSearchParams()
    if (from) q.set('from', from)
    if (to) q.set('to', to)
    const qs = q.toString()
    return request<CashFlowResponse>(`/api/reports/cash-flow${qs ? `?${qs}` : ''}`)
  },
}

export async function fetchAllMovements(): Promise<StockMovementResponse[]> {
  try {
    return await api.reportStockMovements()
  } catch {
    const page = await api.listProducts({ size: 200 })
    const lists = await Promise.all(
      page.content.map((p) => api.productMovements(p.id).catch(() => [])),
    )
    return lists
      .flat()
      .sort((a, b) => b.data.localeCompare(a.data))
  }
}

export function getApiUrl(): string {
  return API_URL
}
