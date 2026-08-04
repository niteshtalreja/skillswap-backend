package com.skillswap.dto;

import com.skillswap.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private UserResponseDTO user;

    public AuthResponseDTO(String token, User user) {
        this.token = token;
        this.user = new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCity(),
                user.getBio()
        );
    }
}