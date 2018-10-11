package com.hitstreamr.hitstreamrbeta;

import java.util.ArrayList;

public class Title {
    private String mName;

    public Title(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }


    private static int lastContactId = 0;

    public static ArrayList<Title> createContactsList(int numContacts) {
        ArrayList<Title> contacts = new ArrayList<Title>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new Title("Video " + ++lastContactId));
        }

        return contacts;
    }
}