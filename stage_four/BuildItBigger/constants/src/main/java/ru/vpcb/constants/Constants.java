package ru.vpcb.constants;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 14-Dec-17
 * Email: vadim.v.voronov@gmail.com
 */

public class Constants {

    // main activity
    public static final String INTENT_STRING_EXTRA = "intent_string_extra";
    public static final String INTENT_IMAGE_EXTRA = "intent_image_extra";
    public static final int MESSAGE_JOKE_ID = 1223;
    public static final int INTENT_REQUEST_CODE = 1224;

    public static final String GET_REQUEST = "get";
    public static final int AD_ACTIVATION_COUNTER = 3;

    // display    public static final String BUNDLE_JOKE_STRING = "bundle_joke_string";
    public static final String BUNDLE_JOKE_LIST = "bundle_joke_list";
    public static final String BUNDLE_POSITION = "bundle_position";

    public static final String BUNDLE_FRONT_TEXT_ID = "bundle_front_text_id";
    public static final String BUNDLE_FRONT_IMAGE_ID = "bundle_front_image_id";

    public static final String BUNDLE_JOKE_TEXT_ID = "bundle_joke_text_id";
    public static final String BUNDLE_JOKE_IMAGE_ID = "bundle_joke_image_id";
    public static final String BUNDLE_AD_COUNTER = "bundle_ad_counter_bar";

    // recycler
    public static final int HIGH_SCALE_WIDTH = 240;     // dpi
    public static final int HIGH_SCALE_HEIGHT = 180;     // dpi
    public static final double SCALE_RATIO_VERT = 1.5;
    public static final double SCALE_RATIO_HORZ = 1.5;

    public static final int MIN_SPAN = 1;
    public static final int MIN_HEIGHT = 100;
    public static final int MIN_WIDTH = 200;

    // tests
    public static final String TEST_REQUEST = "test";
    public static final String TEST_RESPONSE = "test joke received";

    public static final String MESSAGE_TEST_OK = "*** Endpoint Test passed ***";
    public static final String TEST_MESSAGE_OK_FREE = "*** Endpoint Test Free passed ***";
    public static final String TEST_MESSAGE_OK_PAID = "*** Endpoint Test Paid passed ***";

    public static final int TEST_POSITION_0 = 0;
    public static final int TEST_POSITION_5 = 5;
    public static final int TEST_POSITION_10 = 10;

}
