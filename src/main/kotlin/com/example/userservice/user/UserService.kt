package com.example.userservice.user

import com.example.userservice.user.User
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name= "feignUserService", url = "http://localhost:8300/")
interface UserService {
    @GetMapping("/user/{username}")
    fun getUserByUsername(@PathVariable("username") username: String): User
}
