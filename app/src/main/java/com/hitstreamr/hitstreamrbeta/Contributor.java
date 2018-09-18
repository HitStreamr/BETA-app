package com.hitstreamr.hitstreamrbeta;

public class Contributor {
    private String contributorName, contributorPercentage, contributorType;

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
}
