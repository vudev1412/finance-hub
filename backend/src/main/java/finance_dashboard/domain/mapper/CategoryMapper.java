package finance_dashboard.domain.mapper;


import finance_dashboard.domain.entity.Category;
import finance_dashboard.domain.request.CategoryRequest;
import finance_dashboard.domain.response.CategoryResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "user", ignore = true)
    Category toEntity(CategoryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
