package com.example.userservice.security;

import com.example.userservice.service.UserService;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration     // 다른 빌드보다 우선순위를 높여서 빌드 가능
@EnableWebSecurity // Web Security 용도로 등록
public class WebSecurity {

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;  // application.yml 에서 토큰 유효 기간 설정 등의 정보 가져옴

    public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder,
            Environment env) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.env = env;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = getAuthenticationManager(http);

        http.csrf().disable()
            .headers(authorize -> authorize
                    // 이 설정은 웹 페이지가 다른 사이트에 삽입되는 것을 방지하기 위해 X-Frame-Options 헤더를 비활성화한다.
                    // 이 설정을 사용하면 웹 페이지가 iframe 등의 요소로 다른 도메인에서 로드되는 것을 방지할 수 있다.
                    // frame으로 나눠진 H2 화면을 보여준다.
                    .frameOptions().disable()
            )
            .authorizeHttpRequests(authorize -> authorize
                    //.antMatchers("/users/**").permitAll()
                    .antMatchers("/**").access(hasIpAddress("192.168.0.36")) // IP 변경
                    .and()
                    .authenticationManager(authenticationManager)
                    .addFilter(getAuthenticationFilter(authenticationManager))
            );
        return http.build();
    }

    private static AuthorizationManager<RequestAuthorizationContext> hasIpAddress(String ipAddress) {
        IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(ipAddress);
        return (authentication, context) -> {
            HttpServletRequest request = context.getRequest();
            return new AuthorizationDecision(ipAddressMatcher.matches(request));
        };
    }

    // select pwd from users where email=?     -> 역할 -> userDetailsService() 에 인자로 전달
    // db_pwd(encryped) == input_pwd(encryped) -> 역할 -> passwordEncoder() 에 인자로 전달
    private AuthenticationManager getAuthenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject((AuthenticationManagerBuilder.class));
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder((bCryptPasswordEncoder));

        return authenticationManagerBuilder.build();
    }

    // AuthenticationFilter가 Filter를 상속받아서 반환값으로 사용할 수 있음
    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new AuthenticationFilter(authenticationManager, userService, env);
    }


}
