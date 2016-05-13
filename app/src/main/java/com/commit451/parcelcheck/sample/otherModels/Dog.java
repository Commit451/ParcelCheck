package com.commit451.parcelcheck.sample.otherModels;

import android.os.Parcel;

/**
 * A dog!
 */
public class Dog extends Animal {

    private boolean canBark;

    public Dog() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.canBark ? (byte) 1 : (byte) 0);
    }

    protected Dog(Parcel in) {
        super(in);
        this.canBark = in.readByte() != 0;
    }

    public static final Creator<Dog> CREATOR = new Creator<Dog>() {
        @Override
        public Dog createFromParcel(Parcel source) {
            return new Dog(source);
        }

        @Override
        public Dog[] newArray(int size) {
            return new Dog[size];
        }
    };
}
