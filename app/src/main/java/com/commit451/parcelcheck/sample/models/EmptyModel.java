package com.commit451.parcelcheck.sample.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A model that is purposefully kept empty
 */
public class EmptyModel implements Parcelable {

    public EmptyModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    protected EmptyModel(Parcel in) {
    }

    public static final Parcelable.Creator<EmptyModel> CREATOR = new Parcelable.Creator<EmptyModel>() {
        @Override
        public EmptyModel createFromParcel(Parcel source) {
            return new EmptyModel(source);
        }

        @Override
        public EmptyModel[] newArray(int size) {
            return new EmptyModel[size];
        }
    };
}
