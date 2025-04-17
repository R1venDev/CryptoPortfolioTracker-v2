package com.cryptoportfolio.models.responses;

import java.util.Objects;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyResponse {

    private String username;
    private String authToken;
}
