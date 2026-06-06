package com.gestorpyme.service.jpa;

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
import com.gestorpyme.repository.ProductRepository;
import com.gestorpyme.security.CurrentUserProvider;
import com.gestorpyme.service.AuditService;
import com.gestorpyme.repository.StockMovementRepository;
import com.gestorpyme.service.ProductService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Profile({"dev", "prod", "test"})
public class JpaProductService implements ProductService {

    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;
    private final CurrentUserProvider currentUserProvider;
    private final AuditService auditService;

    public JpaProductService(ProductRepository productRepository, StockMovementRepository movementRepository,
                             CurrentUserProvider currentUserProvider, AuditService auditService) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
        this.currentUserProvider = currentUserProvider;
        this.auditService = auditService;
    }

    @Override
    public ProductPageResponse list(String search, String category, Boolean active, int page, int size) {
        List<ProductResponse> filtered = productRepository.findAll().stream()
                .filter(p -> search == null || search.isBlank() || p.getNome().toLowerCase().contains(search.toLowerCase()))
                .filter(p -> category == null || category.isBlank() || p.getCategoria().equalsIgnoreCase(category))
                .filter(p -> active == null || p.isAtivo() == active)
                .sorted(Comparator.comparing(ProductEntity::getNome))
                .map(EntityMapper::toProductResponse)
                .toList();
        int total = filtered.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 1;
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        return new ProductPageResponse(filtered.subList(from, to), page, size, total, totalPages);
    }

    @Override
    public ProductResponse getById(Long id) {
        return EntityMapper.toProductResponse(findProduct(id));
    }

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        ProductEntity p = new ProductEntity();
        p.setNome(request.nome());
        p.setCategoria(request.categoria());
        p.setUnidade(request.unidade());
        p.setPrecoReferencia(request.precoReferencia());
        p.setEstoqueMinimo(request.estoqueMinimo());
        p.setSaldoAtual(request.saldoAtual());
        p.setAtivo(true);
        ProductEntity saved = productRepository.save(p);
        auditService.log("CREATE", "Product", saved.getId(), saved.getNome());
        return EntityMapper.toProductResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        ProductEntity p = findProduct(id);
        p.setNome(request.nome());
        p.setCategoria(request.categoria());
        p.setUnidade(request.unidade());
        p.setPrecoReferencia(request.precoReferencia());
        p.setEstoqueMinimo(request.estoqueMinimo());
        ProductEntity saved = productRepository.save(p);
        auditService.log("UPDATE", "Product", saved.getId(), saved.getNome());
        return EntityMapper.toProductResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse deactivate(Long id) {
        ProductEntity p = findProduct(id);
        p.setAtivo(false);
        ProductEntity saved = productRepository.save(p);
        auditService.log("DEACTIVATE", "Product", saved.getId(), saved.getNome());
        return EntityMapper.toProductResponse(saved);
    }

    @Override
    public List<ProductResponse> lowStockAlerts() {
        return productRepository.findAll().stream()
                .filter(p -> p.isAtivo() && p.getSaldoAtual() <= p.getEstoqueMinimo())
                .map(EntityMapper::toProductResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponse> listMovements(Long productId, LocalDate from, LocalDate to, MovementType type) {
        findProduct(productId);
        return movementRepository.findByProductIdOrderByDataMovimentoDesc(productId).stream()
                .filter(m -> from == null || !m.getDataMovimento().isBefore(from))
                .filter(m -> to == null || !m.getDataMovimento().isAfter(to))
                .filter(m -> type == null || m.getTipo() == type)
                .map(EntityMapper::toMovementResponse)
                .toList();
    }

    @Override
    @Transactional
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
        productRepository.save(product);

        StockMovementEntity m = new StockMovementEntity();
        m.setProduct(product);
        m.setTipo(request.tipo());
        m.setQuantidade(request.quantidade());
        m.setDataMovimento(request.data());
        m.setObservacao(request.observacao());
        m.setCreatedBy(currentUserProvider.getCreatedByLabel());
        StockMovementEntity saved = movementRepository.save(m);
        auditService.log("CREATE", "StockMovement", saved.getId(),
                "productId=" + productId + ", tipo=" + request.tipo() + ", qtd=" + request.quantidade());
        return EntityMapper.toMovementResponse(saved);
    }

    @Override
    public List<StockMovementResponse> allMovements(LocalDate from, LocalDate to) {
        LocalDate start = from != null ? from : LocalDate.of(2000, 1, 1);
        LocalDate end = to != null ? to : LocalDate.of(2100, 12, 31);
        return movementRepository.findByDataMovimentoBetween(start, end).stream()
                .map(EntityMapper::toMovementResponse)
                .toList();
    }

    private ProductEntity findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }
}
