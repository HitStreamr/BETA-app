package com.hitstreamr.hitstreamrbeta;

import android.os.Parcel;
import android.os.Parcelable;

public class Pay implements Parcelable {

    private String PayDate, PaymentAmt;

    public Pay() {
    }

    public Pay(String PayDate, String PaymentAmt) {
        this.PayDate = PayDate;
        this.PaymentAmt = PaymentAmt;
    }

    public String getPayDate() {
        return PayDate;
    }

    public String getPaymentAmt() {
        return PaymentAmt;
    }

    public void setPayDate(String PayDate) {
        this.PayDate = PayDate;
    }

    public void setPaymentAmt(String PaymentAmt) {
        this.PaymentAmt = PaymentAmt;
    }

    @Override
    public String toString() {
        return PayDate + PaymentAmt;
    }

    protected Pay(Parcel in) {
        PayDate = in.readString();
        PaymentAmt = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(PayDate);
        dest.writeString(PaymentAmt);
    }

    public static final Parcelable.Creator<Pay> CREATOR = new Parcelable.Creator<Pay>() {
        @Override
        public Pay createFromParcel(Parcel in) {
            return new Pay(in);
        }

        @Override
        public Pay[] newArray(int size) {
            return new Pay[size];
        }
    };

}

