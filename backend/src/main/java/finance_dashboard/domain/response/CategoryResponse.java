package finance_dashboard.domain.response;

import finance_dashboard.domain.entity.enums.TransactionType;
import lombok.*;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CategoryResponse {
    private UUID id;
    private String name;
    private TransactionType type;
}
