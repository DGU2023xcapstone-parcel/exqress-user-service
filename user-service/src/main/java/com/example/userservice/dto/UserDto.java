package com.example.userservice.dto;

import lombok.Data;

@Data
public class UserDto {

    private String email;
    private String name;
    private String password;
    private String userId;
    private String phoneNumber;

    private String encryptedPwd;
}
