package msa.userserver.service;

import lombok.RequiredArgsConstructor;
import msa.userserver.domain.UserEntity;
import msa.userserver.domain.UserRepository;
import msa.userserver.dto.UserDto;
import msa.userserver.vo.ResponseOrder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
//    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username);

        if(user == null){
            throw new UsernameNotFoundException(username);
        }
        // 마지막 어레이는 로그인 되었을 때 그 다음에 할 수 있는 작업에서의 권한 추가
        return new User(user.getEmail(),user.getEncryptedPwd()
                ,true,true,true,true,new ArrayList<>());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
//        userDto.initEncryptedPassword(passwordEncoder.encode(userDto.getPwd()));
        userDto.setEncryptedPwd(bCryptPasswordEncoder.encode(userDto.getPwd()));
        UserEntity userEntity = userDto.toEntity();
        userRepository.save(userEntity);
        System.out.println(userEntity);
        return UserDto.from(userEntity);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity == null){
            throw new UsernameNotFoundException("user not found");
        }

        List<ResponseOrder> orders = new ArrayList<>();
        UserDto userDto = UserDto.from(userEntity);
        userDto.setOrders(orders);
        return userDto;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);

        if(user == null)
            throw new UsernameNotFoundException(email);

        return UserDto.from(user);
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }
}
