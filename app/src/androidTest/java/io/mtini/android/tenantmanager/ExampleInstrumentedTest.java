package io.mtini.android.tenantmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("io.mtini.android.tenantmanager", appContext.getPackageName());
    }

    @Test
    public void readPreferences(){

        Context appContext = InstrumentationRegistry.getContext();//getTargetContext();

        //eat_preferencescreen
        //SharedPreferences prefs = appContext.getPreferences(Context.MODE_PRIVATE  );
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        //this.getPreferenceScreen().getSharedPreferences()
        assertTrue(prefs.contains("base_currency"));

        String currencyCode = prefs.getString("base_currency","USD");
        System.out.println(currencyCode);
    }


}
