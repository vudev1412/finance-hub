package finance_dashboard.service;

import finance_dashboard.domain.entity.RefreshToken;
import finance_dashboard.domain.entity.Role;
import finance_dashboard.domain.entity.User;
import finance_dashboard.domain.request.LoginRequest;
import finance_dashboard.domain.request.RegisterRequest;
import finance_dashboard.domain.response.AuthResponse;
import finance_dashboard.exception.DuplicateResourceException;
import finance_dashboard.exception.ResourceNotFoundException;
import finance_dashboard.exception.UnauthorizedException;
import finance_dashboard.repository.RefreshTokenRepository;
import finance_dashboard.repository.RoleRepository;
import finance_dashboard.repository.UserRepository;
import finance_dashboard.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "USER"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Xác thực — throws BadCredentialsException nếu sai
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getEmail()));

        // Revoke toàn bộ refresh token cũ
        refreshTokenRepository.revokeAllByUserId(user.getId());

        return buildAuthResponse(user);
    }

    // ── Refresh Token ─────────────────────────────────────────

    @Transactional
    public AuthResponse refreshToken(String rawRefreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (stored.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }
        if (stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);
            throw new UnauthorizedException("Refresh token expired, please login again");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return buildAuthResponse(stored.getUser());
    }

    // ── Logout ────────────────────────────────────────────────

    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
        refreshTokenRepository.revokeAllByUserId(user.getId());
        SecurityContextHolder.clearContext();
    }

    // ── Helper ────────────────────────────────────────────────

    public AuthResponse buildAuthResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(roles)
                .build();

        String accessToken  = securityUtil.createAccessToken(user.getEmail(), user);
        String refreshToken = securityUtil.createRefreshToken(user.getEmail(), userInfo);

        // Lưu refresh token vào DB
        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .revoked(false)
                .expiryDate(LocalDateTime.now()
                        .plusSeconds(securityUtil.getRefreshTokenExpiration()))
                .build());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userInfo)
                .build();
    }

    // Getter riêng cho refresh token string (dùng trong Controller set Cookie)
    public String extractRefreshTokenString(AuthResponse response, User user) {
        return refreshTokenRepository
                .findLatestActiveByUserId(user.getId())
                .map(RefreshToken::getToken)
                .orElseThrow();
    }
}
