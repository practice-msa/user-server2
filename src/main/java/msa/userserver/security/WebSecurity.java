package msa.userserver.security;

import lombok.RequiredArgsConstructor;
import msa.userserver.domain.UserRepository;
import msa.userserver.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private final Environment env;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CustomLoginFailureHandler loginFailureHandler;
    // 이건 권한
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/**").permitAll()
//        http.authorizeRequests().antMatchers("/**")
//                .hasIpAddress(env.getProperty("192.168.35.200"))// 변경해야함
                .and()
                .addFilter(getAuthenticationFilter());
        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception{
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(),userService,userRepository,env);
        authenticationFilter.setAuthenticationFailureHandler(new CustomLoginFailureHandler(userService,userRepository));

        return authenticationFilter;
    }

    // 이건 인증
    // select pwd from users where email = ? -> 이걸 userDetailsService에서 처리함
    // 그리고 해당 비번과 받아온 비번을 암호화해서 비교
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 사용자가 전달했던 이메일 패스워드를 가지고 로그인 처리
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

}
