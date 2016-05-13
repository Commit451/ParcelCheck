package com.commit451.parcelcheck.sample;

import com.commit451.parcelcheck.ParcelCheckTest;
import com.commit451.parcelcheck.sample.otherModels.Dog;

/**
 * Checks to see if dog is parcelable. Spoiler alert, it is
 */
public class SuccessfulParcelCheckTest extends ParcelCheckTest {

    @Override
    public Class[] getClassesToCheck() {
        return new Class[] {
                Dog.class
        };
    }
}
