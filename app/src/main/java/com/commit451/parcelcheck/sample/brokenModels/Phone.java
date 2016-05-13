package com.commit451.parcelcheck.sample.brokenModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * A phone model, which is missing a few parcelable calls
 */
public class Phone implements Parcelable {

    private int modelNumber;
    private String manufacturer;
    private Date releaseDate;

    public Phone() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.modelNumber);
        //Comment this out, which makes the parcel test fail
        //dest.writeString(this.manufacturer);
        dest.writeLong(this.releaseDate != null ? this.releaseDate.getTime() : -1);
    }



    protected Phone(Parcel in) {
        this.modelNumber = in.readInt();
        this.manufacturer = in.readString();
        long tmpReleaseDate = in.readLong();
        this.releaseDate = tmpReleaseDate == -1 ? null : new Date(tmpReleaseDate);
    }

    public static final Parcelable.Creator<Phone> CREATOR = new Parcelable.Creator<Phone>() {
        @Override
        public Phone createFromParcel(Parcel source) {
            return new Phone(source);
        }

        @Override
        public Phone[] newArray(int size) {
            return new Phone[size];
        }
    };
}
