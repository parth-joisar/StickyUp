package com.mycompany.stickyupmaven.controller;

import com.mycompany.stickyupmaven.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    @ResponseBody
    public User profile(HttpSession session) {

        User user =
                (User) session.getAttribute("user");

        if (user == null) {
            return null;
        }

        return user;
    }
}