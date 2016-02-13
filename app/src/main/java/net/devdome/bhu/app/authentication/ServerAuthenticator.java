package net.devdome.bhu.app.authentication;

import com.google.gson.JsonObject;

public interface ServerAuthenticator {

    /**
     * @param email Email Address Provided
     * @param pass  Password Provided
     * @return Object with signing
     */
    public JsonObject userSignIn(final String email, final String pass);


    public JsonObject userRegistration(final String email, final String firstName, final String lastName, final String matricNo, final String password, final String confirmPassword, final String departmentCode, final String level);
}
