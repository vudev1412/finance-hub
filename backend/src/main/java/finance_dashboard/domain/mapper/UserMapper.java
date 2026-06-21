package finance_dashboard.domain.mapper;

import finance_dashboard.domain.entity.User;
import finance_dashboard.domain.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
