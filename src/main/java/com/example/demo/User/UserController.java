package com.example.demo.User;

import com.example.demo.ProfileImg.ProfileImg;
import com.example.demo.ProfileImg.ProfileImgService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ProfileImgService profileImgService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm){
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, @RequestParam MultipartFile imgData){


        if(bindingResult.hasErrors()){
            return "signup_form";
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            bindingResult.rejectValue("password2","passwordInCorrect","2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try{
            ProfileImg profileImg;
            // 업로드된 이미지가 있을 때
            if(!imgData.getOriginalFilename().equals("")){
                profileImg = this.profileImgService.ProfileImgForCreateUser(this.profileImgService.saveFile(imgData));
            }
            // 없을때 기본이미지로
            else{
                profileImg = this.profileImgService.ProfileImgForCreateUser("default_Img.png");
            }

            SiteUser user = userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(),
                    userCreateForm.getPassword1(),
                    profileImg);


        }catch (DataIntegrityViolationException e){
            e.printStackTrace();
            bindingResult.reject("signupFailed","이미 등록된 사용자 입니다.");
            return "signup_form";
        }catch (Exception e){
            e.printStackTrace();
            bindingResult.reject("signup_Faild",e.getMessage());
            return "signup_form";
        }


        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "login_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    public String myPage(Model model, Principal principal){
        SiteUser siteUser = userService.getUser(principal.getName());
        model.addAttribute("username",siteUser.getUsername());
        model.addAttribute("userEmail",siteUser.getEmail());
        return "myPage";
    }

    // 정보 수정 페이지 넘어가기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String userModify(Principal principal, UserCreateForm userCreateForm){
        SiteUser siteUser = userService.getUser(principal.getName());
        userCreateForm.setUsername(siteUser.getUsername());
        userCreateForm.setEmail(siteUser.getEmail());

        return "userModify_form";
    }

    // 정보 수정
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String userModify(Principal principal, @Valid UserCreateForm userCreateForm, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "userModify_form";
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            bindingResult.rejectValue("password2","passwordInCorrect","2개의 패스워드가 일치하지 않습니다.");
            return "userModify_form";
        }

        // 만약 현재 로그인한 username과 UserCreateForm에 넘어온 username이 다르다면
        if(!userCreateForm.getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 접근 입니다");
        }

        this.userService.changePassword(userCreateForm.getUsername(),userCreateForm.getPassword1());

        return "redirect:/user/myPage";
    }

}
