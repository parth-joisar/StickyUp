package com.mycompany.stickyupmaven.controller;

import com.mycompany.stickyupmaven.dao.UserDAO;
import com.mycompany.stickyupmaven.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignupController {

    private final UserDAO userDAO = new UserDAO();

    @PostMapping("/signup")
    public String signup(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        User user = new User(name, email, password);

        boolean saved = userDAO.saveUser(user);

        if (saved) {
            return "redirect:/login.html";
        }

        return "redirect:/signup.html?error=true";
    }
}