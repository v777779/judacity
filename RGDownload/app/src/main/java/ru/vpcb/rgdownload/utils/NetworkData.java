package ru.vpcb.rgdownload.utils;

/**
 * Created by V1 on 28-Sep-17.
 */

public class NetworkData {
    private static final String DEFAULT_LANGUAGE = "en_US";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_ID = 0;
    private final QUERY_TYPE type;
    private final int id;
    private final int page;
    private final String lang;

    public NetworkData(QUERY_TYPE type, int page, int id, String lang) {
        if (type == null || page < 0 || id < 0) {
            throw new IllegalArgumentException();
        }
        if (lang == null || lang.isEmpty()) {
            lang = DEFAULT_LANGUAGE;
        }

        this.type = type;
        this.page = page;
        this.id = id;
        this.lang = lang;
    }

    public NetworkData(QUERY_TYPE type, int page, String lang) {
        this(type, page, DEFAULT_ID, lang);
    }

    public NetworkData(QUERY_TYPE type, int page, int id) {
        this(type, page, id, DEFAULT_LANGUAGE);
    }

    public NetworkData(QUERY_TYPE type, int page) {
        this(type, page, DEFAULT_ID, DEFAULT_LANGUAGE);
    }

    public NetworkData(QUERY_TYPE type) {
        this(type, DEFAULT_PAGE, DEFAULT_ID, DEFAULT_LANGUAGE);
    }

    public int getType() {
        return type.ordinal();
    }

    public int getId() {
        return id;
    }

    public int getPage() {
        return page;
    }

    public String getLang() {

        return lang;
    }
}
