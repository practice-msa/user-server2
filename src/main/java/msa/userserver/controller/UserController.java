package msa.userserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msa.userserver.dto.UserDto;
import msa.userserver.service.UserService;
import msa.userserver.vo.Greeting;
import msa.userserver.vo.RequestUser;
import msa.userserver.vo.ResponseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final Environment env;
    private final UserService userService;
    private final Greeting greeting;


    @RequestMapping("/health_check")
    public String status(){
        return "good";
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
}
