package com.example.demo.User;

import com.example.demo.EmailService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ProfileImgService profileImgService;
    private  final EmailService emailService;

    private static final char[] rndAllCharacters = new char[]{
            //number
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            //uppercase
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            //lowercase
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            //special symbols
            '@', '$', '!', '%', '*', '?', '&'
    };

    private String getRandomPassword1(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();

        int rndAllCharactersLength = rndAllCharacters.length;
        for (int i = 0; i < length; i++) {
            stringBuilder.append(rndAllCharacters[random.nextInt(rndAllCharactersLength)]);
        }

        String randomPassword = stringBuilder.toString();

        // 최소 8자리에 대문자, 소문자, 숫자, 특수문자 각 1개 이상 포함
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}";
        if (!Pattern.matches(pattern, randomPassword)) {
            return getRandomPassword1(length);    //비밀번호 조건(패턴)에 맞지 않는 경우 메서드 재실행
        }
        return randomPassword;
    }

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


    // 비밀번호 찾기
    @GetMapping("/userFindPassword")
    public String userFindPassword(UserCreateForm userCreateForm){
        return "userFindPassword_form";
    }

    @PostMapping("/userFindPassword")
    public String userFindPassword(UserCreateForm userCreateForm, BindingResult bindingResult){

        // 값 검증
        if(userCreateForm.getUsername().equals("") || userCreateForm.getEmail().equals("")){
            bindingResult.reject("","이름과 이메일은 필수 입니다.");
            return "userFindPassword_form";
        }

        // 있는지 확인
        SiteUser user = this.userService.getUserForFindPassword(userCreateForm.getUsername());
        // 이름과 이메일이 맞는지 확인
        if(user == null){
            bindingResult.reject("","이름 또는 이메일을 확인 해주세요");
            return "userFindPassword_form";
        }else if(!user.getEmail().equals(userCreateForm.getEmail())) {
            bindingResult.reject("","이름 또는 이메일을 확인 해주세요");
            return "userFindPassword_form";
        }

        // 임시비밀번호 생성
        String tempPassword = getRandomPassword1(8);

        // 생성된 임시 비밀번호 메일로 전송
        if(emailService.sendChangePasswordMail(user, tempPassword) == false){
            bindingResult.reject("","메일이 안보내졌어유");
            return "userFindPassword_form";
        }

        // 임시 비밀번호로 비밀번호 변경
        userService.changePassword(user.getUsername(),tempPassword);

        return "userLogin_form";
    }

}
