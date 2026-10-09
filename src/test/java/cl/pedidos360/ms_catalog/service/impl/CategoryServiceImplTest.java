package cl.pedidos360.ms_catalog.service.impl;

import cl.pedidos360.ms_catalog.dto.request.CategoryCreateRequest;
import cl.pedidos360.ms_catalog.dto.request.CategoryStatusUpdateRequest;
import cl.pedidos360.ms_catalog.dto.request.CategoryUpdateRequest;
import cl.pedidos360.ms_catalog.dto.response.CategoryResponse;
import cl.pedidos360.ms_catalog.entity.CategoryEntity;
import cl.pedidos360.ms_catalog.exception.ResourceAlreadyExistsException;
import cl.pedidos360.ms_catalog.exception.ResourceNotFoundException;
import cl.pedidos360.ms_catalog.mapper.CategoryMapper;
import cl.pedidos360.ms_catalog.observability.CorrelationIdProvider;
import cl.pedidos360.ms_catalog.repository.CategoryRepository;
import cl.pedidos360.ms_catalog.security.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private CorrelationIdProvider correlationIdProvider;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UUID categoryId;
    private UUID userId;
    private UUID correlationId;

    @BeforeEach
    void setUp() {
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
    void create_shouldSaveCategoryWhenNameDoesNotExist() {
        CategoryCreateRequest request =
                mock(CategoryCreateRequest.class);

        CategoryEntity category = mock(CategoryEntity.class);
        CategoryEntity savedCategory = mock(CategoryEntity.class);
        CategoryResponse expectedResponse =
                mock(CategoryResponse.class);

        when(request.name()).thenReturn("Electrónica");

        when(categoryRepository.existsByNameIgnoreCase("Electrónica"))
                .thenReturn(false);

        when(categoryMapper.toEntity(request))
                .thenReturn(category);

        when(categoryRepository.save(category))
                .thenReturn(savedCategory);

        when(savedCategory.getId())
                .thenReturn(categoryId);

        when(categoryMapper.toResponse(savedCategory))
                .thenReturn(expectedResponse);

        CategoryResponse response = categoryService.create(request);

        assertSame(expectedResponse, response);

        verify(categoryRepository)
                .existsByNameIgnoreCase("Electrónica");

        verify(categoryRepository).save(category);

        verify(categoryMapper).toEntity(request);

        verify(categoryMapper).toResponse(savedCategory);
    }

    @Test
    void create_shouldThrowWhenNameAlreadyExists() {
        CategoryCreateRequest request =
                mock(CategoryCreateRequest.class);

        when(request.name()).thenReturn("Electrónica");

        when(categoryRepository.existsByNameIgnoreCase("Electrónica"))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> categoryService.create(request)
        );

        verify(categoryRepository, never())
                .save(any(CategoryEntity.class));

        verifyNoInteractions(categoryMapper);
    }

    @Test
    void findAll_shouldReturnMappedPage() {
        PageRequest pageable = PageRequest.of(0, 10);

        CategoryEntity category = mock(CategoryEntity.class);
        CategoryResponse expectedResponse =
                mock(CategoryResponse.class);

        Page<CategoryEntity> categoryPage =
                new PageImpl<>(List.of(category), pageable, 1);

        when(categoryRepository.findAll(pageable))
                .thenReturn(categoryPage);

        when(categoryMapper.toResponse(category))
                .thenReturn(expectedResponse);

        Page<CategoryResponse> response =
                categoryService.findAll(pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertSame(expectedResponse, response.getContent().get(0));

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void findById_shouldReturnCategoryWhenItExists() {
        CategoryEntity category = mock(CategoryEntity.class);
        CategoryResponse expectedResponse =
                mock(CategoryResponse.class);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(expectedResponse);

        CategoryResponse response =
                categoryService.findById(categoryId);

        assertSame(expectedResponse, response);

        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void findById_shouldThrowWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.findById(categoryId)
        );

        verify(categoryMapper, never())
                .toResponse(any(CategoryEntity.class));
    }

    @Test
    void update_shouldSaveChangesWhenNameRemainsTheSameIgnoringCase() {
        CategoryUpdateRequest request =
                mock(CategoryUpdateRequest.class);

        CategoryEntity category = mock(CategoryEntity.class);
        CategoryResponse expectedResponse =
                mock(CategoryResponse.class);

        when(request.name()).thenReturn("ELECTRÓNICA");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(category.getName()).thenReturn("Electrónica");

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(category.getId()).thenReturn(categoryId);

        when(categoryMapper.toResponse(category))
                .thenReturn(expectedResponse);

        CategoryResponse response =
                categoryService.update(categoryId, request);

        assertSame(expectedResponse, response);

        verify(categoryRepository, never())
                .existsByNameIgnoreCase(anyString());

        verify(categoryMapper).updateEntity(category, request);
        verify(categoryRepository).save(category);
    }

    @Test
    void update_shouldThrowWhenNewNameBelongsToAnotherCategory() {
        CategoryUpdateRequest request =
                mock(CategoryUpdateRequest.class);

        CategoryEntity category = mock(CategoryEntity.class);

        when(request.name()).thenReturn("Deportes");

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(category.getName()).thenReturn("Electrónica");

        when(categoryRepository.existsByNameIgnoreCase("Deportes"))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> categoryService.update(categoryId, request)
        );

        verify(categoryRepository, never())
                .save(any(CategoryEntity.class));

        verify(categoryMapper, never())
                .updateEntity(any(CategoryEntity.class), any());
    }

    @Test
    void update_shouldThrowWhenCategoryDoesNotExist() {
        CategoryUpdateRequest request =
                mock(CategoryUpdateRequest.class);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.update(categoryId, request)
        );

        verify(categoryRepository, never())
                .save(any(CategoryEntity.class));
    }

    @Test
    void updateStatus_shouldChangeActiveState() {
        CategoryStatusUpdateRequest request =
                mock(CategoryStatusUpdateRequest.class);

        CategoryEntity category = mock(CategoryEntity.class);
        CategoryResponse expectedResponse =
                mock(CategoryResponse.class);

        when(request.active()).thenReturn(false);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(category.isActive()).thenReturn(true, false);

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(category.getId()).thenReturn(categoryId);

        when(categoryMapper.toResponse(category))
                .thenReturn(expectedResponse);

        CategoryResponse response =
                categoryService.updateStatus(categoryId, request);

        assertSame(expectedResponse, response);

        verify(category).setActive(false);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void updateStatus_shouldThrowWhenCategoryDoesNotExist() {
        CategoryStatusUpdateRequest request =
                mock(CategoryStatusUpdateRequest.class);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.updateStatus(categoryId, request)
        );

        verify(categoryRepository, never())
                .save(any(CategoryEntity.class));
    }

    @Test
    void delete_shouldDeactivateCategoryInsteadOfDeletingIt() {
        CategoryEntity category = mock(CategoryEntity.class);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        categoryService.delete(categoryId);

        verify(category).setActive(false);
        verify(categoryRepository).save(category);

        verify(categoryRepository, never())
                .deleteById(categoryId);
    }

    @Test
    void delete_shouldThrowWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.delete(categoryId)
        );

        verify(categoryRepository, never())
                .save(any(CategoryEntity.class));
    }
}
