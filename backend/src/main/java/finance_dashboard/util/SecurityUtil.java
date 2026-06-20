package finance_dashboard.util;

import finance_dashboard.domain.entity.Permission;
import finance_dashboard.domain.entity.User;
import finance_dashboard.domain.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecurityUtil {
    private final JwtEncoder jwtEncoder;

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${lhv.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;   // seconds

    @Value("${lhv.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;  // seconds

    public String createAccessToken(String email, User user) {
        Instant now = Instant.now();
        Instant validity = now.plus(accessTokenExpiration, ChronoUnit.SECONDS);

        Set<String> authorities = user.getRoles().stream()
                .flatMap(role -> {
                    var perms = role.getPermissions().stream()
                            .map(Permission::getName); // VD: USER_READ
                    return java.util.stream.Stream.concat(
                            java.util.stream.Stream.of(role.getName()), // VD: ROLE_ADMIN
                            perms
                    );
                })
                .collect(Collectors.toSet());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("userId", user.getId().toString())
                .claim("authorities", authorities)   // đổi tên claim
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(JwsHeader.with(JWT_ALGORITHM).build(), claims))
                .getTokenValue();
    }
    public String createRefreshToken(String email, AuthResponse.UserInfo userInfo) {
        Instant now = Instant.now();
        Instant validity = now.plus(refreshTokenExpiration, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userInfo)
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(JwsHeader.with(JWT_ALGORITHM).build(), claims))
                .getTokenValue();
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

}
