package de.tu_darmstadt.informatik.newapp.javabeans;

/**
 * Created by niharika.sharma on 27-11-2016.
 */

import java.io.Serializable;
public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String email;
    private String imageName;
    private byte[] imageData;
    private String deviceId;

    public int getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(int registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    private int registrationStatus;



    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    public String getLastName() {

        return lastName;
    }

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }

    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getImageName(){

        return imageName;
    }

    public void setImageName(String imageName) {

        this.imageName = imageName;
    }

    public byte[] getImageData(){

        return imageData;
    }

    public void setImageData(byte[] imageData) {

        this.imageData = imageData;
    }

    public String getDeviceId() {

        return deviceId;
    }

    public void setDeviceId(String deviceId) {

        this.deviceId = deviceId;
    }

}
