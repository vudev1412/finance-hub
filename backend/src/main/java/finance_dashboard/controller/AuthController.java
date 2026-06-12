package finance_dashboard.controller;

import finance_dashboard.domain.entity.User;
import finance_dashboard.domain.request.LoginRequest;
import finance_dashboard.domain.request.RegisterRequest;
import finance_dashboard.domain.response.ApiResponse;
import finance_dashboard.domain.response.AuthResponse;
import finance_dashboard.exception.UnauthorizedException;
import finance_dashboard.repository.UserRepository;
import finance_dashboard.service.AuthService;
import finance_dashboard.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    @Value("${lhv.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration; // seconds

    private final AuthService authService;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    // ── Register ──────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse authResponse = authService.register(request);
        ResponseCookie cookie = buildRefreshTokenCookie(authResponse);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.created(authResponse));
    }

    // ── Login ─────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse authResponse = authService.login(request);
        ResponseCookie cookie = buildRefreshTokenCookie(authResponse);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success("Login success", authResponse));
    }

    // ── Refresh Token ─────────────────────────────────────────

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token not found in cookie");
        }

        AuthResponse authResponse = authService.refreshToken(refreshToken);
        ResponseCookie cookie = buildRefreshTokenCookie(authResponse);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success("Token refreshed", authResponse));
    }

    // ── Logout ────────────────────────────────────────────────

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.logout(userDetails.getUsername());

        // Xóa cookie phía client
        ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(ApiResponse.success("Logged out successfully", null));
    }

    // ── Helper ────────────────────────────────────────────────

    private ResponseCookie buildRefreshTokenCookie(AuthResponse authResponse) {
        // Lấy refresh token từ DB (mới nhất của user vừa login)
        User user = userRepository.findByEmail(authResponse.getUser().getEmail())
                .orElseThrow();
        String refreshToken = authService
                .extractRefreshTokenString(authResponse, user);

        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
    }
}
