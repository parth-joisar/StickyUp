package com.mycompany.stickyupmaven.dao;

import com.mycompany.stickyupmaven.config.HibernateUtil;
import com.mycompany.stickyupmaven.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserDAO {

    public User findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM User WHERE lower(email) = :email", User.class)
                    .setParameter("email", email.trim().toLowerCase())
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean saveUser(User user) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.persist(user);

            transaction.commit();

            return true;

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();

            return false;
        }
    }

    public User loginUser(String email, String password) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            User user = session.createQuery(
                    "FROM User WHERE email = :email AND password = :password",
                    User.class
            )
            .setParameter("email", email)
            .setParameter("password", password)
            .uniqueResult();

            return user;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}