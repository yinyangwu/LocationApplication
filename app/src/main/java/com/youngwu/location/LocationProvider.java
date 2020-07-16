package com.youngwu.location;

import android.content.Context;

/**
 * @author jinpingchen
 */
public interface LocationProvider {

    void startGetLocation(Context context);

    void startGetLocation(Context context, int time);

    void stopGetLocation();

    void setLocationListener(XLocationListener listener);

    void removeLocationListener(XLocationListener listener);

}
