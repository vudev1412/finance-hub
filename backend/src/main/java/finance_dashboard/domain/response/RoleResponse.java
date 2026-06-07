package finance_dashboard.domain.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleResponse {

    private Long id;

    private String name;
}
