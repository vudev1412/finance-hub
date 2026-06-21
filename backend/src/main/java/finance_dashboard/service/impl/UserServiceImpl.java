package finance_dashboard.service.impl;

import finance_dashboard.domain.entity.User;
import finance_dashboard.domain.mapper.UserMapper;
import finance_dashboard.domain.request.ChangePasswordRequest;
import finance_dashboard.domain.request.UpdateProfileRequest;
import finance_dashboard.domain.response.UserResponse;
import finance_dashboard.repository.UserRepository;
import finance_dashboard.service.UserService;
import finance_dashboard.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final SecurityUtil securityUtils;
    @Override
    public UserResponse getMyProfile() {
        User user = securityUtils.getCurrentUser();

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = securityUtils.getCurrentUser();

        user.setName(request.getName());

        return userMapper.toResponse(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = securityUtils.getCurrentUser();

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException(
                    "Current password incorrect"
            );
        }
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );
    }
}
