CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    unidade VARCHAR(20) NOT NULL,
    preco_referencia DECIMAL(12, 2) NOT NULL,
    estoque_minimo INT NOT NULL,
    saldo_atual INT NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE stock_movements (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    tipo VARCHAR(10) NOT NULL,
    quantidade INT NOT NULL,
    data_movimento DATE NOT NULL,
    observacao VARCHAR(500),
    created_by VARCHAR(80) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payables (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(300) NOT NULL,
    valor DECIMAL(12, 2) NOT NULL,
    vencimento DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_pagamento DATE
);

CREATE TABLE receivables (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(300) NOT NULL,
    valor DECIMAL(12, 2) NOT NULL,
    vencimento DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_recebimento DATE
);

CREATE INDEX idx_products_categoria ON products(categoria);
CREATE INDEX idx_stock_movements_product ON stock_movements(product_id);
CREATE INDEX idx_payables_vencimento ON payables(vencimento);
CREATE INDEX idx_receivables_vencimento ON receivables(vencimento);
