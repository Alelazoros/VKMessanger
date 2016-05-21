package ua.nure.vkmessanger.model;

/**
 * Created by Nickitee on 15.05.2016.
 */
public class User {

    private int id;

    private String fullAvatarOriginalUrl;

    private String firstName, lastName;

    private String birthDayDate;

    public User(int id, String avatar, String fName, String lName, String birthday) {
        this.id = id;
        this.fullAvatarOriginalUrl = avatar;
        this.lastName = lName;
        this.firstName = fName;
        this.birthDayDate = birthday;
    }

    public int getId() {
        return id;
    }

    public String getFullAvatarOriginalUrl() {
        return fullAvatarOriginalUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthDayDate() {
        return birthDayDate;
    }
}
