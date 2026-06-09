package finance_dashboard.domain.mapper;

import finance_dashboard.domain.entity.Transaction;
import finance_dashboard.domain.request.TransactionRequest;
import finance_dashboard.domain.response.TransactionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface TransactionMapper {

    @Mapping(source = "category", target = "category")
    @Mapping(source = "category.type", target = "type")
    TransactionResponse toResponse(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "transactionDate", ignore = true)
    Transaction toEntity(TransactionRequest request);
}
