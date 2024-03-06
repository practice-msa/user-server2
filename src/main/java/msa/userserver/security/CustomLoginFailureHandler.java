package msa.userserver.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msa.userserver.domain.UserEntity;
import msa.userserver.domain.UserRepository;
import msa.userserver.dto.response.ApiResponse;
import msa.userserver.dto.response.ErrorResponse;
import msa.userserver.service.UserService;
import msa.userserver.service.UserServiceImpl;
import msa.userserver.vo.RequestLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage;
        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "내부적으로 발생한 시스템 문제로 인해 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
        } else {
            errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
        }

        RequestLogin creds = (RequestLogin) request.getAttribute("requestLogin");
        String email = creds.getEmail();
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity != null) {
            userEntity.getPassword().updateFailedCount(false);
            userRepository.save(userEntity);
        }
        if(userEntity.getPassword().getFailedCount() > 5){
            errorMessage = "5회 초과하였습니다.";
        }


        response.setCharacterEncoding("UTF-8"); // 문자 인코딩 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 상태 코드 설정
        response.setContentType("application/json"); // 응답 형식 설정 (예: 텍스트)

        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponse<String> apiResponse = new ApiResponse<>(false, null, new ErrorResponse(errorMessage));
        Map<String, Object> errorMap = Collections.singletonMap("error", apiResponse);
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse); // 응답 본문에 에러 메시지 작성
    }
}
