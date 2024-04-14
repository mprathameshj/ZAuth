package com.example.ZAuth.DataEncryptor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptEncrypt {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static  String encrypt(String token){
        return encoder.encode(token);
    }

}
