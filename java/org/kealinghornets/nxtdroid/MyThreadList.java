package org.kealinghornets.nxtdroid;

import android.util.Log;

/**
 * Created by jychuah on 11/3/13.
 */
public class MyThreadList {
    public MyThreadList() {
        Log.d("MyThreadList", "MyThreadListInitialized");
        new MyNXTThread();
    }
}
