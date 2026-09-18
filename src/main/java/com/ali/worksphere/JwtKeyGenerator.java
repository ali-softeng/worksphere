package com.ali.worksphere;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Base64;

public class JwtKeyGenerator {

    public static void main(String[] args) {

        SecretKey secretKey = Jwts.SIG.HS256.key().build();
        String secret = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        System.out.println("secret key : " + secret);
    }
}
