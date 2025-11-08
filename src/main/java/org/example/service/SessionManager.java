package org.example.service;

import org.example.model.User;
import spark.Request;

public class SessionManager {
    private final UserService userService;

    public SessionManager(UserService userService) {
        this.userService = userService;
    }

    public void loginUser(Request req, User user) {
        if (req == null || user == null) return;
        req.session(true).attribute("userId", user.getId());
    }

    public void logout(Request req) {
        if (req == null) return;
        if (req.session(false) != null) req.session().removeAttribute("userId");
    }

    public User getLoggedUser(Request req) {
        if (req == null) return null;
        if (req.session(false) == null) return null;
        Object v = req.session().attribute("userId");
        if (v == null) return null;
        String id = (String) v;
        return userService.get(id);
    }
}

