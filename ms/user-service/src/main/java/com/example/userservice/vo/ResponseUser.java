package com.example.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL) // 클래스 내 필드가 null이 들어갈 때 -> 제외시킴
public class ResponseUser {

    private String email;
    private String name;
    private String userId;

    private List<ResponseOrder> orders;
}
