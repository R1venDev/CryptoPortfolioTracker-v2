package com.cryptoportfolio.models.requests;

import java.util.Objects;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyRequest {

    private String username;
    private String authToken;
}
