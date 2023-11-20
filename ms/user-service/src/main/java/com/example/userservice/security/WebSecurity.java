package com.example.userservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration     // 다른 빌드보다 우선순위를 높여서 빌드 가능
@EnableWebSecurity // Web Security 용도로 등록
public class WebSecurity {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .headers(authorize -> authorize
                    // 이 설정은 웹 페이지가 다른 사이트에 삽입되는 것을 방지하기 위해 X-Frame-Options 헤더를 비활성화한다.
                    // 이 설정을 사용하면 웹 페이지가 iframe 등의 요소로 다른 도메인에서 로드되는 것을 방지할 수 있다.
                    // frame으로 나눠진 H2 화면을 보여준다.
                    .frameOptions().disable()
            )
            .authorizeHttpRequests(authorize -> authorize
                    .antMatchers("/users/**").permitAll()
            );
        return http.build();
    }
}
