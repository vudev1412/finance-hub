package finance_dashboard.repository;

import finance_dashboard.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    @Query("""
    SELECT r FROM RefreshToken r
    WHERE r.user.id = :userId AND r.revoked = false
    ORDER BY r.expiryDate DESC
    LIMIT 1
""")
    Optional<RefreshToken> findLatestActiveByUserId(@Param("userId") UUID userId);
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId")
    void revokeAllByUserId(UUID userId);
}
