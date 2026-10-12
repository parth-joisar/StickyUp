package com.mycompany.stickyupmaven.dao;

import com.mycompany.stickyupmaven.config.HibernateUtil;
import com.mycompany.stickyupmaven.model.PasswordResetToken;
import com.mycompany.stickyupmaven.model.User;
import java.time.LocalDateTime;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PasswordResetDAO {

    public void saveToken(String email, String tokenHash, LocalDateTime expiresAt) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createMutationQuery("delete from PasswordResetToken where lower(email) = :email")
                    .setParameter("email", email.toLowerCase())
                    .executeUpdate();
            session.persist(new PasswordResetToken(email.toLowerCase(), tokenHash, expiresAt));
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public boolean resetPassword(String tokenHash, String newPassword) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            PasswordResetToken resetToken = session.createQuery(
                            "from PasswordResetToken where tokenHash = :tokenHash",
                            PasswordResetToken.class)
                    .setParameter("tokenHash", tokenHash)
                    .uniqueResult();

            if (resetToken == null || !resetToken.getExpiresAt().isAfter(LocalDateTime.now())) {
                transaction.rollback();
                return false;
            }

            User user = session.createQuery(
                            "from User where lower(email) = :email", User.class)
                    .setParameter("email", resetToken.getEmail().toLowerCase())
                    .setMaxResults(1)
                    .uniqueResult();

            if (user == null) {
                transaction.rollback();
                return false;
            }

            user.setPassword(newPassword);
            session.remove(resetToken);
            transaction.commit();
            return true;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
