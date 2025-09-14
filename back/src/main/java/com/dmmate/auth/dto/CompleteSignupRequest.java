package com.dmmate.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class CompleteSignupRequest {
    @NotBlank
    private String nickname;

    @NotBlank
    private String password;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}