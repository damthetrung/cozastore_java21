package com.cybersoft.cozastore_java21.config;

import com.cybersoft.cozastore_java21.filter.JwtFilter;
import com.cybersoft.cozastore_java21.provider.CustomAuthenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CustomAuthenProvider customAuthenProvider;
    @Autowired
    private JwtFilter jwtFilter;

    // khai báo chuản mã hoá bcrypt và lưu trữ trên IOC
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(customAuthenProvider)
                .build();
    }
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder){
//        UserDetails admin= User.withUsername("admin")
//                .password(passwordEncoder().encode("123456"))
//                .roles("ADMIN", "DELETE").build();
//        UserDetails user=User.withUsername("user")
//                .password(passwordEncoder().encode("123456"))
//                .roles("USER", "SAVE").build();
//        return new InMemoryUserDetailsManager(admin, user);
//    }
    //đây là filter custom các rule liên quan đến link hoặc cấu hình của security
    //Java 8, 11 : antMatchers
    //java 17    : requestantMatchers
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
            return httpSecurity.csrf().disable() // tắt cấu hình liên quan tấn công csrf
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // khai báo không sử dụng session
                    .and()
                    .authorizeHttpRequests()// quy định lại các rule liên quan đến chứng thực cho link được gọi
                    .antMatchers( "/signin", "/signup").permitAll() // cho qua
                    .antMatchers("/admin").hasRole("ADMIN")
                    .anyRequest().authenticated() // tất cả link còn lại đều phải chứng thực
                    .and()
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
    }
}
