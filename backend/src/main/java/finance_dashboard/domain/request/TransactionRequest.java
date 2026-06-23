package finance_dashboard.domain.request;

import finance_dashboard.domain.entity.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TransactionRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String note;
    private TransactionType type;
    private LocalDate transactionDate;

    @NotNull(message = "Category is required")
    private UUID categoryId;
}