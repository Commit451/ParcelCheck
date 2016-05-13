package com.commit451.parcelcheck.sample;

import com.commit451.parcelcheck.ParcelCheckPackageTest;

/**
 * Test which will always fail due to models being broken
 */
public class BrokenParcelTest extends ParcelCheckPackageTest {

    @Override
    public String[] getModelPackageNames() {
        return new String[] {
                "com.commit451.parcelcheck.sample.brokenModels"
        };
    }

    @Override
    protected boolean checkingForSuccess() {
        //please don't ever do this
        return false;
    }
}
