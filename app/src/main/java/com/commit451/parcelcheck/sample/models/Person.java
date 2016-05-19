package com.commit451.parcelcheck.sample.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Test class
 */
public class Person implements Parcelable {

    private String name;
    private double age;
    private float size;
    private long friends;
    private boolean isCool;
    private Person bestFriend;

    public Person() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeDouble(this.age);
        dest.writeFloat(this.size);
        dest.writeLong(this.friends);
        dest.writeByte(this.isCool ? (byte) 0 : (byte) 1);
        dest.writeParcelable(this.bestFriend, flags);
    }

    protected Person(Parcel in) {
        this.name = in.readString();
        this.age = in.readDouble();
        this.size = in.readFloat();
        this.friends = in.readLong();
        this.isCool = in.readByte() != 0;
        this.bestFriend = in.readParcelable(Person.class.getClassLoader());
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
