package finance_dashboard.domain.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserResponse {

    private Long id;

    private String name;

    private String email;

    private List<RoleResponse> roles;

    private LocalDateTime createdAt;
}
