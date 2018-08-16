package com.hitstreamr.hitstreamrbeta;

public class ArtistUser {
    public String firstname, lastname, email, username, password, address, city, state, phone;
    public float zip;

    public ArtistUser(String firstname, String lastname, String email, String username, String password, String address, String city, String state, String phone, float zip) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.city = city;
        this.state = state;
        this.zip = zip;

        }
    }
