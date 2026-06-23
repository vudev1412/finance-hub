// specification/TransactionSpecification.java
package finance_dashboard.specification;

import finance_dashboard.domain.entity.Transaction;
import finance_dashboard.domain.request.TransactionFilterRequest;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionSpecification {

    private TransactionSpecification() {}

    public static Specification<Transaction> filter(
            TransactionFilterRequest filter, UUID userId) {

        return (Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Luôn filter theo userId
            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (filter.getFromDate() != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("transactionDate"),
                                filter.getFromDate()
                        )
                );
            }

            if (filter.getToDate() != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("transactionDate"),
                                filter.getToDate()
                        )
                );
            }

            // Filter theo TransactionType — join sang category
            if (filter.getType() != null) {
                Join<Object, Object> categoryJoin = root.join("category", JoinType.LEFT);
                predicates.add(cb.equal(categoryJoin.get("type"), filter.getType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}