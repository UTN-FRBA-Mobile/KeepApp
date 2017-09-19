package com.utn.mobile.keepapp.domain;

/**
 * Created by julis on 19/9/2017.
 */

public class Usuario {
    private String username;
    private String fullName;
    private String email;
    private String provider;
    private String profilePic;

    public Usuario() {
    }

    public Usuario(String username, String email, String provider) {
        this.username = username;
        this.email = email;
        this.provider = provider;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
