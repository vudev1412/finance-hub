package finance_dashboard.repository;

import finance_dashboard.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUserIdOrderByTransactionDateDesc(UUID userId);

    List<Transaction> findByUserIdAndCategoryId(UUID userId, UUID categoryId);

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.user.id = :userId
        AND MONTH(t.transactionDate) = :month
        AND YEAR(t.transactionDate) = :year
        ORDER BY t.transactionDate DESC
    """)
    List<Transaction> findByUserIdAndMonthAndYear(
            @Param("userId") UUID userId,
            @Param("month") int month,
            @Param("year") int year);

    // Tính tổng spent cho budget
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
        WHERE t.user.id = :userId
        AND t.category.id = :categoryId
        AND MONTH(t.transactionDate) = :month
        AND YEAR(t.transactionDate) = :year
    """)
    BigDecimal sumAmountByUserAndCategoryAndMonthAndYear(
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId,
            @Param("month") int month,
            @Param("year") int year);

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    List<Transaction> findByUserIdAndCategoryIdOrderByTransactionDateDesc(
            UUID userId, UUID categoryId);
}
