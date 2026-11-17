package com.mycompany.stickyupmaven.controller;

import com.mycompany.stickyupmaven.dao.PasswordResetDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {

    private final PasswordResetDAO resetDAO = new PasswordResetDAO();

    @PostMapping("/forgot-password")
    public String saveResetRequest(@RequestParam("email") String email) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        if (normalizedEmail.isEmpty()) {
            return "redirect:/forgot-password.html?status=unavailable";
        }

        try {
            resetDAO.saveManualRequest(normalizedEmail);
            return "redirect:/forgot-password.html?status=pending";
        } catch (Exception e) {
            System.err.println("Could not save a manual password reset request.");
            e.printStackTrace();
            return "redirect:/forgot-password.html?status=unavailable";
        }
    }
}
