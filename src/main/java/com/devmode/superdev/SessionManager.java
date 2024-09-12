package com.devmode.superdev;

public class SessionManager {
    private static SessionManager instance;
    private String username;
    private int id;
    private String role;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setRole(String role){
        this.role = role;
    }

    public String getRole(){
        return this.role;
    }

    public void clearSession() {
        this.username = null;
    }
}
