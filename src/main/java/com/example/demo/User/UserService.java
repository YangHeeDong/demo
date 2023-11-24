package com.example.demo.User;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String userName, String email, String password){
        SiteUser user = new SiteUser();
        user.setUserName(userName);
        user.setEmail(email);

        user.setPassword(this.passwordEncoder.encode(password));

        this.userRepository.save(user);
        return user;
    }
}
