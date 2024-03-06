package msa.userserver.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import msa.userserver.domain.UserEntity;
import msa.userserver.domain.UserRepository;
import msa.userserver.dto.UserDto;
import msa.userserver.dto.response.ApiResponse;
import msa.userserver.dto.response.ErrorResponse;
import msa.userserver.service.UserService;
import msa.userserver.vo.RequestLogin;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private UserService userService;
    private UserRepository userRepository;
    private Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, UserRepository userRepository, Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.userRepository = userRepository;
        this.env = env;
    }



    // 요청 정보를 보냈을 때 처리하는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try{
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(),RequestLogin.class);
            request.setAttribute("requestLogin", creds);
            // 입력 받은 이메일과 비밀번호를 spring security에서 사용할 수 있는 형태로 변경해줘야함.
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    // 실제 인증처리가 성공했을 때 어떤 처리를 해줄 것인지(토큰을 만든다거나 반환값을 뭘 줄 것인가)
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        String userName = (((User)authResult.getPrincipal()).getUsername());
        UserDto userDetails = userService.getUserDetailsByEmail(userName);
        UserEntity userEntity = userRepository.findByEmail(userName);
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponse<String> apiResponse;

        response.setCharacterEncoding("UTF-8"); // 문자 인코딩 설정


        if (userEntity.getPassword().getFailedCount() > 5) {
            // 비밀번호 재설정이 필요한 경우에 대한 처리
            apiResponse = new ApiResponse<>(false, "비밀번호재설정", null);

            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
            response.getWriter().write(jsonResponse);
            return;
        }
        if(userEntity.getPassword().isExpiration()){
            apiResponse = new ApiResponse<>(false, "만료되었음", null);

            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
            response.getWriter().write(jsonResponse);
            return;
        }

        userEntity.getPassword().updateFailedCount(true);
        userRepository.save(userEntity);

        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512,env.getProperty("token.secret"))
                .compact();

        response.addHeader("token",token);
        response.addHeader("userId",userDetails.getUserId());
        apiResponse = new ApiResponse<>(true, "로그인 성공", null);

        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);

    }
}
