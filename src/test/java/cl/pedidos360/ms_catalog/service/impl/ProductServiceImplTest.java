package cl.pedidos360.ms_catalog.service.impl;

import cl.pedidos360.ms_catalog.dto.request.ProductCreateRequest;
import cl.pedidos360.ms_catalog.dto.request.ProductPriceUpdateRequest;
import cl.pedidos360.ms_catalog.dto.request.ProductRestockRequest;
import cl.pedidos360.ms_catalog.dto.request.ProductStockUpdateRequest;
import cl.pedidos360.ms_catalog.entity.CategoryEntity;
import cl.pedidos360.ms_catalog.entity.PriceHistoryEntity;
import cl.pedidos360.ms_catalog.entity.ProductEntity;
import cl.pedidos360.ms_catalog.entity.StockMovementEntity;
import cl.pedidos360.ms_catalog.enums.StockAdjustmentOperation;
import cl.pedidos360.ms_catalog.enums.StockMovementType;
import cl.pedidos360.ms_catalog.exception.InsufficientStockException;
import cl.pedidos360.ms_catalog.exception.InvalidOperationException;
import cl.pedidos360.ms_catalog.exception.ResourceAlreadyExistsException;
import cl.pedidos360.ms_catalog.exception.ResourceNotFoundException;
import cl.pedidos360.ms_catalog.mapper.PriceHistoryMapper;
import cl.pedidos360.ms_catalog.mapper.ProductMapper;
import cl.pedidos360.ms_catalog.mapper.StockMovementMapper;
import cl.pedidos360.ms_catalog.observability.CorrelationIdProvider;
import cl.pedidos360.ms_catalog.repository.CategoryRepository;
import cl.pedidos360.ms_catalog.repository.PriceHistoryRepository;
import cl.pedidos360.ms_catalog.repository.ProductRepository;
import cl.pedidos360.ms_catalog.repository.StockMovementRepository;
import cl.pedidos360.ms_catalog.security.CurrentUserProvider;
import cl.pedidos360.ms_catalog.dto.response.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private StockMovementMapper stockMovementMapper;

    @Mock
    private PriceHistoryMapper priceHistoryMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private CorrelationIdProvider correlationIdProvider;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID productId;
    private UUID categoryId;
    private UUID userId;
    private UUID correlationId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        userId = UUID.randomUUID();
        correlationId = UUID.randomUUID();

        lenient()
                .when(currentUserProvider.getCurrentUserId())
                .thenReturn(userId);

        lenient()
                .when(correlationIdProvider.getCorrelationId())
                .thenReturn(correlationId);
    }

    @Test
    void create_shouldCreateProductAndInitialStockMovement() {
        ProductCreateRequest request = new ProductCreateRequest(
                "SKU-001",
                "Teclado mecánico",
                "Teclado de prueba",
                new BigDecimal("49990"),
                10,
                categoryId
        );

        ProductEntity product = mock(ProductEntity.class);
        ProductResponse expectedResponse = mock(ProductResponse.class);
        StockMovementEntity movement = mock(StockMovementEntity.class);

        when(productRepository.existsBySku("SKU-001"))
                .thenReturn(false);

        when(productMapper.toEntity(request))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(product);

        when(product.getId())
                .thenReturn(productId);

        when(product.getSku())
                .thenReturn("SKU-001");

        when(product.getStock())
                .thenReturn(10);

        when(stockMovementMapper.toEntity(
                productId,
                StockMovementType.INITIAL_LOAD,
                10,
                0,
                10,
                "Carga inicial de stock.",
                correlationId
        )).thenReturn(movement);

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);

        ProductResponse response = productService.create(request);

        assertSame(expectedResponse, response);

        verify(productRepository).save(product);

        verify(stockMovementRepository).save(movement);

        verify(product).setCategoryId(categoryId);
    }

    @Test
    void create_shouldAssignDefaultCategoryWhenCategoryIsNull() {
        ProductCreateRequest request = new ProductCreateRequest(
                "SKU-002",
                "Mouse",
                "Mouse de prueba",
                new BigDecimal("12990"),
                0,
                null
        );

        ProductEntity product = mock(ProductEntity.class);
        CategoryEntity defaultCategory = mock(CategoryEntity.class);
        ProductResponse expectedResponse = mock(ProductResponse.class);

        when(productRepository.existsBySku("SKU-002"))
                .thenReturn(false);

        when(productMapper.toEntity(request))
                .thenReturn(product);

        when(categoryRepository.findByNameIgnoreCase("OTROS"))
                .thenReturn(Optional.of(defaultCategory));

        when(defaultCategory.isActive())
                .thenReturn(true);

        when(defaultCategory.getId())
                .thenReturn(categoryId);

        when(productRepository.save(product))
                .thenReturn(product);

        when(product.getStock())
                .thenReturn(0);

        when(product.getId())
                .thenReturn(productId);

        when(product.getSku())
                .thenReturn("SKU-002");

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);

        ProductResponse response = productService.create(request);

        assertSame(expectedResponse, response);

        verify(product).setCategoryId(categoryId);

        verify(stockMovementRepository, never())
                .save(any(StockMovementEntity.class));
    }

    @Test
    void create_shouldThrowWhenSkuAlreadyExists() {
        ProductCreateRequest request = new ProductCreateRequest(
                "SKU-003",
                "Monitor",
                "Monitor de prueba",
                new BigDecimal("159990"),
                0,
                categoryId
        );

        when(productRepository.existsBySku("SKU-003"))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> productService.create(request)
        );

        verify(productRepository, never())
                .save(any(ProductEntity.class));

        verifyNoInteractions(productMapper);
    }

    @Test
    void create_shouldThrowWhenDefaultCategoryDoesNotExist() {
        ProductCreateRequest request = new ProductCreateRequest(
                "SKU-004",
                "Audífonos",
                "Audífonos de prueba",
                new BigDecimal("29990"),
                0,
                null
        );

        ProductEntity product = mock(ProductEntity.class);

        when(productRepository.existsBySku("SKU-004"))
                .thenReturn(false);

        when(productMapper.toEntity(request))
                .thenReturn(product);

        when(categoryRepository.findByNameIgnoreCase("OTROS"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.create(request)
        );

        verify(productRepository, never())
                .save(any(ProductEntity.class));
    }

    @Test
    void findById_shouldReturnProductWhenItExists() {
        ProductEntity product = mock(ProductEntity.class);
        ProductResponse expectedResponse = mock(ProductResponse.class);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);

        ProductResponse response = productService.findById(productId);

        assertSame(expectedResponse, response);

        verify(productRepository).findById(productId);
    }

    @Test
    void findById_shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findById(productId)
        );

        verify(productMapper, never())
                .toResponse(any(ProductEntity.class));
    }

    @Test
    void updatePrice_shouldSaveProductAndPriceHistory() {
        ProductEntity product = mock(ProductEntity.class);
        ProductResponse expectedResponse = mock(ProductResponse.class);
        PriceHistoryEntity history = mock(PriceHistoryEntity.class);

        ProductPriceUpdateRequest request =
                mock(ProductPriceUpdateRequest.class);

        BigDecimal previousPrice = new BigDecimal("10000");
        BigDecimal newPrice = new BigDecimal("12000");

        when(request.price()).thenReturn(newPrice);
        when(request.reason()).thenReturn("Actualización de precio");

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(product.getPrice()).thenReturn(previousPrice);
        when(product.getId()).thenReturn(productId);

        when(productRepository.save(product))
                .thenReturn(product);

        when(priceHistoryMapper.toEntity(
                productId,
                previousPrice,
                newPrice,
                "Actualización de precio",
                correlationId
        )).thenReturn(history);

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);

        ProductResponse response =
                productService.updatePrice(productId, request);

        assertSame(expectedResponse, response);

        verify(product).setPrice(newPrice);
        verify(productRepository).save(product);
        verify(priceHistoryRepository).save(history);
    }

    @Test
    void updatePrice_shouldThrowWhenPriceDoesNotChange() {
        ProductEntity product = mock(ProductEntity.class);

        ProductPriceUpdateRequest request =
                mock(ProductPriceUpdateRequest.class);

        BigDecimal currentPrice = new BigDecimal("10000");

        when(request.price())
                .thenReturn(new BigDecimal("10000.00"));

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(product.getPrice())
                .thenReturn(currentPrice);

        assertThrows(
                InvalidOperationException.class,
                () -> productService.updatePrice(productId, request)
        );

        verify(productRepository, never())
                .save(any(ProductEntity.class));

        verifyNoInteractions(priceHistoryRepository);
    }

    @Test
    void updateStock_shouldIncreaseStockAndRegisterMovement() {
        ProductEntity product = mock(ProductEntity.class);
        ProductResponse expectedResponse = mock(ProductResponse.class);
        StockMovementEntity movement = mock(StockMovementEntity.class);

        ProductStockUpdateRequest request =
                mock(ProductStockUpdateRequest.class);

        when(request.operation())
                .thenReturn(StockAdjustmentOperation.INCREASE);

        when(request.quantity()).thenReturn(5);
        when(request.reason()).thenReturn("Ajuste de inventario");

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(product.getStock()).thenReturn(10);
        when(product.getId()).thenReturn(productId);

        when(productRepository.save(product))
                .thenReturn(product);

        when(stockMovementMapper.toEntity(
                productId,
                StockMovementType.ADJUSTMENT,
                5,
                10,
                15,
                "Ajuste de inventario",
                correlationId
        )).thenReturn(movement);

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);

        ProductResponse response =
                productService.updateStock(productId, request);

        assertSame(expectedResponse, response);

        verify(product).setStock(15);
        verify(productRepository).save(product);
        verify(stockMovementRepository).save(movement);
    }

    @Test
    void updateStock_shouldThrowWhenStockIsInsufficient() {
        ProductEntity product = mock(ProductEntity.class);

        ProductStockUpdateRequest request =
                mock(ProductStockUpdateRequest.class);

        when(request.operation())
                .thenReturn(StockAdjustmentOperation.DECREASE);

        when(request.quantity()).thenReturn(15);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(product.getStock()).thenReturn(10);

        assertThrows(
                InsufficientStockException.class,
                () -> productService.updateStock(productId, request)
        );

        verify(productRepository, never())
                .save(any(ProductEntity.class));

        verifyNoInteractions(stockMovementRepository);
    }

    @Test
    void restock_shouldIncreaseStockAndRegisterMovement() {
        ProductEntity product = mock(ProductEntity.class);
        ProductResponse expectedResponse = mock(ProductResponse.class);
        StockMovementEntity movement = mock(StockMovementEntity.class);

        ProductRestockRequest request =
                mock(ProductRestockRequest.class);

        when(request.quantity()).thenReturn(8);
        when(request.reason()).thenReturn("Reposición de mercadería");

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(product.getStock()).thenReturn(10);
        when(product.getId()).thenReturn(productId);

        when(productRepository.save(product))
                .thenReturn(product);

        when(stockMovementMapper.toEntity(
                productId,
                StockMovementType.RESTOCK,
                8,
                10,
                18,
                "Reposición de mercadería",
                correlationId
        )).thenReturn(movement);

        when(productMapper.toResponse(product))
                .thenReturn(expectedResponse);

        ProductResponse response =
                productService.restock(productId, request);

        assertSame(expectedResponse, response);

        verify(product).setStock(18);
        verify(productRepository).save(product);
        verify(stockMovementRepository).save(movement);
    }
}
