package com.commit451.parcelcheck.sample;

import com.commit451.parcelcheck.ParcelCheckTest;
import com.commit451.parcelcheck.sample.models.EmptyModel;

/**
 * Check and see if an empty model does not fail
 */
public class EmptyParcelCheckTest extends ParcelCheckTest {

    @Override
    public Class[] getClassesToCheck() {
        return new Class[] {
                EmptyModel.class
        };
    }
}
