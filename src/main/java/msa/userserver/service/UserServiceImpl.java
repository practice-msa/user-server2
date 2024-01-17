package msa.userserver.service;

import lombok.RequiredArgsConstructor;
import msa.userserver.domain.UserEntity;
import msa.userserver.domain.UserRepository;
import msa.userserver.dto.UserDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
//    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
//        userDto.initEncryptedPassword(passwordEncoder.encode(userDto.getPwd()));
        userDto.setEncryptedPwd(userDto.getPwd());
        UserEntity userEntity = userDto.toEntity();
        userRepository.save(userEntity);
        System.out.println(userEntity);
        return UserDto.from(userEntity);
    }
}
