package com.example.userservice

import com.example.userservice.user.UserService
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

    @Autowired
    private lateinit var userService: UserService

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        // hard coding the users. All passwords must be encoded.
        val appUser = userService.getUserByUsername(username)

        val grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_" + appUser.role)


        return User(appUser.username, encoder!!.encode(appUser.password), grantedAuthorities)

    }
}

















//val appUser = userService.getUserByUsername(username)
//
//val grantedAuthorities = AuthorityUtils
//        .commaSeparatedStringToAuthorityList("ROLE_" + appUser.role)
//
//
//return User(appUser.username, appUser.password, grantedAuthorities)
