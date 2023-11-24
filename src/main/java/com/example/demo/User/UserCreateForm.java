package com.example.demo.User;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class UserCreateForm {
    @Size(min=3, max=25)
    @NotEmpty
    private String userName;

    @NotEmpty(message = "비밀번호는 필수 항목 입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수 항목 입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수 항목 입니다.")
    @Email
    private String email;
}
