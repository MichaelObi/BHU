package net.devdome.bhu.authentication;

import com.google.gson.JsonObject;

public interface ServerAuthenticator {

    /**
     * @param email Email Address Provided
     * @param pass  Password Provided
     * @return Object with signing
     * @throws Exception
     */
    public JsonObject userSignIn(final String email, final String pass);
}
