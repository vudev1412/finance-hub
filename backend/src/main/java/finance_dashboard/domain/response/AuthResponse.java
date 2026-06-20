package finance_dashboard.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    @JsonIgnore
    private String refreshToken;

    private UserInfo user;

    // Refresh token KHÔNG trả trong body — set qua HttpOnly Cookie

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private UUID id;
        private String name;
        private String email;
        private List<String> roles;
    }
}
