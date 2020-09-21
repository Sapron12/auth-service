package com.example.userservice

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class UserController {

    @GetMapping("/user-service/status")
    fun status(): String = "$this OK"

}
