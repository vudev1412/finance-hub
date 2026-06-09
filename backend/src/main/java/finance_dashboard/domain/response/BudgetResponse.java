package finance_dashboard.domain.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class BudgetResponse {
    private UUID id;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;   // tính từ transactions
    private BigDecimal remaining;     // limitAmount - spentAmount
    private Integer month;
    private Integer year;
    private CategoryResponse category;
}
