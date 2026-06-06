CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    acao VARCHAR(50) NOT NULL,
    entidade VARCHAR(80) NOT NULL,
    entidade_id BIGINT,
    usuario VARCHAR(180) NOT NULL,
    detalhes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_entidade ON audit_logs(entidade, entidade_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
