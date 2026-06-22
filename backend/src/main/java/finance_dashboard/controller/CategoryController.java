package finance_dashboard.controller;

import finance_dashboard.domain.request.CategoryRequest;
import finance_dashboard.domain.response.ApiResponse;
import finance_dashboard.domain.response.CategoryResponse;
import finance_dashboard.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@RequestBody CategoryRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(
                categoryService.create(request)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(){
        return ResponseEntity.ok().body(ApiResponse.success(
                categoryService.getAll()
        ));
    }

    @GetMapping("/id")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable UUID id){
        return ResponseEntity.ok().body(ApiResponse.success(
                categoryService.getById(id)
        ));
    }

    @PutMapping("/id")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable UUID id, CategoryRequest request){
        return ResponseEntity.ok().body(ApiResponse.success(
                "Updated successfully", categoryService.update(id, request)
        ));
    }
    @DeleteMapping("/id")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id){
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(
                "Delete success", null

        ));
    }

}
