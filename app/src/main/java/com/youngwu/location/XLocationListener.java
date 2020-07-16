package com.youngwu.location;

import android.location.Location;

/**
 * @author jinpingchen
 */
public interface XLocationListener {

    void onLocation(Location location);

    void onFail(String error);

}
