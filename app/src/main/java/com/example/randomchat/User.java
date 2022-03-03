package com.example.randomchat;

//Custom class to define a user
public class User {
    String userID; // Firebase userID
    String email; // Signed in email
    String name; // Name

    public User() {
    }

    public User(String userID, String email, String name) {
        this.userID = userID;
        this.email = email;
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
