package com.gestorpyme.mock;

import com.gestorpyme.domain.entity.*;
import com.gestorpyme.domain.enums.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Profile("mock")
public class MockDataLoader {

    private final MockDataStore store;
    private final PasswordEncoder passwordEncoder;

    public MockDataLoader(MockDataStore store, PasswordEncoder passwordEncoder) {
        this.store = store;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void load() {
        if (!store.getProducts().isEmpty()) {
            return;
        }
        seedUsers();
        seedProducts();
        seedMovements();
        seedPayables();
        seedReceivables();
    }

    private void seedUsers() {
        UserEntity admin = new UserEntity();
        admin.setId(store.nextUserId());
        admin.setNome("Administrador");
        admin.setEmail("admin@gestorpyme.local");
        admin.setSenhaHash(passwordEncoder.encode("admin123"));
        admin.setPerfil(UserRole.GESTOR);
        store.getUsers().put(admin.getId(), admin);
    }

    private void seedProducts() {
        addProduct("Arroz 5kg", "Mercearia", "UN", "28.90", 5, 3);
        addProduct("Feijão 1kg", "Mercearia", "UN", "8.50", 10, 25);
        addProduct("Óleo de Soja 900ml", "Mercearia", "UN", "7.99", 8, 6);
        addProduct("Café 500g", "Mercearia", "UN", "15.90", 4, 2);
        addProduct("Açúcar 1kg", "Mercearia", "UN", "4.50", 10, 30);
        addProduct("Leite Integral 1L", "Laticínios", "UN", "5.49", 12, 8);
        addProduct("Detergente 500ml", "Limpeza", "UN", "2.99", 6, 4);
        addProduct("Papel Higiênico 12un", "Higiene", "CX", "22.90", 3, 15);
    }

    private void addProduct(String nome, String cat, String un, String preco, int min, int saldo) {
        ProductEntity p = new ProductEntity();
        p.setId(store.nextProductId());
        p.setNome(nome);
        p.setCategoria(cat);
        p.setUnidade(un);
        p.setPrecoReferencia(new BigDecimal(preco));
        p.setEstoqueMinimo(min);
        p.setSaldoAtual(saldo);
        p.setAtivo(true);
        store.getProducts().put(p.getId(), p);
    }

    private void seedMovements() {
        addMovement(1L, MovementType.ENTRADA, 20, LocalDate.of(2026, 5, 1), "Compra inicial");
        addMovement(1L, MovementType.SAIDA, 17, LocalDate.of(2026, 5, 20), "Vendas do mês");
        addMovement(4L, MovementType.ENTRADA, 10, LocalDate.of(2026, 5, 5), "Reposição");
        addMovement(4L, MovementType.SAIDA, 8, LocalDate.of(2026, 5, 25), "Vendas");
    }

    private void addMovement(Long productId, MovementType tipo, int qtd, LocalDate data, String obs) {
        ProductEntity product = store.getProducts().get(productId);
        if (product == null) return;
        StockMovementEntity m = new StockMovementEntity();
        m.setId(store.nextMovementId());
        m.setProduct(product);
        m.setTipo(tipo);
        m.setQuantidade(qtd);
        m.setDataMovimento(data);
        m.setObservacao(obs);
        m.setCreatedBy(MockDataStore.DEFAULT_USER);
        store.getMovements().put(m.getId(), m);
    }

    private void seedPayables() {
        addPayable("Aluguel do ponto", "2500.00", LocalDate.of(2026, 5, 10), PayableStatus.PAGA, LocalDate.of(2026, 5, 9));
        addPayable("Energia elétrica", "450.00", LocalDate.of(2026, 5, 15), PayableStatus.VENCIDA, null);
        addPayable("Fornecedor mercearia", "1800.00", LocalDate.of(2026, 6, 10), PayableStatus.PENDENTE, null);
        addPayable("Internet", "120.00", LocalDate.of(2026, 6, 5), PayableStatus.PENDENTE, null);
        addPayable("Água", "85.00", LocalDate.of(2026, 4, 20), PayableStatus.VENCIDA, null);
    }

    private void addPayable(String desc, String valor, LocalDate venc, PayableStatus status, LocalDate pago) {
        PayableEntity e = new PayableEntity();
        e.setId(store.nextPayableId());
        e.setDescricao(desc);
        e.setValor(new BigDecimal(valor));
        e.setVencimento(venc);
        e.setStatus(status);
        e.setDataPagamento(pago);
        store.getPayables().put(e.getId(), e);
    }

    private void seedReceivables() {
        addReceivable("Venda cliente ABC", "1800.00", LocalDate.of(2026, 5, 20), ReceivableStatus.RECEBIDA, LocalDate.of(2026, 5, 18));
        addReceivable("Venda cliente XYZ", "950.00", LocalDate.of(2026, 5, 25), ReceivableStatus.RECEBIDA, LocalDate.of(2026, 5, 24));
        addReceivable("Pedido corporativo", "3200.00", LocalDate.of(2026, 6, 15), ReceivableStatus.PENDENTE, null);
        addReceivable("Serviço entrega", "350.00", LocalDate.of(2026, 4, 30), ReceivableStatus.VENCIDA, null);
        addReceivable("Venda balcão", "200.00", LocalDate.of(2026, 6, 1), ReceivableStatus.PENDENTE, null);
    }

    private void addReceivable(String desc, String valor, LocalDate venc, ReceivableStatus status, LocalDate rec) {
        ReceivableEntity e = new ReceivableEntity();
        e.setId(store.nextReceivableId());
        e.setDescricao(desc);
        e.setValor(new BigDecimal(valor));
        e.setVencimento(venc);
        e.setStatus(status);
        e.setDataRecebimento(rec);
        store.getReceivables().put(e.getId(), e);
    }
}
