package org.example;

public class User {
    private static String email;
    private static String password;

    public User (String email, String password){
        super();
        this.email = email;
        this.password = password;
    }

    public static String getEmail() {
        return email;
    }

    public static String getPassword() {
        return password;
    }

}

