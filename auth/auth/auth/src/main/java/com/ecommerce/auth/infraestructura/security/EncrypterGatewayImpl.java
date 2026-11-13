package com.ecommerce.auth.infraestructura.security;

import com.ecommerce.auth.domain.port.out.EncrypterGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncrypterGatewayImpl implements EncrypterGateway {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encrypt(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean checkPass(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null || encodedPassword.isBlank()) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
