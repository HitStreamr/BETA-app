package com.hitstreamr.hitstreamrbeta.UserTypes;

public class ArtistUser {

    private String firstname, lastname, artistname, email, username, address, city, state, country,
            phone, zip, userID, bio;

    public ArtistUser(String firstname, String lastname, String artistname, String email, String username, String address,
                      String city, String state, String country, String phone, String zip, String bio/*, String userID*/){
        this.firstname = firstname;
        this.lastname = lastname;
        this.artistname = artistname;
        this.email =  email;
        this.username = username;
        this.address = address;
        this.phone = phone;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.bio = bio;
//        this.userID = userID;
    }

    public ArtistUser() {}

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getArtistname() {
        return artistname;
    }

    public void setArtistname(String firstname) {
        this.artistname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

//    public String getUserID() {
//        return userID;
//    }
//
//    public void setUserID(String userID) {
//        this.userID = userID;
//    }


    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
