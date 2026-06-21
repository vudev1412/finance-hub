package finance_dashboard.controller;

import finance_dashboard.domain.request.ChangePasswordRequest;
import finance_dashboard.domain.request.UpdateProfileRequest;
import finance_dashboard.domain.response.ApiResponse;
import finance_dashboard.domain.response.UserResponse;
import finance_dashboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile() {

        return ResponseEntity.ok().body(ApiResponse.success(
                "Get success",userService.getMyProfile()
        ));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestBody
            UpdateProfileRequest request
    ) {
        return ResponseEntity.ok().body(ApiResponse.success(
                "Update success",userService.updateProfile(request)
        ));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(request);

        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully", null)
        );
    }
}
