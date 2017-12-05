package ru.vpcb.popularmovie.utils;

import android.content.Context;

import static ru.vpcb.popularmovie.utils.Constants.DEFAULT_ID;
import static ru.vpcb.popularmovie.utils.Constants.DEFAULT_LANGUAGE;
import static ru.vpcb.popularmovie.utils.Constants.DEFAULT_PAGE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class NetworkData {
    private final QueryType type;
    private final int id;
    private final int page;
    private final String lang;
    private final Context context;

    /**
     *  Constructor creates object from query parameters
     * @param context   parent context
     * @param type      query type from QueryType enumeration POPULAR, NOWDAYS, TOPRATED and etc.
     * @param page      page number to load
     * @param id        movie Id to load
     * @param lang      language, "en_US" by default
     */
    private NetworkData(Context context, QueryType type, int page, int id, String lang) {
        if (type == null || page < 0 || id < 0) {
            throw new IllegalArgumentException();
        }
        if (lang == null || lang.isEmpty()) {
            lang = DEFAULT_LANGUAGE;
        }
        this.context = context;
        this.type = type;
        this.page = page;
        this.id = id;
        this.lang = lang;
    }

//    public NetworkData(Context context, QueryType type, int page, String lang) {
//        this(context,type, page, DEFAULT_ID, lang);
//    }

    /**
     *  Constructor creates object from shorted list of query parameters
     * @param context   parent context
     * @param type      query type from QueryType enumeration POPULAR, NOWDAYS, TOPRATED and etc.
     * @param page      page number to load
     * @param id        movie Id to load
     */
    public NetworkData(Context context, QueryType type, int page, int id) {
        this(context,type, page, id, DEFAULT_LANGUAGE);
    }

    /**
     *  Constructor creates object from shorted list of query parameters
     * @param context   parent context
     * @param type      query type from QueryType enumeration POPULAR, NOWDAYS, TOPRATED and etc.
     * @param page      page number to load
     */
    public NetworkData(Context context, QueryType type, int page) {
        this(context,type, page, DEFAULT_ID, DEFAULT_LANGUAGE);
    }

    /**
     *  Constructor creates object from shorted list of query parameters
     * @param context   parent context
     * @param type      query type from QueryType enumeration POPULAR, NOWDAYS, TOPRATED and etc.
     */
    public NetworkData(Context context, QueryType type) {
        this(context,type, DEFAULT_PAGE, DEFAULT_ID, DEFAULT_LANGUAGE);
    }

    // standard getters
    int getType() {
        return type.ordinal();
    }

    public int getId() {
        return id;
    }

    int getPage() {
        return page;
    }

    String getLang() {
        return lang;
    }

    Context getContext() {
        return context;
    }
}
