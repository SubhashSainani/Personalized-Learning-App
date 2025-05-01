package com.example.personalizedlearning.models;

import java.util.List;

public class User {
    private String username;
    private String email;
    private String phone;
    private String password;
    private List<String> interests;

    public User(String username, String email, String phone, String password) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public List<String> getInterests() { return interests; }

    public void setInterests(List<String> interests) { this.interests = interests; }
}