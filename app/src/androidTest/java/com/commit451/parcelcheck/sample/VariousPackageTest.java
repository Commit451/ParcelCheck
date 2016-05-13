package com.commit451.parcelcheck.sample;

import com.commit451.parcelcheck.ParcelCheckPackageTest;

/**
 * Test to make sure that models that are not Parcelable will get checked
 */
public class VariousPackageTest extends ParcelCheckPackageTest {
    @Override
    public String[] getModelPackageNames() {
        return new String[] {
                "com.commit451.parcelcheck.sample.packageWithOtherThingsToo"
        };
    }
}
