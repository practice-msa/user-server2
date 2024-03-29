package msa.userserver.controller;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msa.userserver.domain.UserEntity;
import msa.userserver.dto.UserDto;
import msa.userserver.dto.response.ApiResponse;
import msa.userserver.service.UserService;
import msa.userserver.vo.Greeting;
import msa.userserver.vo.RequestUser;
import msa.userserver.vo.ResponseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final Environment env;
    private final UserService userService;
    private final Greeting greeting;


    @RequestMapping("/health_check")
    //@Timed(value="users.status", longTask = true)
    public String status(){

        return String.format("It's working in User Service"
                + ", port(local.server.port)" + env.getProperty("local.server.port")
                + ", port(server.port)" + env.getProperty("server.port")
                + ", token secret" + env.getProperty("token.secret")
                + ", token expiration time" + env.getProperty("token.expiration_time "));
    }

    @RequestMapping("/welcome")
    //@Timed(value="users.welcome", longTask = true)
    public String welcome(){
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ApiResponse<ResponseUser> createUser(@RequestBody @Valid RequestUser user){
        UserDto userDto = userService.createUser(user.toDto());
        ResponseUser res = ResponseUser.from(userDto);
        return new ApiResponse<>(true,res,null);
    }

    @GetMapping("/users")
    public ApiResponse<List<ResponseUser>> getUsers(){
        Iterable<UserEntity> userList = userService.getUserByAll();
        List<ResponseUser> result = new ArrayList<>();

        for (UserEntity userEntity : userList) {
            result.add(ResponseUser.from(userEntity));
        }
        return new ApiResponse<>(true,result,null);
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<ResponseUser> getUser(@PathVariable String userId){
        UserDto userDto  = userService.getUserByUserId(userId);

        return new ApiResponse<>(true,ResponseUser.from(userDto),null);
    }
}
