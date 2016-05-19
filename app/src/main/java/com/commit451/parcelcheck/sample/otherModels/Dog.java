package com.commit451.parcelcheck.sample.otherModels;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * A dog!
 */
public class Dog extends Animal {

    private boolean canBark;
    private float size;
    private double age;
    private int numFeet;
    private long numTails;
    private char lastInitial;
    private Boolean canSwim;
    private Float weight;
    private Double coolness;
    private Integer numMouths;
    private Long numTeeth;
    private Character firstInitial;
    private ArrayList<Dog> bestFriends;

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
        dest.writeFloat(this.size);
        dest.writeDouble(this.age);
        dest.writeInt(this.numFeet);
        dest.writeLong(this.numTails);
        dest.writeInt(this.lastInitial);
        dest.writeValue(this.canSwim);
        dest.writeValue(this.weight);
        dest.writeValue(this.coolness);
        dest.writeValue(this.numMouths);
        dest.writeValue(this.numTeeth);
        dest.writeSerializable(this.firstInitial);
        dest.writeTypedList(this.bestFriends);
    }

    protected Dog(Parcel in) {
        super(in);
        this.canBark = in.readByte() != 0;
        this.size = in.readFloat();
        this.age = in.readDouble();
        this.numFeet = in.readInt();
        this.numTails = in.readLong();
        this.lastInitial = (char) in.readInt();
        this.canSwim = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.weight = (Float) in.readValue(Float.class.getClassLoader());
        this.coolness = (Double) in.readValue(Double.class.getClassLoader());
        this.numMouths = (Integer) in.readValue(Integer.class.getClassLoader());
        this.numTeeth = (Long) in.readValue(Long.class.getClassLoader());
        this.firstInitial = (Character) in.readSerializable();
        this.bestFriends = in.createTypedArrayList(Dog.CREATOR);
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
