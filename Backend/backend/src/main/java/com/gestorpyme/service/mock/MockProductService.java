package com.gestorpyme.service.mock;

import com.gestorpyme.domain.entity.ProductEntity;
import com.gestorpyme.domain.entity.StockMovementEntity;
import com.gestorpyme.domain.enums.MovementType;
import com.gestorpyme.dto.request.CreateMovementRequest;
import com.gestorpyme.dto.request.CreateProductRequest;
import com.gestorpyme.dto.request.UpdateProductRequest;
import com.gestorpyme.dto.response.ProductPageResponse;
import com.gestorpyme.dto.response.ProductResponse;
import com.gestorpyme.dto.response.StockMovementResponse;
import com.gestorpyme.exception.BusinessException;
import com.gestorpyme.exception.ResourceNotFoundException;
import com.gestorpyme.mapper.EntityMapper;
import com.gestorpyme.mock.MockDataStore;
import com.gestorpyme.security.CurrentUserProvider;
import com.gestorpyme.service.ProductService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@Profile("mock")
public class MockProductService implements ProductService {

    private final MockDataStore store;
    private final CurrentUserProvider currentUserProvider;

    public MockProductService(MockDataStore store, CurrentUserProvider currentUserProvider) {
        this.store = store;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public ProductPageResponse list(String search, String category, Boolean active, int page, int size) {
        Stream<ProductEntity> stream = store.getProducts().values().stream();
        if (search != null && !search.isBlank()) {
            String s = search.toLowerCase();
            stream = stream.filter(p -> p.getNome().toLowerCase().contains(s));
        }
        if (category != null && !category.isBlank()) {
            stream = stream.filter(p -> p.getCategoria().equalsIgnoreCase(category));
        }
        if (active != null) {
            stream = stream.filter(p -> p.isAtivo() == active);
        }
        List<ProductResponse> all = stream
                .sorted(Comparator.comparing(ProductEntity::getNome))
                .map(EntityMapper::toProductResponse)
                .toList();
        int total = all.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 1;
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        return new ProductPageResponse(all.subList(from, to), page, size, total, totalPages);
    }

    @Override
    public ProductResponse getById(Long id) {
        return EntityMapper.toProductResponse(findProduct(id));
    }

    @Override
    public ProductResponse create(CreateProductRequest request) {
        ProductEntity p = new ProductEntity();
        p.setId(store.nextProductId());
        p.setNome(request.nome());
        p.setCategoria(request.categoria());
        p.setUnidade(request.unidade());
        p.setPrecoReferencia(request.precoReferencia());
        p.setEstoqueMinimo(request.estoqueMinimo());
        p.setSaldoAtual(request.saldoAtual());
        p.setAtivo(true);
        store.getProducts().put(p.getId(), p);
        return EntityMapper.toProductResponse(p);
    }

    @Override
    public ProductResponse update(Long id, UpdateProductRequest request) {
        ProductEntity p = findProduct(id);
        p.setNome(request.nome());
        p.setCategoria(request.categoria());
        p.setUnidade(request.unidade());
        p.setPrecoReferencia(request.precoReferencia());
        p.setEstoqueMinimo(request.estoqueMinimo());
        return EntityMapper.toProductResponse(p);
    }

    @Override
    public ProductResponse deactivate(Long id) {
        ProductEntity p = findProduct(id);
        p.setAtivo(false);
        return EntityMapper.toProductResponse(p);
    }

    @Override
    public List<ProductResponse> lowStockAlerts() {
        return store.getProducts().values().stream()
                .filter(p -> p.isAtivo() && p.getSaldoAtual() <= p.getEstoqueMinimo())
                .map(EntityMapper::toProductResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponse> listMovements(Long productId, LocalDate from, LocalDate to, MovementType type) {
        findProduct(productId);
        return store.getMovements().values().stream()
                .filter(m -> m.getProduct().getId().equals(productId))
                .filter(m -> from == null || !m.getDataMovimento().isBefore(from))
                .filter(m -> to == null || !m.getDataMovimento().isAfter(to))
                .filter(m -> type == null || m.getTipo() == type)
                .sorted(Comparator.comparing(StockMovementEntity::getDataMovimento).reversed())
                .map(EntityMapper::toMovementResponse)
                .toList();
    }

    @Override
    public StockMovementResponse createMovement(Long productId, CreateMovementRequest request) {
        ProductEntity product = findProduct(productId);
        if (!product.isAtivo()) {
            throw new BusinessException("Produto inativo não permite movimentação");
        }
        if (request.tipo() == MovementType.SAIDA && product.getSaldoAtual() < request.quantidade()) {
            throw new BusinessException("Saldo insuficiente para saída");
        }
        int delta = request.tipo() == MovementType.ENTRADA ? request.quantidade() : -request.quantidade();
        product.setSaldoAtual(product.getSaldoAtual() + delta);

        StockMovementEntity m = new StockMovementEntity();
        m.setId(store.nextMovementId());
        m.setProduct(product);
        m.setTipo(request.tipo());
        m.setQuantidade(request.quantidade());
        m.setDataMovimento(request.data());
        m.setObservacao(request.observacao());
        m.setCreatedBy(currentUserProvider.getCreatedByLabel());
        store.getMovements().put(m.getId(), m);
        return EntityMapper.toMovementResponse(m);
    }

    @Override
    public List<StockMovementResponse> allMovements(LocalDate from, LocalDate to) {
        return store.getMovements().values().stream()
                .filter(m -> from == null || !m.getDataMovimento().isBefore(from))
                .filter(m -> to == null || !m.getDataMovimento().isAfter(to))
                .sorted(Comparator.comparing(StockMovementEntity::getDataMovimento).reversed())
                .map(EntityMapper::toMovementResponse)
                .toList();
    }

    private ProductEntity findProduct(Long id) {
        ProductEntity p = store.getProducts().get(id);
        if (p == null) {
            throw new ResourceNotFoundException("Produto não encontrado: " + id);
        }
        return p;
    }
}
