package com.mycompany.stickyupmaven.controller;

import com.mycompany.stickyupmaven.dao.PasswordResetDAO;
import com.mycompany.stickyupmaven.dao.UserDAO;
import com.mycompany.stickyupmaven.model.User;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_LIFETIME_MINUTES = 30;

    private final UserDAO userDAO = new UserDAO();
    private final PasswordResetDAO resetDAO = new PasswordResetDAO();

    @PostMapping("/forgot-password")
    public String sendResetLink(
            @RequestParam("email") String email,
            HttpServletRequest request) {

        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        if (normalizedEmail.isEmpty()) {
            return "redirect:/forgot-password.html?status=sent";
        }

        try {
            JavaMailSenderImpl mailSender = createMailSender();
            User user = userDAO.findByEmail(normalizedEmail);

            if (user != null) {
                byte[] tokenBytes = new byte[TOKEN_BYTES];
                RANDOM.nextBytes(tokenBytes);
                String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
                LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(TOKEN_LIFETIME_MINUTES);

                resetDAO.saveToken(normalizedEmail, sha256(rawToken), expiresAt);

                String baseUrl = configuredBaseUrl(request);
                String resetLink = baseUrl + request.getContextPath()
                        + "/reset-password.html?token=" + rawToken;
                sendEmail(mailSender, normalizedEmail, resetLink);
            }
        } catch (IllegalStateException e) {
            return "redirect:/forgot-password.html?status=unavailable";
        } catch (Exception e) {
            System.err.println("Could not send a password reset email.");
            e.printStackTrace();
        }

        // This response is the same whether the email exists or not.
        return "redirect:/forgot-password.html?status=sent";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword) {

        if (!isStrongPassword(password) || !password.equals(confirmPassword)) {
            return "redirect:/reset-password.html?token=" + safeToken(token) + "&status=invalid";
        }

        try {
            if (token == null || token.isBlank() || !resetDAO.resetPassword(sha256(token), password)) {
                return "redirect:/reset-password.html?status=expired";
            }
            return "redirect:/login.html?reset=success";
        } catch (Exception e) {
            System.err.println("Could not reset the account password.");
            e.printStackTrace();
            return "redirect:/reset-password.html?token=" + safeToken(token) + "&status=error";
        }
    }

    private JavaMailSenderImpl createMailSender() {
        String host = environment("SMTP_HOST");
        String username = environment("SMTP_USERNAME");
        String password = environment("SMTP_PASSWORD");
        String from = environment("MAIL_FROM");

        if (host == null || username == null || password == null || from == null) {
            throw new IllegalStateException("SMTP settings are missing.");
        }

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(parsePort(environment("SMTP_PORT")));
        sender.setUsername(username);
        sender.setPassword(password);
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());
        sender.getJavaMailProperties().put("mail.smtp.auth", "true");
        sender.getJavaMailProperties().put("mail.smtp.starttls.enable", "true");
        sender.getJavaMailProperties().put("mail.smtp.starttls.required", "true");
        sender.getJavaMailProperties().put("mail.smtp.connectiontimeout", "10000");
        sender.getJavaMailProperties().put("mail.smtp.timeout", "10000");
        sender.getJavaMailProperties().put("mail.smtp.writetimeout", "10000");
        return sender;
    }

    private void sendEmail(JavaMailSenderImpl sender, String email, String resetLink) {
        String from = environment("MAIL_FROM");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Reset your StickyUp password");
        message.setText("We received a request to reset your StickyUp password.\n\n"
                + "Use this one-time link within " + TOKEN_LIFETIME_MINUTES + " minutes:\n"
                + resetLink + "\n\n"
                + "If you did not request this, you can ignore this email.");
        sender.send(message);
    }

    private String configuredBaseUrl(HttpServletRequest request) {
        String configured = environment("APP_BASE_URL");
        if (configured != null) {
            return configured.replaceAll("/+$", "");
        }

        int port = request.getServerPort();
        String origin = request.getScheme() + "://" + request.getServerName();
        if (!("http".equalsIgnoreCase(request.getScheme()) && port == 80)
                && !("https".equalsIgnoreCase(request.getScheme()) && port == 443)) {
            origin += ":" + port;
        }
        return origin;
    }

    private int parsePort(String value) {
        if (value == null) {
            return 587;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("SMTP_PORT must be a number.", e);
        }
    }

    private String environment(String name) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable.", e);
        }
    }

    private boolean isStrongPassword(String password) {
        return password != null && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^A-Za-z0-9].*");
    }

    private String safeToken(String token) {
        return token != null && token.matches("[A-Za-z0-9_-]{20,100}") ? token : "";
    }
}
