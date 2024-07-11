package com.auth.boilerplate.user.controller;

import com.auth.boilerplate.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {
    @RequestMapping("/join")
    public CommonResponse<?> join() {

    }
}
