package msa.userserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msa.userserver.domain.UserEntity;
import msa.userserver.dto.UserDto;
import msa.userserver.service.UserService;
import msa.userserver.vo.Greeting;
import msa.userserver.vo.RequestUser;
import msa.userserver.vo.ResponseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final Environment env;
    private final UserService userService;
    private final Greeting greeting;


    @RequestMapping("/health_check")
    public String status(){

        return String.format("Port %s",env.getProperty("local.server.port"));
    }

    @RequestMapping("/welcome")
    public String welcome(){
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user){
        UserDto userDto = userService.createUser(user.toDto());

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUser.from(userDto));
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers(){
        Iterable<UserEntity> userList = userService.getUserByAll();
        List<ResponseUser> result = new ArrayList<>();

        for (UserEntity userEntity : userList) {
            result.add(ResponseUser.from(userEntity));
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable String userId){
        UserDto userDto  = userService.getUserByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseUser.from(userDto));
    }
}
