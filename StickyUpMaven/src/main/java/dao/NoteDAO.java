package com.mycompany.stickyupmaven.dao;

import com.mycompany.stickyupmaven.config.HibernateUtil;
import com.mycompany.stickyupmaven.model.Note;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class NoteDAO {

    public boolean saveNote(Note note) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.persist(note);

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

    public List<Note> getNotesByUser(int userId) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                    "FROM Note WHERE userId = :userId ORDER BY id",
                    Note.class
            )
            .setParameter("userId", userId)
            .getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    public Note getNoteById(int noteId, int userId) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                    "FROM Note WHERE id = :id AND userId = :userId",
                    Note.class
            )
            .setParameter("id", noteId)
            .setParameter("userId", userId)
            .uniqueResult();

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    public boolean updateNote(Note note) {

    Transaction transaction = null;

    try (Session session = HibernateUtil.getSessionFactory().openSession()) {

        transaction = session.beginTransaction();

        Note existing = session.find(Note.class, note.getId());

        if (existing == null || existing.getUserId() != note.getUserId()) {
            transaction.rollback();
            return false;
        }

        existing.setTitle(note.getTitle());
        existing.setContent(note.getContent());
        existing.setXPosition(note.getXPosition());
        existing.setYPosition(note.getYPosition());
        existing.setBackgroundColor(note.getBackgroundColor());

        session.flush();

        transaction.commit();

        System.out.println(
                "POSITION SAVED: X=" + existing.getXPosition()
                + " Y=" + existing.getYPosition()
        );

        return true;

    } catch (Exception e) {

        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }

        e.printStackTrace();

        return false;
    }
}

    public boolean setFavorite(int noteId, int userId, boolean favorite) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            Note note = session.createQuery(
                    "FROM Note WHERE id = :id AND userId = :userId",
                    Note.class
            )
            .setParameter("id", noteId)
            .setParameter("userId", userId)
            .uniqueResult();

            if (note == null) {
                transaction.rollback();
                return false;
            }

            note.setFavorite(favorite);

            transaction.commit();

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();

            return false;
        }
    }

    public boolean setArchived(int noteId, int userId, boolean archived) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            Note note = session.createQuery(
                    "FROM Note WHERE id = :id AND userId = :userId",
                    Note.class
            )
            .setParameter("id", noteId)
            .setParameter("userId", userId)
            .uniqueResult();

            if (note == null) {
                transaction.rollback();
                return false;
            }

            note.setArchived(archived);

            transaction.commit();

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();

            return false;
        }
    }

    public boolean setPinned(int noteId, int userId, boolean pinned) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            Note note = session.createQuery(
                    "FROM Note WHERE id = :id AND userId = :userId",
                    Note.class
            )
            .setParameter("id", noteId)
            .setParameter("userId", userId)
            .uniqueResult();

            if (note == null) {
                transaction.rollback();
                return false;
            }

            note.setPinned(pinned);

            transaction.commit();

            return true;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();

            return false;
        }
    }

    public boolean deleteNote(int noteId, int userId) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            Note note = session.createQuery(
                    "FROM Note WHERE id = :id AND userId = :userId",
                    Note.class
            )
            .setParameter("id", noteId)
            .setParameter("userId", userId)
            .uniqueResult();

            if (note != null) {
                session.remove(note);
            }

            transaction.commit();

            return note != null;

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();

            return false;
        }
    }
}