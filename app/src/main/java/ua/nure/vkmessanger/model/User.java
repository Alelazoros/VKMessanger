package ua.nure.vkmessanger.model;

import java.util.Date;

/**
 * Created by Nickitee on 15.05.2016.
 */
public class User {
    private int id;
    private String full_avatar_orig_url;
    private String firstName, lastName;
    private String birthDayDate;

    public User(int _id, String avatar, String fname, String lname, String bday) {
        this.id = _id;
        this.full_avatar_orig_url = avatar;
        this.lastName = lname;
        this.firstName = fname;
        this.birthDayDate = bday;
    }

    public int getId() { return id;}
    public String getAvatarUrl() { return full_avatar_orig_url; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getBirthDayDate() { return birthDayDate; }
}
