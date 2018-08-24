package com.hitstreamr.hitstreamrbeta;

public class Artist {
    protected String firstName, lastName, email, phone, address, city, state, zipcode, country, username;

    public Artist(String firstName, String lastName, String email, String username,
                  String address, String city, String state, String zipcode, String country, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.address = address;
        this.city = city;
        this.state = state;
        this.phone = phone;
        this.country = country;
        this.zipcode = zipcode;
    }
}

