package com.commit451.parcelcheck;

import android.os.Parcel;
import android.os.Parcelable;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Test to see if a package of models are all properly parcelable. Simply override and provide the packages that contain your parcelable models
 */
public abstract class ParcelCheckPackageTest extends AndroidTestCase {

    /**
     * The one method you need to implement.
     * @return the packages you want to check
     */
    public abstract String[] getModelPackageNames();

    /**
     * Test all parcels within package to see if they properly implement {@link Parcelable}
     * @throws Exception
     */
    public void testParcels() throws Exception {
        String[] packages = getModelPackageNames();
        boolean oneTestFailed = false;
        for (String pkg : packages) {
            ArrayList<String> classes = ObjectHelper.getClassesOfPackage(getContext(), pkg);
            Assert.assertFalse("No classes found in package " + pkg, classes == null || classes.size() == 0);
            for (String className : classes) {
                Class currentClass = getContext().getClassLoader().loadClass(pkg + "." + className);
                if (ObjectHelper.isParcelable(currentClass) && !Modifier.isAbstract(currentClass.getModifiers())) {
                    /**
                     * Helps us to not get into infinite loops if we have a structure in our models where something
                     * like a Person model can have a Person field
                     */
                    ObjectStack objectStack = new ObjectStack();
                    Parcelable parcelable = (Parcelable) ObjectHelper.getTestObject(objectStack, currentClass);
                    Parcel parcel = Parcel.obtain();
                    parcelable.writeToParcel(parcel, 0);
                    parcel.setDataPosition(0);
                    Field creatorField = currentClass.getField("CREATOR");
                    Parcelable.Creator creator = (Parcelable.Creator) creatorField.get(null);
                    Parcelable read = (Parcelable) creator.createFromParcel(parcel);
                    if (!checkingForSuccess()) {
                        boolean success = ObjectHelper.checkFieldsEqual(currentClass.getSimpleName(), parcelable, read, checkingForSuccess());
                        if (!success) {
                            oneTestFailed = true;
                        }
                    } else {
                        assertTrue(currentClass.getSimpleName() + " did not equal after parceling", ObjectHelper.checkFieldsEqual(currentClass.getSimpleName(), parcelable, read, checkingForSuccess()));
                    }
                }
            }
        }
        if (!checkingForSuccess()) {
            assertTrue("You said that the test should fail, but they all passed. ???", oneTestFailed);
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
