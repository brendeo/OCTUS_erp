INSERT INTO products (nome, categoria, unidade, preco_referencia, estoque_minimo, saldo_atual) VALUES
('Arroz 5kg', 'Mercearia', 'UN', 28.90, 5, 3),
('Feijão 1kg', 'Mercearia', 'UN', 8.50, 10, 25),
('Óleo de Soja 900ml', 'Mercearia', 'UN', 7.99, 8, 6),
('Café 500g', 'Mercearia', 'UN', 15.90, 4, 2),
('Açúcar 1kg', 'Mercearia', 'UN', 4.50, 10, 30),
('Leite Integral 1L', 'Laticínios', 'UN', 5.49, 12, 8),
('Detergente 500ml', 'Limpeza', 'UN', 2.99, 6, 4),
('Papel Higiênico 12un', 'Higiene', 'CX', 22.90, 3, 15);

INSERT INTO stock_movements (product_id, tipo, quantidade, data_movimento, observacao, created_by) VALUES
(1, 'ENTRADA', 20, '2026-05-01', 'Compra inicial', 'admin'),
(1, 'SAIDA', 17, '2026-05-20', 'Vendas do mês', 'admin'),
(4, 'ENTRADA', 10, '2026-05-05', 'Reposição', 'admin'),
(4, 'SAIDA', 8, '2026-05-25', 'Vendas', 'admin');

INSERT INTO payables (descricao, valor, vencimento, status, data_pagamento) VALUES
('Aluguel do ponto', 2500.00, '2026-05-10', 'PAGA', '2026-05-09'),
('Energia elétrica', 450.00, '2026-05-15', 'VENCIDA', NULL),
('Fornecedor mercearia', 1800.00, '2026-06-10', 'PENDENTE', NULL),
('Internet', 120.00, '2026-06-05', 'PENDENTE', NULL),
('Água', 85.00, '2026-04-20', 'VENCIDA', NULL);

INSERT INTO receivables (descricao, valor, vencimento, status, data_recebimento) VALUES
('Venda cliente ABC', 1800.00, '2026-05-20', 'RECEBIDA', '2026-05-18'),
('Venda cliente XYZ', 950.00, '2026-05-25', 'RECEBIDA', '2026-05-24'),
('Pedido corporativo', 3200.00, '2026-06-15', 'PENDENTE', NULL),
('Serviço entrega', 350.00, '2026-04-30', 'VENCIDA', NULL),
('Venda balcão', 200.00, '2026-06-01', 'PENDENTE', NULL);
