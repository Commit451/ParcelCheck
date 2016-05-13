package com.commit451.parcelcheck.sample.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Test class
 */
public class Person implements Parcelable {

    private String name;
    private double age;
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
        dest.writeParcelable(this.bestFriend, flags);
    }

    protected Person(Parcel in) {
        this.name = in.readString();
        this.age = in.readDouble();
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
