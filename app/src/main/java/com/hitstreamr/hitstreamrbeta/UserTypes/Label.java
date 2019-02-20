package com.hitstreamr.hitstreamrbeta.UserTypes;

public class Label {
    protected String firstName, lastName, email, label, address, city, state, zipcode, country, phone;

    public Label(String firstName, String lastName, String email, String label, String address,
                 String city, String state, String zipcode, String country, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.label = label;
        this.address = address;
        this.city = city;
        this.state = state;
        this.phone = phone;
        this.country = country;
        this.zipcode = zipcode;
    }

    public Label() {}

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
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
}
