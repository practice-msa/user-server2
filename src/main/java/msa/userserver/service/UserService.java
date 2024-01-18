package msa.userserver.service;

import msa.userserver.domain.UserEntity;
import msa.userserver.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();
}
