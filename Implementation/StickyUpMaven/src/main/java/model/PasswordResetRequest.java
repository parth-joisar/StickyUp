package com.mycompany.stickyupmaven.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_requests")
public class PasswordResetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 320)
    private String email;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(nullable = false, length = 20)
    private String status;

    public PasswordResetRequest() {
    }

    public PasswordResetRequest(String email, LocalDateTime requestedAt, String status) {
        this.email = email;
        this.requestedAt = requestedAt;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public String getStatus() {
        return status;
    }
}
