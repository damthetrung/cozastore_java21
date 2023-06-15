package com.cybersoft.cozastore_java21.provider;

import com.cybersoft.cozastore_java21.entity.UserEntity;
import com.cybersoft.cozastore_java21.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomAuthenProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username=authentication.getName();
        String password= (String) authentication.getCredentials();
        UserEntity user=userRepository.findByEmail(username);
        if(user != null && passwordEncoder.matches(password, user.getPassword())){
                  // đang nhập thành công
            return new UsernamePasswordAuthenticationToken(username, user.getPassword(), new ArrayList<>());
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //khai báo loại chứng thực để so sánh
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
