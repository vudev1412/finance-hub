package finance_dashboard.service;

import finance_dashboard.domain.request.ChangePasswordRequest;
import finance_dashboard.domain.request.UpdateProfileRequest;
import finance_dashboard.domain.response.UserResponse;

public interface UserService {
    UserResponse getMyProfile();

    UserResponse updateProfile(
            UpdateProfileRequest request
    );

    void changePassword(
            ChangePasswordRequest request
    );
}
