package com.example.demo.User;

import ch.qos.logback.classic.model.LevelModel;
import lombok.Getter;

@Getter
public enum UserRole {

    ADMIN("ROLE_ADMIN"),USER("ROLE_USER");

    UserRole(String value){
        this.value = value;
    }

    private String value;

}
