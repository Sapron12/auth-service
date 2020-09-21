package com.example.userservice

class JwtConfig {
    val uri: String? = "/user-service/auth"

    val header: String? = "Authorization"

    val prefix: String? = "Bearer "

    val expiration = 24*60*60*10

    val secret: String? = "JwtSecretKey"
}
