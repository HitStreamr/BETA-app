package com.hitstreamr.hitstreamrbeta;

public class Label {
    protected String firstName, lastName, email, password, label, address, city, state, zipcode, country, phone;

    public Label(String firstName, String lastName, String email, String password, String label,
                 String address, String city, String state, String zipcode, String country, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.label = label;
        this.address = address;
        this.city = city;
        this.state = state;
        this.phone = phone;
        this.country = country;
        this.zipcode = zipcode;
    }
}
