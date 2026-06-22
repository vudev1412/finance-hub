package finance_dashboard.service;

import finance_dashboard.domain.request.CategoryRequest;
import finance_dashboard.domain.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    List<CategoryResponse> getAll();
    CategoryResponse getById(UUID id);
    CategoryResponse update(UUID id, CategoryRequest request);
    void delete(UUID id);
}
