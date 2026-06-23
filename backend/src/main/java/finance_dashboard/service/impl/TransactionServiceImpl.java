package finance_dashboard.service.impl;

import finance_dashboard.domain.entity.Category;
import finance_dashboard.domain.entity.Transaction;
import finance_dashboard.domain.entity.User;
import finance_dashboard.domain.entity.enums.TransactionType;
import finance_dashboard.domain.mapper.TransactionMapper;
import finance_dashboard.domain.request.TransactionFilterRequest;
import finance_dashboard.domain.request.TransactionRequest;
import finance_dashboard.domain.response.PageResponse;
import finance_dashboard.domain.response.TransactionResponse;
import finance_dashboard.exception.AppException;
import finance_dashboard.repository.CategoryRepository;
import finance_dashboard.repository.TransactionRepository;
import finance_dashboard.service.TransactionService;
import finance_dashboard.specification.TransactionSpecification;
import finance_dashboard.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final SecurityUtil securityUtil;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionResponse create(TransactionRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        Category category = categoryRepository.findByIdAndUserId(
                request.getCategoryId(),currentUser.getId()
        ).orElseThrow();
        Transaction transaction = Transaction.builder()
                .title(request.getTitle())
                .amount(request.getAmount())
                .note(request.getNote())
                .type(request.getType())
                .transactionDate(request.getTransactionDate())
                .category(category)
                .user(currentUser)
                .build();
        transactionRepository.save(transaction);
        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> filter(TransactionFilterRequest filter) {
        User currentUser = securityUtil.getCurrentUser();
        // Build Sort
        Sort.Direction direction = filter.getSortDir().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        String sortField = resolveSortField(filter.getSortBy());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(),
                Sort.by(direction, sortField));

        // Build Specification
        Specification<Transaction> spec =
                TransactionSpecification.filter(filter, currentUser.getId());

        // Query
        Page<Transaction> page = transactionRepository.findAll(spec, pageable);

        List<TransactionResponse> content = page.getContent()
                .stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<TransactionResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
    // Whitelist sort fields — tránh SQL injection qua sortBy param
    private String resolveSortField(String sortBy) {
        return switch (sortBy) {
            case "amount" -> "amount";
            case "title" -> "title";
            default -> "transactionDate";
        };
    }
    @Override
    public List<TransactionResponse> getAll() {
        User currentUser = securityUtil.getCurrentUser();
        return transactionRepository
                .findByUserIdOrderByTransactionDateDesc(currentUser.getId())
                .stream().map(transactionMapper::toResponse).toList();
    }

    @Override
    public TransactionResponse getById(UUID id) {
        User currentUser = securityUtil.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUser.getId()).orElseThrow();
        return transactionMapper.toResponse(transaction);
    }

    @Override
    public TransactionResponse update(UUID id, TransactionRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUser.getId()).orElseThrow();
        Category category =
                categoryRepository
                        .findByIdAndUserId(
                                request.getCategoryId(),
                                currentUser.getId()
                        )
                        .orElseThrow();

        transaction.setTitle(request.getTitle());
        transaction.setAmount(request.getAmount());
        transaction.setNote(request.getNote());
        transaction.setType(request.getType());
        transaction.setTransactionDate(
                request.getTransactionDate()
        );
        transaction.setCategory(category);

        return transactionMapper.toResponse(
                transaction
        );
    }

    @Override
    public void delete(UUID id) {
        User currentUser =
                securityUtil.getCurrentUser();

        Transaction transaction =
                transactionRepository
                        .findByIdAndUserId(
                                id,
                                currentUser.getId()
                        )
                        .orElseThrow();

        transactionRepository.delete(transaction);
    }

    @Override
    public List<TransactionResponse> getByMonthAndYear(int month, int year) {
        return List.of();
    }

    @Override
    public List<TransactionResponse> getByTypeAndMonthAndYear(TransactionType type, int month, int year) {
        return List.of();
    }

    @Override
    public List<TransactionResponse> getByCategory(UUID categoryId) {
        User currentUser =
                securityUtil.getCurrentUser();
        Category category = categoryRepository.findByIdAndUserId(categoryId, currentUser.getId()).orElseThrow();
        return transactionRepository
                .findByUserIdAndCategoryIdOrderByTransactionDateDesc(
                        currentUser.getId(), categoryId)
                .stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }
}
