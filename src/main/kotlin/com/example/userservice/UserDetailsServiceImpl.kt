package com.example.userservice

import java.util.Arrays
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service // It has to be annotated with @Service.
class UserDetailsServiceImpl : UserDetailsService {
    @Autowired
    private val encoder: BCryptPasswordEncoder? = null

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {

        // hard coding the users. All passwords must be encoded.
        val users: List<AppUser> = listOf(
                AppUser(1, "omar", encoder!!.encode("12345"), "USER"),
                AppUser(2, "admin", encoder.encode("12345"), "ADMIN")
        )
        for (appUser in users) {
            if (appUser.username == username) {

                // Remember that Spring needs roles to be in this format: "ROLE_" + userRole (i.e. "ROLE_ADMIN")
                // So, we need to set it to that format, so we can verify and compare roles (i.e. hasRole("ADMIN")).
                val grantedAuthorities = AuthorityUtils
                        .commaSeparatedStringToAuthorityList("ROLE_" + appUser.role)

                // The "User" class is provided by Spring and represents a model class for user to be returned by UserDetailsService
                // And used by auth manager to verify and check user authentication.
                return User(appUser.username, appUser.password, grantedAuthorities)
            }
        }
        throw UsernameNotFoundException("Username: $username not found")
    }

    // A (temporary) class represent the user saved in the database.
    private class AppUser     // getters and setters ....
    (var id: Int, var username: String, var password: String, var role: String)
}
