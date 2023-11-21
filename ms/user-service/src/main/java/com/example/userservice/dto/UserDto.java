package com.example.userservice.dto;

import com.example.userservice.vo.ResponseOrder;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class UserDto {

    private String email;
    private String name;
    private String pwd;
    private String userId;
    private LocalDateTime createdAt;

    private String encryptedPwd;

    private List<ResponseOrder> orders;
}
