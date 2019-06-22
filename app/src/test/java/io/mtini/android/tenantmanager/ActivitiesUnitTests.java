package io.mtini.android.tenantmanager;

import android.os.Bundle;
import android.os.Parcel;

import org.junit.Test;

public class ActivitiesUnitTests {

    @Test
    public void testMainActivity(){
        Bundle bundle =  Bundle.EMPTY;// Bundle.CREATOR.createFromParcel(Parcel.obtain());
        MainActivity activity = new MainActivity();
        activity.onCreate(bundle);
    }
}
