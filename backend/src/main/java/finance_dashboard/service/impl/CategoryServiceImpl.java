package finance_dashboard.service.impl;

import finance_dashboard.domain.entity.Category;
import finance_dashboard.domain.entity.User;
import finance_dashboard.domain.mapper.CategoryMapper;
import finance_dashboard.domain.request.CategoryRequest;
import finance_dashboard.domain.response.CategoryResponse;
import finance_dashboard.repository.CategoryRepository;
import finance_dashboard.service.CategoryService;
import finance_dashboard.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final SecurityUtil securityUtil;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        if(categoryRepository.existsByNameAndUserId(request.getName(), currentUser.getId())){
            throw new RuntimeException(
                    "Category already exits"
            );
        }
        Category category = Category.builder()
                .name(request.getName())
                .type(request.getType())
                .user(currentUser)
                .build();
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getAll() {
        User currentUser = securityUtil.getCurrentUser();
        return categoryRepository
                .findByUserId(currentUser.getId())
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getById(UUID id) {
        User currentUser = securityUtil.getCurrentUser();
        Category category =
                categoryRepository
                        .findByIdAndUserId(
                                id,
                                currentUser.getId()
                        )
                        .orElseThrow();

        return categoryMapper.toResponse(
                category
        );
    }

    @Override
    public CategoryResponse update(UUID id, CategoryRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        Category category =
                categoryRepository
                        .findByIdAndUserId(
                                id,
                                currentUser.getId()
                        )
                        .orElseThrow();

        category.setName(request.getName());
        category.setType(request.getType());

        return categoryMapper.toResponse(
                category
        );
    }

    @Override
    public void delete(UUID id) {
        User currentUser = securityUtil.getCurrentUser();
        Category category =
                categoryRepository
                        .findByIdAndUserId(
                                id,
                                currentUser.getId()
                        )
                        .orElseThrow();

        categoryRepository.delete(category);
    }
}
