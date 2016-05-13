package com.commit451.parcelcheck.sample;

import com.commit451.parcelcheck.ParcelCheckTest;
import com.commit451.parcelcheck.sample.brokenModels.Phone;

/**
 * Checks to see if dog is parcelable. Spoiler alert, it isn't
 */
public class FailureParcelCheckTest extends ParcelCheckTest {

    @Override
    public Class[] getClassesToCheck() {
        return new Class[] {
                Phone.class
        };
    }

    @Override
    protected boolean checkingForSuccess() {
        return false;
    }
}
