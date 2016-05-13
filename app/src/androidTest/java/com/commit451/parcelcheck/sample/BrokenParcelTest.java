package com.commit451.parcelcheck.sample;

import com.commit451.parcelcheck.ParcelCheckTest;

/**
 * Test which will always fail due to models being broken
 */
public class BrokenParcelTest extends ParcelCheckTest {

    @Override
    public String[] getModelPackageNames() {
        return new String[] {
                "com.commit451.parcelcheck.sample.brokenModels"
        };
    }
}
