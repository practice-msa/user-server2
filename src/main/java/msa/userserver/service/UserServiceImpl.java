package msa.userserver.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msa.userserver.client.OrderServiceClient;
import msa.userserver.domain.UserEntity;
import msa.userserver.domain.UserRepository;
import msa.userserver.dto.UserDto;
import msa.userserver.vo.ResponseOrder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;
//    private final RestTemplate restTemplate;

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

//        String orderUrl = String.format(env.getProperty("order_service.url"),userId);
//
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(orderUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<ResponseOrder>>() {
//                });

//        List<ResponseOrder> orders = orderListResponse.getBody();
        // feign
//        List<ResponseOrder> orders = null;
//        try {
//            orders = orderServiceClient.getOrders(userId);
//        }catch (FeignException ex){
//            log.error(ex.getMessage());
//        }

        // 원래 feign 코드
//        List<ResponseOrder> orders = orderServiceClient.getOrders(userId);
        UserDto userDto = UserDto.from(userEntity);

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
        List<ResponseOrder> orders = circuitBreaker.run(()->orderServiceClient.getOrders(userId), throwable -> new ArrayList<>());

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
