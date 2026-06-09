package finance_dashboard.domain.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class UserResponse {

    private UUID id;

    private String name;

    private String email;

    private Set<String> roles;

    private LocalDateTime createdAt;
}
