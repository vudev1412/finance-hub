package finance_dashboard.service;

import finance_dashboard.domain.entity.enums.TransactionType;
import finance_dashboard.domain.request.TransactionRequest;
import finance_dashboard.domain.response.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse create(TransactionRequest request);
    List<TransactionResponse> getAll();
    TransactionResponse getById(UUID id);
    TransactionResponse update(UUID id, TransactionRequest request);
    void delete(UUID id);
    List<TransactionResponse> getByMonthAndYear(int month, int year);
    List<TransactionResponse> getByTypeAndMonthAndYear(TransactionType type, int month, int year);
    List<TransactionResponse> getByCategory(UUID categoryId);


}
