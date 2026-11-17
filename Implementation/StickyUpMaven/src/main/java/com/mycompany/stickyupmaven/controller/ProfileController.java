package com.mycompany.stickyupmaven.controller;

import com.mycompany.stickyupmaven.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    @ResponseBody
    public ResponseEntity<?> profile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.noStore())
                .body(new ProfileInfo(user.getName(), user.getEmail()));
    }

    public record ProfileInfo(String name, String email) {
    }
}
