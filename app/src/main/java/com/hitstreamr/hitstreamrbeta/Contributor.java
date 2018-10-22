package com.hitstreamr.hitstreamrbeta;

import android.os.Parcel;
import android.os.Parcelable;

public class Contributor implements Parcelable {
    private String contributorName, contributorPercentage, contributorType;

    public Contributor() {
    }

    public Contributor(String contributorName, String contributorPercentage, String contributorType) {
        this.contributorName = contributorName;
        this.contributorPercentage = contributorPercentage;
        this.contributorType = contributorType;
    }

    public String getContributorName() {
        return contributorName;
    }

    public String getContributorPercentage() {
        return contributorPercentage;
    }

    public String getContributorType() {
        return contributorType;
    }

    public void setContributorName(String contributorName) {
        this.contributorName = contributorName;
    }

    public void setContributorPercentage(String contributorPercentage) {
        this.contributorPercentage = contributorPercentage;
    }

    public void setContributorType(String contributorType) {
        this.contributorType = contributorType;
    }

    @Override
    public String toString() {
        return contributorName + contributorPercentage + contributorType;
    }

    protected Contributor(Parcel in) {
        contributorName = in.readString();
        contributorPercentage = in.readString();
        contributorType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contributorName);
        dest.writeString(contributorPercentage);
        dest.writeString(contributorType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Contributor> CREATOR = new Parcelable.Creator<Contributor>() {
        @Override
        public Contributor createFromParcel(Parcel in) {
            return new Contributor(in);
        }

        @Override
        public Contributor[] newArray(int size) {
            return new Contributor[size];
        }
    };
}
