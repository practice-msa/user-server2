package msa.userserver.service;

import msa.userserver.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
}
