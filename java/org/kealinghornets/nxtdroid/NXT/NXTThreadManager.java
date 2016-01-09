package org.kealinghornets.nxtdroid.NXT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.util.Log;

/**
 * A class to manage notifications and instances of {@link org.kealinghornets.nxtdroid.NXT.NXTThread}.
 * It is responsible for assigning NXT Thread IDs.
 *
 * @see org.kealinghornets.nxtdroid.NXT.NXTThread#getNXTThreadID()
 */
public class NXTThreadManager {

    private static ArrayList<Handler> handlers = new ArrayList<Handler>();

    private static int lastThreadID = 0;

	private static List<NXTThread> nxtThreadList = new ArrayList<NXTThread>();

	private static Map<String, NXTThread> nxtThreadMap = new HashMap<String, NXTThread>();

    /**
     * Registers a handler to be notified of {@link org.kealinghornets.nxtdroid.NXT.NXTThread} events
     *
     * @param handler An instance of {@link android.os.Handler}
     */
    public static void registerHandler(Handler handler) {
        handlers.add(handler);
    }

    /**
     * Returns an {@link org.kealinghornets.nxtdroid.NXT.NXTThread} by its internal String ID
     *
     * @param id An {@link org.kealinghornets.nxtdroid.NXT.NXTThread} id
     * @return An {@link org.kealinghornets.nxtdroid.NXT.NXTThread} or <b>null</b> if no matching
     *          thread is found
     * @see org.kealinghornets.nxtdroid.NXT.NXTThread#id;
     */
    public static NXTThread getThreadById(String id) {
        return nxtThreadMap.get(id);
    }

    static void notifyNXTDisconnect(NXT nxt) {
        for (NXTThread thread : nxtThreadList) {
            if (thread.getNXT() == nxt) {
                thread.state = NXTThread.DISCONNECTED;
            }
        }
    }

    /**
     * Get thread by index number when iterating through the thread list
     *
     * @param index A number between 0 and {@link NXTThreadManager#getNumThreads()}
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.NXTThread}
     */
    public static NXTThread getThreadByIndex(int index) {
        return nxtThreadList.get(index);
    }

    /**
     * Gets number of threads managed by {@link org.kealinghornets.nxtdroid.NXT.NXTThreadManager}
     * @return The number of {@link org.kealinghornets.nxtdroid.NXT.NXTThread} instances
     */
    public static int getNumThreads() {
        return nxtThreadList.size();
    }

    /**
     * Assigns all {@link org.kealinghornets.nxtdroid.NXT.NXTThread} instances the same
     * {@link org.kealinghornets.nxtdroid.NXT.NXT}
     * @param nxt An instance of {@link org.kealinghornets.nxtdroid.NXT.NXT}
     * @see NXTThread#setNXT(NXT)
     */
    public static void setAllThreadsNXT(NXT nxt) {
        for (NXTThread threads : nxtThreadList) {
            threads.setNXT(nxt);
        }
    }

    /**
     * Notifies all handlers that an {@link org.kealinghornets.nxtdroid.NXT.NXTThreadManager}
     * has changed. It is called automatically, but can be called explicitly to force an update.
     *
     * @see org.kealinghornets.nxtdroid.NXT.NXTThreadManager#registerHandler(android.os.Handler)
     */
    public static void notifyHandlers() {
        for (Handler h : handlers) {
            h.sendEmptyMessage(0);
        }
    }

    static void deleteThread(NXTThread oldThread) {
        try {
            nxtThreadList.remove(oldThread);
            nxtThreadMap.remove(oldThread.id);
            notifyHandlers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void regenerateThread(NXTThread oldThread) {
        try {
            NXTThread newThread = (NXTThread)oldThread.clone();

            nxtThreadList.remove(newThread);
            nxtThreadMap.remove(newThread.id);
            newThread.id = oldThread.id;
            int oldIndex = nxtThreadList.indexOf(oldThread);
            if (oldIndex >= 0) {
                nxtThreadList.set(oldIndex, newThread);
                nxtThreadMap.remove(oldThread.id);
                nxtThreadMap.put(newThread.id, newThread);
            } else {
                nxtThreadList.add(newThread);
                nxtThreadMap.put(newThread.id, newThread);
            }

            notifyHandlers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("NXTThreadManager", "Regenerated thread");
    }

	static void addItem(NXTThread item) {
        if (!nxtThreadMap.containsKey(item.id)) {
            lastThreadID++;
            nxtThreadList.add(item);
            item.id = "NXTThread " + lastThreadID;
            nxtThreadMap.put(item.id, item);
            notifyHandlers();
        }
	}
}
