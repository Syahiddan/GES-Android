package com.example.shidan.ges.shidan;

import android.content.Context;

import com.example.shidan.ges.model.User;

public class Session {
    private static User user;
    private static Class currentContext;
    public static Class getCurrentContext() {
        return currentContext;
    }

    public static void setCurrentContext(Class currentContext) {
        Session.currentContext = currentContext;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Session.user = user;
    }


}
