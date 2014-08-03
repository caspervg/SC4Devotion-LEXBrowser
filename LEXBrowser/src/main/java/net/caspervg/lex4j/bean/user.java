package net.caspervg.lex4j.bean;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Casper
 * Date: 21/11/13
 */
public class User {

    private int id;
    private String fullname;
    private String username;
    private Date registered;
    private Date lastLogin;

    @SerializedName("user_level")
    private int userLevel;

    private String email;
    @SerializedName("login_count")

    private int loginCount;

    @SerializedName("is_active")
    private boolean active;

    @SerializedName("is_donator")
    private boolean donator;

    @SerializedName("is_rater")
    private boolean rater;

    @SerializedName("is_uploader")
    private boolean uploader;

    @SerializedName("is_author")
    private boolean author;

    @SerializedName("is_admin")
    private boolean admin;

    public User() {
        /*
         * Hacky solution to avoid having to write seperate Date parsing rules for User.lastLogin, which uses yyyyMMddHHmmss
         * compared to the rest of the LEX API, which uses yyyyMMdd. We could parse, but there would be timezone issues.
         * Since the LEX API handles Basic Auth as a "log in", the lastLogin would always equal the current time anyhow.
         */
        this.lastLogin = new Date();
    }

    public int getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getUsername() {
        return username;
    }

    public Date getRegistered() {
        return registered;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public String getEmail() {
        return email;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDonator() {
        return donator;
    }

    public boolean isRater() {
        return rater;
    }

    public boolean isUploader() {
        return uploader;
    }

    public boolean isAuthor() {
        return author;
    }

    public boolean isAdmin() {
        return admin;
    }
}