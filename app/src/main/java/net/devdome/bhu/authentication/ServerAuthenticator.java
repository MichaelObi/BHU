package net.devdome.bhu.authentication;

import android.support.annotation.Nullable;

import com.google.gson.JsonObject;

public interface ServerAuthenticator {

    /**
     * @param email Email Address Provided
     * @param pass  Password Provided
     * @return Object with signing
     * @throws Exception
     */
    public JsonObject userSignIn(final String email, final String pass, @Nullable String gcmToken);


    public JsonObject userRegistration(final String email, final String firstName, final String lastName, final String matricNo, final String password, final String confirmPassword, final String departmentCode, final String level);
}
