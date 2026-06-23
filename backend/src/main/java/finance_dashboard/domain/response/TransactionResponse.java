package finance_dashboard.domain.response;

import finance_dashboard.domain.entity.enums.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TransactionResponse {
    private UUID id;
    private String title;
    private BigDecimal amount;
    private String note;
    private LocalDate transactionDate;
    private TransactionType type;
    UUID categoryId;
    private String categoryName;
}