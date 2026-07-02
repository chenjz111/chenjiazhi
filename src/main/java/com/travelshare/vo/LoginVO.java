package com.travelshare.vo;

import lombok.Data;

@Data
public class LoginVO {

    private Long id;

    private String username;

    private String email;

    private String role;

    private String token;
}