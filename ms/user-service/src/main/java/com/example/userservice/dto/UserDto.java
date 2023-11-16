package com.example.userservice.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserDto {

    private String email;
    private String name;
    private String pwd;
    private String userId;
    private LocalDateTime createdAt;

    private String encryptedPwd;
}
