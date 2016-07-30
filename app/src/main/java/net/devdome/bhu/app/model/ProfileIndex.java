package net.devdome.bhu.app.model;

public class ProfileIndex {
    public String name;
    public String email;
    public int idOnServer;
    public String avatarUrl;

    public ProfileIndex() {
    }

    public ProfileIndex(String name, String email, int idOnServer, String avatarUrl) {
        this.name = name;
        this.email = email;
        this.idOnServer = idOnServer;
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(int idOnServer) {
        this.idOnServer = idOnServer;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}