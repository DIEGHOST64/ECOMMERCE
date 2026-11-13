package com.ecommerce.auth.domain.port.out;

/**
 * Puerto de salida para el cifrado y verificacion de contrasenas.
 */
public interface EncrypterGateway {

    String encrypt(String password);

    boolean checkPass(String rawPassword, String encodedPassword);
}
