package com.mycompany.stickyupmaven.config;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateTest {

    public static void main(String[] args) {

        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();

            transaction = session.beginTransaction();

            transaction.commit();

            System.out.println("Hibernate connected to MySQL successfully!");

        } catch (HibernateException e) {

            if (transaction != null) {
                transaction.rollback();
            }


        } finally {

            if (session != null) {
                session.close();
            }

            HibernateUtil.shutdown();
        }
    }
}   