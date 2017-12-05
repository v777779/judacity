package ru.vpcb.btdetail;

import java.util.List;

/**
 * Created by V1 on 14-Nov-17.
 */

public interface IFragmentHelper {
    void onCallback(int position);
    List<String> getList();
}
