package com.cryptoportfolio.models.responses;

import java.util.Objects;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailureResponse {

    private String message;
}
