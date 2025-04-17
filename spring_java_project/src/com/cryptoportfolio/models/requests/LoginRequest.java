package com.cryptoportfolio.models.requests;

import java.util.Objects;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    private String username;
    private String password;
}
