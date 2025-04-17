package com.cryptoportfolio.models;

import java.io.Serializable;
import java.util.Objects;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel implements Serializable {

    private static final long serialVersionUID = 2166465282947561034L;

    private String username;
    private String password;
    private Boolean isNewUser;
}
