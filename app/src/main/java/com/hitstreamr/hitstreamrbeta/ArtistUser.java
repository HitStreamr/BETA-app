package com.hitstreamr.hitstreamrbeta;

public class ArtistUser {
    protected String firstname, lastname, email, username, address, city, state, country, phone, zip;

    public ArtistUser(){

    }

    public ArtistUser(String firstname, String lastname, String email, String username, String country, String address, String city, String state, String phone, String zip) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.username = username;
        this.address = address;
        this.phone = phone;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        }
    }
