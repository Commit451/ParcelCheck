package com.commit451.parcelcheck.sample;

import com.commit451.parcelcheck.ParcelCheckTest;

/**
 * Shows how to set up a model parcel test. Basically, just need to specify the package name
 */
public class AllModelsTest extends ParcelCheckTest {

    @Override
    public String[] getModelPackageNames() {
        return new String[] {
                "com.commit451.parcelcheck.sample.models",
                "com.commit451.parcelcheck.sample.otherModels"
        };
    }
}
