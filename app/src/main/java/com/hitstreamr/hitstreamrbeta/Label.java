package com.hitstreamr.hitstreamrbeta;

public class Label {
    private String firstName, lastName, email, organization, address, city, state, phone;
    private float zipcode;

    public Label(String firstName, String lastName, String email, String organization,
                 String address, String city, String state, float zipcode, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.organization = organization;
        this.address = address;
        this.city = city;
        this.state = state;
        this.phone = phone;
        this.zipcode = zipcode;
    }
}
