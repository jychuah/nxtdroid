package org.kealinghornets.nxtdroid.app;


import android.util.Log;
import android.view.View;

import org.kealinghornets.nxtdroid.NXT.NXTThread;
import org.kealinghornets.nxtdroid.NXT.NXTThreadManager;

/**
 * Created by jychuah on 11/3/13.
 */
public class ThreadClickListener implements View.OnClickListener {
    String id;
    public ThreadClickListener(String id) {
        this.id = id;
    }
    @Override
    public void onClick(View view) {
        NXTThread thread = NXTThreadManager.getThreadById(id);
        if (thread != null && thread.getNXTThreadState() == NXTThread.READY) {
            thread.start();
            NXTThreadManager.notifyHandlers();
        }
    }
}
