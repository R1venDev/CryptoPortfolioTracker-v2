package com.cryptoportfolio.models.responses;

import java.util.Objects;
import lombok.*;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String username;
    private String authToken;
    private boolean isAdminRight;
    private boolean isMainAdmin;
    private boolean isNewUser;

}
