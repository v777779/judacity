package ru.vpcb.footballassistant.utils;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import ru.vpcb.footballassistant.R;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 24-Jan-18
 * Email: vadim.v.voronov@gmail.com
 */

public class Config {

    // data
    public static final String FD_BASE_URI = "http://api.football-data.org/v1/";
    public static final String FD_COMPETITIONS_GET = "competitions/";

    public static final String FD_QUERY_ID = "id";
    public static final String FD_QUERY_MATCHDAY = "matchday";
    public static final String FD_QUERY_TIMEFRAME = "timeFrame";
    public static final String FD_QUERY_SEASON = "season";

    public static final String FD_COMPETITIONS_TEAMS_GET = "competitions/{" + FD_QUERY_ID + "}/teams";
    public static final String FD_COMPETITIONS_FIXTURES_GET = "competitions/{" + FD_QUERY_ID + "}/fixtures";
    public static final String FD_COMPETITIONS_TABLE_GET = "competitions/{" + FD_QUERY_ID + "}/leagueTable";


    public static final String FD_TEAM_GET = "teams/{" + FD_QUERY_ID + "}";
    public static final String FD_TEAM_FIXTURES_GET = "teams/{" + FD_QUERY_ID + "}/fixtures";
    public static final String FD_TEAM_PLAYERS_GET = "teams/{" + FD_QUERY_ID + "}/players";

    public static final String FD_TIME_PAST = "p";
    public static final String FD_TIME_FUTUTRE = "n";

    public static final String FD_REGEX_TEAMS = ".*teams/";
    public static final String FD_REGEX_FIXTURES = ".*fixtures/";
    public static final String FD_REGEX_COMPETITIONS = ".*competitions/";

    // update service
    public static final String UPDATE_SERVICE_TAG = "UpdaterService";
    public static final int UPDATE_SERVICE_PROGRESS = 100;
    public static final int MAIN_ACTIVITY_PROGRESS = 100;

    // retrofit

    // loaders
    public static final int LOADERS_UPDATE_COUNTER = 5;


    //main_activity
    public static final int MAIN_ACTIVITY_STATE_0 = 0;
    public static final int MAIN_ACTIVITY_STATE_1 = 1;
    public static final int MAIN_ACTIVITY_STATE_2 = 2;
    public static final int MAIN_ACTIVITY_STATE_3 = 3;
    public static final int MAIN_ACTIVITY_STATE_4 = 4;
    public static final int MAIN_ACTIVITY_STATE_5 = 5;
    public static final int MAIN_ACTIVITY_INDEFINITE = -1;


    public static final int[] IMAGE_IDS = {
            R.drawable.back_001,
            R.drawable.back_002,
            R.drawable.back_003,
            R.drawable.back_004

    };


    // recycler
    private static final int HIGH_SCALE_WIDTH = 180;     // dpi
    private static final int HIGH_SCALE_HEIGHT = 300;     // dpi 200
    private static final double SCALE_RATIO_VERT = 0.6;   // dw/dh
    private static final double SCALE_RATIO_HORZ = 0.45;   // dw/dh

    private static final int MIN_SPAN = 1;
    private static final int MIN_HEIGHT = 100;
    private static final int MIN_WIDTH = 100;

    public static final int RM_ITEM_VIEW_TYPE = 0;
    public static final int RM_HEAD_VIEW_TYPE = 1;
    public static final int RT_ITEM_VIEW_TYPE_LIGHT = 0;
    public static final int RT_ITEM_VIEW_TYPE_DARK = 1;
    public static final int RT_HEAD_VIEW_TYPE = 2;


    // viewpager
    public static final int VIEWPAGER_OFF_SCREEN_PAGE_NUMBER = 3;


    // competitions
    public static final String EMPTY_TEAM_NAME = "-";
    public static final String EMPTY_TWO_DASH = "--";
    public static final String EMPTY_LONG_DASH = "\u2014";
    public static final String EMPTY_MATCH_TIME = "-- : --";
    public static final String EMPTY_FIXTURE_DATE = "--/--/--";
    public static final String EMPTY_PLAYER_DATE = "--.--.--";
    public static final String EMPTY_MATCH_SCORE = "- : -";

    //detail activity
    public static final int CALENDAR_DIALOG_ACTION_APPLY = 0;
    public static final int CALENDAR_DIALOG_ACTION_CANCEL = 1;

    // team activity
    public static final String BUNDLE_INTENT_TEAM_ID = "bundle_intent_team_id";
    public static final String BUNDLE_TEAM_ID = "bundle_team_id";
    public static final int EMPTY_TEAM_ID = -1;

    // fragment_team
    public static final String FRAGMENT_TEAM_TAG = "fragment_team";
    public static final String BUNDLE_TEAM_VIEWPAGER_POS = "fragment_team_bundle_viewpager_pos";

    public static final int FRAGMENT_TEAM_VIEWPAGER_TEAM_POS = 0;
    public static final int FRAGMENT_TEAM_VIEWPAGER_PLAYERS_POS = 1;


    // league activity
    public static final String BUNDLE_INTENT_LEAGUE_ID = "bundle_intent_league_id";
    public static final String BUNDLE_LEAGUE_ID = "bundle_league_id";
    public static final int EMPTY_LEAGUE_ID = -1;

    // fragment_league
    public static final String FRAGMENT_LEAGUE_TAG = "fragment_league";
    public static final String BUNDLE_LEAGUE_VIEWPAGER_POS = "fragment_team_bundle_viewpager_pos";
    public static final int FRAGMENT_LEAGUE_VIEWPAGER_LEAGUE_POS = 0;

    public static final String[] LEAGUE_CODES = new String[]{
            "BL1", "Germany", "1. Bundesliga",
            "BL2", "Germany", "2. Bundesliga",
            "BL3", "Germany", "3. Bundesliga",
            "DFB", "Germany", "Dfb-Cup",
            "PL", "England", "Premiere League",
            "EL1", "England", "League One",
            "ELC", "England", "Championship",
            "FAC", "England", "FA-Cup",
            "SA", "Italy", "Serie A",
            "SB", "Italy", "Serie B",
            "PD", "Spain", "Primera Division",
            "SD", "Spain", "Segunda Division",
            "CDR", "Spain", "Copa del Rey",
            "FL1", "France", "Ligue 1",
            "FL2", "France", "Ligue 2",
            "DED", "Netherlands", "Eredivisie",
            "PPL", "Portugal", "Primeira Liga",
            "GSL", "Greece", "Super League",
            "CL", "Europe", "Champions-League",
            "EL", "Europe", "UEFA-Cup",
            "EC", "Europe", "European-Cup of Nations",
            "WC", "World", "World-Cup"
    };

    private static final String[] TEAM_LOGOS = new String[] {

            "Logo_of_Beşiktaş_JK.svg",
            "https://upload.wikimedia.org/wikipedia/en/4/47/Besiktas_JK%27s_official_logo.png"
    };


    public static String imageCheckReplaceURL(String imageURL) {
        for (int i = 0; i <TEAM_LOGOS.length ; i+=2) {
            if(imageURL.contains(TEAM_LOGOS[i])) return TEAM_LOGOS[i+1];

        }
        return imageURL;
    }

    /**
     * Span class used for RecyclerView as storage of display item parameters
     */
    public static class Span {
        /**
         * int number items of RecyclerView in width
         */
        private int spanX;
        /**
         * int number items of RecyclerView in height
         */
        private int spanY;
        /**
         * int width of RecyclerView item
         */
        private int width;
        /**
         * int height of RecyclerView item
         */
        private int height;

        /**
         * int width of screen
         */
        private int screenWidth;
        /**
         * int height of screen
         */
        private int screenHeight;


        /**
         * Constructor
         *
         * @param spanX  int number items of RecyclerView in width
         * @param spanY  int number items of RecyclerView in height
         * @param width  int width of RecyclerView item
         * @param height int height of RecyclerView item
         */
        public Span(int spanX, int spanY, int width, int height, int screenWidth, int screenHeight) {
            this.spanX = spanX;
            this.spanY = spanY;
            this.width = width;
            this.height = height;
            this.screenHeight = screenHeight;
            this.screenWidth = screenWidth;

        }

        /**
         * Returns  span in width
         *
         * @return int span in width
         */
        public int getSpanX() {
            return spanX;
        }

        /**
         * Returns  span in height
         *
         * @return int span in height
         */
        public int getSpanY() {
            return spanY;
        }

        /**
         * Returns  width of RecyclerView item
         *
         * @return int width of RecyclerView item
         */
        public int getWidth() {
            return width;
        }

        /**
         * Returns  height of RecyclerView item
         *
         * @return int height of RecyclerView item
         */
        public int getHeight() {
            return height;
        }

        /**
         * Returns true if RecyclerView is smaller than screen
         *
         * @param nItems int number of items ofRecyclerView
         * @param isVert boolean true for VERTICAL scrolling mode
         * @return boolean true if RecyclerView is smaller than screen
         */
        public boolean isShowBar(int nItems, boolean isVert) {
            if (isVert) {
                return (Math.ceil((double) nItems / spanX)) * height < screenHeight;
            } else {
                return (Math.ceil((double) nItems / spanY)) * width < screenWidth;
            }
        }
    }

    /**
     * Returns double value from 0.0 to 1.0 the width between to guideline of ConstraintLayout
     * Used to get actual space occupied by RecyclerView
     *
     * @param context Context of calling activity
     * @param guideId int resource Id of guideline of ConstraintLayout
     * @return double value from 0.0 to 1.0 the width between to guideline of ConstraintLayout
     */
    private static double getPercent(AppCompatActivity context, int guideId) {
        return ((ConstraintLayout.LayoutParams)
                context.findViewById(guideId).getLayoutParams()).guidePercent;
    }

    /**
     * Returns  Span class object with number and size of items in width and height.
     * Used to build GridLayout for RecyclerView object.
     *
     * @param context Context of calling activity
     * @return Span class object with number and size of items in width and height.
     */
    public static Span getDisplayMetrics(AppCompatActivity context) {
        DisplayMetrics dp = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dp);
// tightened to layout
        double height_ratio = getPercent(context, R.id.guide_h2) - getPercent(context, R.id.guide_h1);
        double width_ratio = getPercent(context, R.id.guide_v2) - getPercent(context, R.id.guide_v1);

        double width = dp.widthPixels / dp.density * width_ratio;
        double height = dp.heightPixels / dp.density * height_ratio;  // real height

        int spanInWidth = (int) Math.round(width / HIGH_SCALE_WIDTH);
        int spanHeight = (int) (width * dp.density / spanInWidth / SCALE_RATIO_VERT);  // vertical only
        int spanInHeight = (int) Math.round(height / HIGH_SCALE_HEIGHT);
        int spanWidth = (int) (height * dp.density / spanInHeight * SCALE_RATIO_HORZ);  // horizontal only
        if (spanInWidth < MIN_SPAN) spanInWidth = MIN_SPAN;
        if (spanInHeight < MIN_SPAN) spanInHeight = MIN_SPAN;

        if (spanHeight < MIN_HEIGHT) spanHeight = MIN_HEIGHT;

        int minWidth = (int) (MIN_WIDTH);  // horizontal
        if (spanWidth < minWidth) spanWidth = minWidth;

// vertical
//        mSpan = spanInWidth;
//        mSpanHeight = spanHeight;
// horizontal
//        mSpan = spanInHeight;
//        mSpanWidth = spanWidth;

        return new Span(spanInWidth, spanInHeight, spanWidth, spanHeight, dp.widthPixels, dp.heightPixels);
    }

}
