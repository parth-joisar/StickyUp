package com.mycompany.stickyupmaven.controller;

import com.mycompany.stickyupmaven.dao.UserDAO;
import com.mycompany.stickyupmaven.model.User;
import com.mycompany.stickyupmaven.service.SessionRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserDAO userDAO = new UserDAO();
    private final SessionRegistry sessionRegistry;

    public LoginController(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpServletRequest request) {

        User user = userDAO.loginUser(email, password);

        if (user != null) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                session = request.getSession(true);
            } else {
                sessionRegistry.remove(session.getId());
                request.changeSessionId();
            }

            session.setMaxInactiveInterval(30 * 60);
            session.setAttribute("user", user);
            sessionRegistry.register(session, user, request.getHeader("User-Agent"));
            return "redirect:/workspace.html";
        }

        return "redirect:/login.html?error=true";
    }
}
