// app/src/main/java/com/av/avmessenger/SessionManager.java
package com.av.avmessenger;

public class SessionManager {
    private static SessionManager instance;
    private Users currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void setCurrentUser(Users user) {
        this.currentUser = user;
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
}