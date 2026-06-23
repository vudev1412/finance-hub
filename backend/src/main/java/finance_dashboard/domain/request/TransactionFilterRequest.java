package finance_dashboard.domain.request;

import finance_dashboard.domain.entity.enums.TransactionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionFilterRequest {
    private LocalDate fromDate;
    private LocalDate toDate;

    private TransactionType type;

    // Pagination
    @Min(value = 0, message = "Page must be >= 0")
    @Builder.Default
    private int page = 0;

    @Min(value = 1, message = "Size must be >= 1")
    @Max(value = 100, message = "Size must be <= 100")
    @Builder.Default
    private int size = 10;

    // Sort: "transactionDate", "amount", "title"
    @Builder.Default
    private String sortBy = "transactionDate";

    @Builder.Default
    private String sortDir = "desc";
}
