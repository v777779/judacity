package ru.vpcb.basicactivity;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static ru.vpcb.basicactivity.MainActivity.CONNECT_TIMEOUT;
import static ru.vpcb.basicactivity.MainActivity.MESSAGE_TEST_OK;
import static ru.vpcb.basicactivity.MainActivity.REQUEST_TEST_GET_TEMPLATE;
import static ru.vpcb.basicactivity.MainActivity.REQUEST_TEST_OUT_TEMPLATE;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BasicActivityInstrumentedTest {
    public static final String TAG = BasicActivityInstrumentedTest.class.getSimpleName();



    private class EndpointsCallback implements ICallback {
        private String result;

        public EndpointsCallback() {
            this.result = "";
        }

        @Override
        public void onComplete(String s) {
            result = s;
        }
    }

    @Test
    public void useAppContext() throws Exception {
        EndpointsCallback endpointsCallback = new EndpointsCallback();

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        new EndpointsAsyncTask(endpointsCallback, REQUEST_TEST_GET_TEMPLATE).execute();
        int counter = CONNECT_TIMEOUT;
        while (endpointsCallback.result.isEmpty() && counter > 0) {
            Thread.sleep(1000);
            counter--;
        }
//        assertEquals("joke test received", endpointsCallback.result);
        assertEquals(REQUEST_TEST_OUT_TEMPLATE, endpointsCallback.result);
        Log.i(TAG,MESSAGE_TEST_OK);
    }
}
