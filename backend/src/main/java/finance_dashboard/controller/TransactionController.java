package finance_dashboard.controller;

import finance_dashboard.domain.entity.enums.TransactionType;
import finance_dashboard.domain.request.TransactionFilterRequest;
import finance_dashboard.domain.request.TransactionRequest;
import finance_dashboard.domain.response.ApiResponse;
import finance_dashboard.domain.response.PageResponse;
import finance_dashboard.domain.response.TransactionResponse;
import finance_dashboard.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @Valid @RequestBody TransactionRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        transactionService.create(request)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAll(
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            @RequestParam(required = false) @Min(2000) Integer year,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) UUID categoryId) {

        List<TransactionResponse> result;

        if (categoryId != null) {
            result = transactionService.getByCategory(categoryId);
        } else if (month != null && year != null && type != null) {
            result = transactionService.getByTypeAndMonthAndYear(type, month, year);
        } else if (month != null && year != null) {
            result = transactionService.getByMonthAndYear(month, year);
        } else {
            result = transactionService.getAll();
        }

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.success(
                transactionService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request
           ) {

        return ResponseEntity.ok(ApiResponse.success(
                transactionService.update(id, request)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id
          ) {

        transactionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted", null));
    }
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> filter(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        TransactionFilterRequest filter = TransactionFilterRequest.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .type(type)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        return ResponseEntity.ok(ApiResponse.success(
                transactionService.filter(filter)));
    }
}
