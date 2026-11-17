package com.mycompany.stickyupmaven.dao;

import com.mycompany.stickyupmaven.config.HibernateUtil;
import com.mycompany.stickyupmaven.model.PasswordResetRequest;
import java.time.LocalDateTime;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PasswordResetDAO {

    public void saveManualRequest(String email) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(new PasswordResetRequest(
                    email.trim().toLowerCase(),
                    LocalDateTime.now(),
                    "PENDING"));
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
