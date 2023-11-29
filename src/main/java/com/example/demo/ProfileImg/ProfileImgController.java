package com.example.demo.ProfileImg;

import com.example.demo.User.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ProfileImgController {

    private final ProfileImgService profileImgService;

//    public void ProfileForCreateUser(SiteUser newUser, String url){
//        this.profileImgService.ProfileImgForCreateUser(newUser, url);
//    }
}
