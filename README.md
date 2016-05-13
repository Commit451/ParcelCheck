# ParcelCheck
Checks models within specified packages to make sure they are properly parcelable

[![Build Status](https://travis-ci.org/Commit451/ParcelCheck.svg?branch=master)](https://travis-ci.org/Commit451/ParcelCheck)
[![](https://jitpack.io/v/Commit451/ParcelCheck.svg)](https://jitpack.io/#Commit451/ParcelCheck)

# Usage
Setup is simple, just create a class within your application's `androidTest` directory and make sure the newly created test extends ParcelCheckTest
```java
public class AllModelsTest extends ParcelCheckTest {

    @Override
    public String[] getModelPackageNames() {
        return new String[] {
                "com.commit451.parcelcheck.sample.models",
                "com.commit451.parcelcheck.sample.otherModels"
        };
    }
}
```
See the sample app for more

# Notes
This library does not check the validity of your Parcelable methods. In other words, if you were to flip the value of a boolean when parceling, this library would not catch that. Since most people use generators to generate Parcelable methods, it is more dedicated to catching errors of the user forgetting to regenerate methods after adding data to a model, or other such cases.

License
--------

    Copyright 2016 Commit 451

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
