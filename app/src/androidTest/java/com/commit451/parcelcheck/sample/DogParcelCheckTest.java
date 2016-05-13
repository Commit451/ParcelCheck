package com.commit451.parcelcheck.sample;

import com.commit451.parcelcheck.ParcelCheckTest;
import com.commit451.parcelcheck.sample.otherModels.Dog;

/**
 * Checks to see if dog is parcelable. Spoiler alert, it is
 */
public class DogParcelCheckTest extends ParcelCheckTest {

    @Override
    public Class getClassToCheck() {
        return Dog.class;
    }
}
