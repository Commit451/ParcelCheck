package com.commit451.parcelcheck;

import android.os.Parcel;
import android.os.Parcelable;
import android.test.AndroidTestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Test which checks a single parcel model to assure that it is parcelable. Override and provide the
 * fully qualified package name of the model ie. com.commit451.model.Person
 */
public abstract class ParcelCheckTest extends AndroidTestCase {

    /**
     * Please provide the class you wish to check, ie. Dog.class
     * @return the class to run the Parcel check on
     */
    public abstract Class getClassToCheck();

    /**
     * Test all parcels within package to see if they properly implement {@link Parcelable}
     * @throws Exception
     */
    public void testParcel() throws Exception {
        Class classToCheck = getClassToCheck();
        if (ObjectHelper.isParcelable(classToCheck) && !Modifier.isAbstract(classToCheck.getModifiers())) {
            /**
             * Helps us to not get into infinite loops if we have a structure in our models where something
             * like a Person model can have a Person field
             */
            ObjectStack objectStack = new ObjectStack();
            Parcelable parcelable = (Parcelable) ObjectHelper.getTestObject(objectStack, classToCheck);
            Parcel parcel = Parcel.obtain();
            parcelable.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            Field creatorField = classToCheck.getField("CREATOR");
            Parcelable.Creator creator = (Parcelable.Creator) creatorField.get(null);
            Parcelable read = (Parcelable) creator.createFromParcel(parcel);
            if (checkingForSuccess()) {
                assertTrue(classToCheck.getSimpleName() + " did not equal after parceling", ObjectHelper.checkFieldsEqual(classToCheck.getSimpleName(), parcelable, read, checkingForSuccess()));
            } else {
                boolean success = ObjectHelper.checkFieldsEqual(classToCheck.getSimpleName(), parcelable, read, checkingForSuccess());
                assertFalse("Looks like "+ classToCheck.getSimpleName() + " was supposed to have failed, but didn't", success);
            }
        }
    }

    /**
     * This is only used by the library since we want to verify that our lib will also fail properly
     * @return true by default to check the parcelable for success
     */
    protected boolean checkingForSuccess() {
        return true;
    }
}
