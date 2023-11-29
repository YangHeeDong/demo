package com.example.demo.User;

import com.example.demo.DataNotFoundException;
import com.example.demo.ProfileImg.ProfileImg;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String userName, String email, String password, ProfileImg profileImg){
        SiteUser user = new SiteUser();
        user.setUsername(userName);
        user.setEmail(email);

        user.setPassword(this.passwordEncoder.encode(password));
        user.setProfileImg(profileImg);

        this.userRepository.save(user);
        return user;
    }

    public SiteUser changePassword(String username, String password){

        SiteUser user = getUser(username);
        user.setPassword(this.passwordEncoder.encode(password));

        this.userRepository.save(user);

        return user;
    }

    public SiteUser getUser(String username){
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);

        if(siteUser.isPresent()){
            return siteUser.get();
        }else{
            throw new DataNotFoundException("siteuser not found");
        }

    }
}
