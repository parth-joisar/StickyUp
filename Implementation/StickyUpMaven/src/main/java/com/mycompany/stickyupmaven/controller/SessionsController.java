package com.mycompany.stickyupmaven.controller;

import com.mycompany.stickyupmaven.model.User;
import com.mycompany.stickyupmaven.service.SessionRegistry;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SessionsController {

    private final SessionRegistry sessionRegistry;

    public SessionsController(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @GetMapping("/sessions")
    @ResponseBody
    public ResponseEntity<List<SessionRegistry.SessionSummary>> listSessions(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.noStore())
                .body(sessionRegistry.listForUser(user.getId(), session.getId()));
    }

    @PostMapping(value = "/sessions/revoke-others", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RevokeResult> revokeOtherSessions(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int revoked = sessionRegistry.revokeOtherSessions(user.getId(), session.getId());
        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.noStore())
                .body(new RevokeResult(revoked));
    }

    public record RevokeResult(int revoked) {
    }
}
