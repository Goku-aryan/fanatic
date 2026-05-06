package com.fanatic.dto.user;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String fullName;
    private String bio;
    private String avatarUrl;
}