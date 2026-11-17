package com.mycompany.stickyupmaven.service;

import com.mycompany.stickyupmaven.model.User;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/** Tracks authenticated sessions for the lifetime of this application instance. */
@Component
public class SessionRegistry {

    private static final String REGISTRATION_ATTRIBUTE = SessionRegistry.class.getName() + ".registration";
    private final Map<String, RegisteredSession> sessions = new ConcurrentHashMap<>();

    public void register(HttpSession session, User user, String userAgent) {
        long now = System.currentTimeMillis();
        String sessionId = session.getId();
        sessions.put(sessionId, new RegisteredSession(
                sessionId,
                user.getId(),
                session,
                describeDevice(userAgent),
                session.getCreationTime() > 0 ? session.getCreationTime() : now
        ));
        session.setAttribute(REGISTRATION_ATTRIBUTE, new RegistrationBinding(this, sessionId));
    }

    public List<SessionSummary> listForUser(int userId, String currentSessionId) {
        return sessions.values().stream()
                .filter(entry -> entry.userId == userId)
                .filter(this::isActive)
                .map(entry -> new SessionSummary(
                        entry.device,
                        entry.createdAt,
                        lastActiveAt(entry),
                        entry.sessionId.equals(currentSessionId)
                ))
                .sorted(Comparator.comparingLong(SessionSummary::lastActiveAt).reversed())
                .toList();
    }

    public int revokeOtherSessions(int userId, String currentSessionId) {
        int revoked = 0;
        for (RegisteredSession entry : sessions.values()) {
            if (entry.userId != userId || entry.sessionId.equals(currentSessionId)) {
                continue;
            }

            try {
                Object sessionUser = entry.session.getAttribute("user");
                if (sessionUser instanceof User user && user.getId() == userId) {
                    entry.session.invalidate();
                    revoked++;
                }
            } catch (IllegalStateException ignored) {
                // The container already expired or invalidated this session.
            } finally {
                sessions.remove(entry.sessionId, entry);
            }
        }
        return revoked;
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }

    private boolean isActive(RegisteredSession entry) {
        try {
            Object sessionUser = entry.session.getAttribute("user");
            if (sessionUser instanceof User user && user.getId() == entry.userId) {
                return true;
            }
        } catch (IllegalStateException ignored) {
            // The container already expired or invalidated this session.
        }
        sessions.remove(entry.sessionId, entry);
        return false;
    }

    private long lastActiveAt(RegisteredSession entry) {
        try {
            return Math.max(entry.createdAt, entry.session.getLastAccessedTime());
        } catch (IllegalStateException ignored) {
            sessions.remove(entry.sessionId, entry);
            return entry.createdAt;
        }
    }

    private String describeDevice(String userAgent) {
        String agent = userAgent == null ? "" : userAgent;
        String lower = agent.toLowerCase();

        String browser = agent.contains("Edg/") ? "Microsoft Edge"
                : agent.contains("OPR/") ? "Opera"
                : agent.contains("Firefox/") ? "Firefox"
                : agent.contains("Chrome/") ? "Chrome"
                : agent.contains("Safari/") ? "Safari"
                : "Browser";

        String operatingSystem = lower.contains("windows") ? "Windows"
                : lower.contains("android") ? "Android"
                : lower.contains("iphone") || lower.contains("ipad") ? "iOS"
                : lower.contains("mac os") || lower.contains("macintosh") ? "macOS"
                : lower.contains("linux") ? "Linux"
                : "device";

        return browser + " on " + operatingSystem;
    }

    private static final class RegisteredSession {
        private final String sessionId;
        private final int userId;
        private final HttpSession session;
        private final String device;
        private final long createdAt;

        private RegisteredSession(
                String sessionId,
                int userId,
                HttpSession session,
                String device,
                long createdAt) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.session = session;
            this.device = device;
            this.createdAt = createdAt;
        }
    }

    private static final class RegistrationBinding implements HttpSessionBindingListener {
        private final SessionRegistry registry;
        private final String sessionId;

        private RegistrationBinding(SessionRegistry registry, String sessionId) {
            this.registry = registry;
            this.sessionId = sessionId;
        }

        @Override
        public void valueBound(HttpSessionBindingEvent event) {
            // The session is registered before this binding is attached.
        }

        @Override
        public void valueUnbound(HttpSessionBindingEvent event) {
            registry.remove(sessionId);
        }
    }

    public record SessionSummary(
            String device,
            long createdAt,
            long lastActiveAt,
            boolean current) {
    }
}
