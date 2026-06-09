package finance_dashboard.domain.mapper;


import finance_dashboard.domain.entity.Budget;
import finance_dashboard.domain.response.BudgetResponse;
import finance_dashboard.domain.response.CategoryResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BudgetMapper {

    // spentAmount được tính ở service, truyền vào đây
    public BudgetResponse toResponse(Budget budget, BigDecimal spentAmount) {
        BigDecimal spent = spentAmount != null ? spentAmount : BigDecimal.ZERO;
        BigDecimal remaining = budget.getLimitAmount().subtract(spent);

        return BudgetResponse.builder()
                .id(budget.getId())
                .limitAmount(budget.getLimitAmount())
                .spentAmount(spent)
                .remaining(remaining)
                .month(budget.getMonth())
                .year(budget.getYear())
                .category(CategoryResponse.builder()
                        .id(budget.getCategory().getId())
                        .name(budget.getCategory().getName())
                        .type(budget.getCategory().getType())
                        .build())
                .build();
    }
}
