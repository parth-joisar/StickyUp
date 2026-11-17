package com.mycompany.stickyupmaven.controller;

import com.mycompany.stickyupmaven.service.SessionRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogoutController {

    private final SessionRegistry sessionRegistry;

    public LogoutController(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            sessionRegistry.remove(session.getId());
            session.invalidate();
        }

        return ResponseEntity.noContent()
                .cacheControl(CacheControl.noStore())
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .build();
    }
}
