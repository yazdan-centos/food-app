package com.mapnaom.foodapp.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -8091879091924046844L;

    private String token;
    private String refreshToken;
    private String type;
    private String username;
    private List<String> authorities;
    private long expiresIn;
}
