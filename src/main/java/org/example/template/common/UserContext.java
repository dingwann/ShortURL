package org.example.template.common;

public class UserContext {
    private static final ThreadLocal<String> userHolder = new ThreadLocal<>();

    public static void setUser(String user) {
        userHolder.set(user);
    }

    public static String getUser() {
        return userHolder.get();
    }

    public static void removeUser() {
        userHolder.remove();
    }
}