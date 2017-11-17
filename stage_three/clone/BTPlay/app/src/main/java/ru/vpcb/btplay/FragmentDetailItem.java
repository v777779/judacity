package ru.vpcb.btplay;

import static ru.vpcb.btplay.utils.Constants.COLLAPSED_TYPE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 17-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */
public class FragmentDetailItem {

    private final String name;
    private final int type;

    public FragmentDetailItem(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public FragmentDetailItem(String name) {
        this.name = name;
        this.type = COLLAPSED_TYPE;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }
}
