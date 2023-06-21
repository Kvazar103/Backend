package com.example.backend.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerLoginPasswordRoleDTO {
    private String login;
    private String password;
    private String role="ROLE_USER";// ролі в б мають починатися з ROLE_

}
