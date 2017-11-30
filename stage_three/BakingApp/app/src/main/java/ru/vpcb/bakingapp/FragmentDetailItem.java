package ru.vpcb.bakingapp;

import static ru.vpcb.bakingapp.utils.Constants.COLLAPSED_TYPE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 17-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */
public class FragmentDetailItem {

    private final String name;
    private final int type;
    private final FragmentDetailItem child;

    public FragmentDetailItem(String name, int type, FragmentDetailItem child) {
        this.name = name;
        this.type = type;
        this.child = child;
    }

    public FragmentDetailItem(String name) {
        this(name, COLLAPSED_TYPE, null);
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public FragmentDetailItem getChild() {
        return child;
    }
}
