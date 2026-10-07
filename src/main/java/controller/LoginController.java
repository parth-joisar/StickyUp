package com.mycompany.stickyupmaven.controller;

import com.mycompany.stickyupmaven.dao.UserDAO;
import com.mycompany.stickyupmaven.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserDAO userDAO = new UserDAO();

    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session) {

        User user = userDAO.loginUser(email, password);

        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/workspace.html";
        }

        return "redirect:/login.html?error=true";
    }
}