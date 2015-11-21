package net.devdome.bhu.authentication;

public class AccountConfig {


    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "net.devdome.bhu";

    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "BHU";

    public static ServerAuthenticator serverAuthenticator = new HttpServerAuthenticator();
    public static String TOKEN_TYPE = "General";
}
