package com.commit451.parcelcheck.sample.otherModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * An animal!
 */
public class Animal implements Parcelable {

    private int numberOfLegs;
    private float topSpeed;
    private double lifespan;
    private String name;
    private boolean canFly;

    public Animal() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.numberOfLegs);
        dest.writeFloat(this.topSpeed);
        dest.writeDouble(this.lifespan);
        dest.writeString(this.name);
        dest.writeByte(this.canFly ? (byte) 1 : (byte) 0);
    }

    protected Animal(Parcel in) {
        this.numberOfLegs = in.readInt();
        this.topSpeed = in.readFloat();
        this.lifespan = in.readDouble();
        this.name = in.readString();
        this.canFly = in.readByte() != 0;
    }

    public static final Creator<Animal> CREATOR = new Creator<Animal>() {
        @Override
        public Animal createFromParcel(Parcel source) {
            return new Animal(source);
        }

        @Override
        public Animal[] newArray(int size) {
            return new Animal[size];
        }
    };
}
