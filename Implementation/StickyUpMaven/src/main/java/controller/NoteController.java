package com.mycompany.stickyupmaven.controller;

import com.mycompany.stickyupmaven.dao.NoteDAO;
import com.mycompany.stickyupmaven.model.Note;
import com.mycompany.stickyupmaven.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class NoteController {

    private final NoteDAO noteDAO = new NoteDAO();

    @GetMapping("/notes")
    @ResponseBody
    public List<Note> getNotes(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return null;
        }

        return noteDAO.getNotesByUser(user.getId());
    }

    @PostMapping("/notes/create")
    @ResponseBody
    public Note createNote(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("x") int x,
            @RequestParam("y") int y,
            @RequestParam("backgroundColor") String backgroundColor,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return null;
        }

        Note note = new Note(
                user.getId(),
                title,
                content,
                x,
                y,
                backgroundColor
        );

        if (noteDAO.saveNote(note)) {
            return note;
        }

        return null;
    }

    @PostMapping("/notes/update")
    @ResponseBody
    public String updateNote(
            @RequestParam("id") int id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("x") int x,
            @RequestParam("y") int y,
            @RequestParam("backgroundColor") String backgroundColor,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "not_logged_in";
        }

        Note note = noteDAO.getNoteById(id, user.getId());

        if (note == null) {
            return "error";
        }

        note.setTitle(title);
        note.setContent(content);
        note.setXPosition(x);
        note.setYPosition(y);
        note.setBackgroundColor(backgroundColor);

        if (noteDAO.updateNote(note)) {
            return "success";
        }

        return "error";
    }

    @PostMapping("/notes/favorite")
    @ResponseBody
    public String setFavorite(
            @RequestParam("id") int id,
            @RequestParam("favorite") boolean favorite,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "not_logged_in";
        }

        return noteDAO.setFavorite(id, user.getId(), favorite)
                ? "success"
                : "error";
    }

    @PostMapping("/notes/archive")
    @ResponseBody
    public String setArchived(
            @RequestParam("id") int id,
            @RequestParam("archived") boolean archived,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "not_logged_in";
        }

        return noteDAO.setArchived(id, user.getId(), archived)
                ? "success"
                : "error";
    }

    @PostMapping("/notes/pin")
    @ResponseBody
    public String setPinned(
            @RequestParam("id") int id,
            @RequestParam("pinned") boolean pinned,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "not_logged_in";
        }

        return noteDAO.setPinned(id, user.getId(), pinned)
                ? "success"
                : "error";
    }

    @PostMapping("/notes/delete")
    @ResponseBody
    public String deleteNote(
            @RequestParam("id") int id,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "not_logged_in";
        }

        if (noteDAO.deleteNote(id, user.getId())) {
            return "success";
        }

        return "error";
    }
}