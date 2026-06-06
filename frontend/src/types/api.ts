export type UserRole = 'GESTOR' | 'OPERADOR' | 'ESTOQUE' | 'CONTABIL'

export type MovementType = 'ENTRADA' | 'SAIDA'

export interface UserResponse {
  id: number
  nome: string
  email: string
  perfil: UserRole
}

export interface LoginResponse {
  token: string
  tipo: string
  expiraEm: number
  usuario: UserResponse
}

export interface ProductResponse {
  id: number
  nome: string
  categoria: string
  unidade: string
  precoReferencia: number
  estoqueMinimo: number
  saldoAtual: number
  ativo: boolean
  estoqueBaixo: boolean
}

export interface ProductPageResponse {
  content: ProductResponse[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface StockMovementResponse {
  id: number
  productId: number
  tipo: MovementType
  quantidade: number
  data: string
  observacao: string | null
  createdBy: string | null
  produto: ProductResponse | null
}

export interface DashboardResponse {
  totalProdutos: number
  produtosEstoqueBaixo: number
  totalAPagar: number
  totalAReceber: number
  saldoCaixa: number
}

export interface CreateProductRequest {
  nome: string
  categoria: string
  unidade: string
  precoReferencia: number
  estoqueMinimo: number
  saldoAtual?: number
}

export interface UpdateProductRequest {
  nome: string
  categoria: string
  unidade: string
  precoReferencia: number
  estoqueMinimo: number
}

export interface CreateMovementRequest {
  tipo: MovementType
  quantidade: number
  data: string
  observacao?: string
}

export type PayableStatus = 'PENDENTE' | 'PAGA' | 'VENCIDA'
export type ReceivableStatus = 'PENDENTE' | 'RECEBIDA' | 'VENCIDA'

export interface PayableResponse {
  id: number
  descricao: string
  valor: number
  vencimento: string
  status: PayableStatus
  dataPagamento: string | null
}

export interface ReceivableResponse {
  id: number
  descricao: string
  valor: number
  vencimento: string
  status: ReceivableStatus
  dataRecebimento: string | null
}

export interface CashFlowResponse {
  from: string
  to: string
  totalEntradas: number
  totalSaidas: number
  saldo: number
}

export interface ApiError {
  message?: string
  status?: number
}
