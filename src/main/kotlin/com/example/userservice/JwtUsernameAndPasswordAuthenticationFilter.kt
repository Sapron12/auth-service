package com.example.userservice

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.io.IOException
import java.util.Collections
import java.util.Date
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


class JwtUsernameAndPasswordAuthenticationFilter(// We use auth manager to validate the user credentials
        private val authManager: AuthenticationManager, private val jwtConfig: JwtConfig) : UsernamePasswordAuthenticationFilter() {
    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        return try {
            val creds = ObjectMapper().readValue(request.inputStream, UserCredentials::class.java)
            val authToken = UsernamePasswordAuthenticationToken(
                    creds.username, creds.password, Collections.emptyList())
            authManager.authenticate(authToken)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    // Upon successful authentication, generate a token.
    // The 'auth' passed to successfulAuthentication() is the current authenticated user.
    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse, chain: FilterChain?,
                                          auth: Authentication) {
        val now = System.currentTimeMillis()
        val token = Jwts.builder()
                .setSubject(auth.name) // Convert to list of strings.
                // This is important because it affects the way we get them back in the Gateway.
                .claim("authorities", auth.authorities.stream()
                        .map { obj: GrantedAuthority -> obj.authority }.collect(Collectors.toList()))
                .setIssuedAt(Date(now))
                .setExpiration(Date(now + jwtConfig.expiration * 1000)) // in milliseconds
                .signWith(SignatureAlgorithm.HS512, jwtConfig.secret!!.toByteArray())
                .compact()

        // Add token to header
        response.addHeader(jwtConfig.header, jwtConfig.prefix + token)
    }

    // A (temporary) class just to represent the user credentials
    private class UserCredentials {
         var username: String? = null
         var password: String? = null
    }

    init {

        // By default, UsernamePasswordAuthenticationFilter listens to "/login" path.
        // In our case, we use "/auth". So, we need to override the defaults.
        setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher(jwtConfig.uri, "POST"))
    }
}
