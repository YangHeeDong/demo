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

import java.io.File;
import java.io.IOException;
import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ProfileImgService profileImgService;

    @GetMapping("/userSignup")
    public String signup(UserCreateForm userCreateForm){
        return "userSignup_form";
    }

    @PostMapping("/userSignup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, @RequestParam MultipartFile imgData){


        if(bindingResult.hasErrors()){
            return "userSignup_form";
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            bindingResult.rejectValue("password2","passwordInCorrect","2개의 패스워드가 일치하지 않습니다.");
            return "userSignup_form";
        }

        try{
            SiteUser user = userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(),
                    userCreateForm.getPassword1());

            ProfileImg profileImg;
            // 업로드된 이미지가 있을 때
            if(!imgData.getOriginalFilename().equals("")){
                profileImg = this.profileImgService.ProfileImgForCreateUser(user,this.profileImgService.saveFile(imgData));
            }
            // 없을때 기본이미지로
            else{
                profileImg = this.profileImgService.ProfileImgForCreateUser(user,"default_Img.png");
            }

        }catch (DataIntegrityViolationException e){
            e.printStackTrace();
            bindingResult.reject("signupFailed","이미 등록된 사용자 입니다.");
            return "userSignup_form";
        }catch (Exception e){
            e.printStackTrace();
            bindingResult.reject("signup_Faild",e.getMessage());
            return "userSignup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/userLogin")
    public String login(){
        return "userLogin_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/userMyPage")
    public String myPage(Model model, Principal principal){
        SiteUser siteUser = userService.getUser(principal.getName());
        model.addAttribute("username",siteUser.getUsername());
        model.addAttribute("userEmail",siteUser.getEmail());
        model.addAttribute("imgUrl",siteUser.getProfileImg().getUrl());

        return "userMyPage";
    }

    // 정보 수정 페이지 넘어가기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/userModify")
    public String userModify(Principal principal, UserCreateForm userCreateForm, Model model){
        SiteUser siteUser = userService.getUser(principal.getName());
        userCreateForm.setUsername(siteUser.getUsername());
        userCreateForm.setEmail(siteUser.getEmail());
        model.addAttribute("imgUrl", siteUser.getProfileImg().getUrl());
        return "userModify_form";
    }

    // 정보 수정
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/userModify")
    public String userModify(Principal principal,UserCreateForm userCreateForm,Model model, BindingResult bindingResult,@RequestParam MultipartFile imgData)throws IOException{

        // 만약 현재 로그인한 username과 UserCreateForm에 넘어온 username이 다르다면
        if(!userCreateForm.getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 접근 입니다");
        }

        // 비밀번호가 입력 되어 있으면 비밀번호 변경 처리
        if(!userCreateForm.getPassword1().equals("") || !userCreateForm.getPassword2().equals("")){
            if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
                bindingResult.reject("passwordInCorrect","2개의 패스워드가 일치하지 않습니다.");
                model.addAttribute("imgUrl",this.userService.getUser(userCreateForm.getUsername()).getProfileImg().getUrl());
                return "userModify_form";
            }else{
                this.userService.changePassword(userCreateForm.getUsername(),userCreateForm.getPassword1());
            }
        }

        // 넘어온 프로필 파일이 달라져 있으면
        if(!imgData.getOriginalFilename().equals("")){

            // 기존 파일이 default img 인지 확인 후 아니라면
            if(!this.userService.getUser(userCreateForm.getUsername()).getProfileImg().getUrl().equals("/Image/default_Img.png")){
                // 기존 파일 삭제
                profileImgService.deletFile(this.userService.getUser(userCreateForm.getUsername()).getProfileImg().getUrl());
            }

            // 새 파일 저장 및 경로 변경
            userService.changeProfile(userCreateForm.getUsername(),this.profileImgService.saveFile(imgData));
        }

        return "redirect:/user/userMyPage";
    }

}
