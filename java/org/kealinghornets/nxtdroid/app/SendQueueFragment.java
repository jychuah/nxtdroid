package org.kealinghornets.nxtdroid.app;

import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.kealinghornets.nxtdroid.NXT.NXT;
import org.kealinghornets.nxtdroid.NXT.NXTConnectionManager;
import org.kealinghornets.nxtdroidproject.R;

/**
 * Created by jychuah on 11/4/13.
 */
public class SendQueueFragment extends Fragment {
    View view;
    private Handler mHandler;

    NXT myNXT = null;

    public static final String ARG_NXT_ID = "org.kealinghornets.nxtdroid.app.SendQueueActivity.nxt_address";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.thread_detail_layout,
                container, false);
        ImageView image  = (ImageView)view.findViewById(R.id.thread_status_image);
        if (getArguments().containsKey(ARG_NXT_ID)) {
            myNXT = NXTConnectionManager.getNXTByAddress(getArguments().getString(ARG_NXT_ID));
            if (myNXT != null) {

                ((TextView)view.findViewById(R.id.thread_name)).setText(myNXT.deviceName);
                mHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        post(new Runnable() {
                            public void run() {
                                updateThreadDetails();
                            }
                        });
                    }
                };
                myNXT.registerThreadEventHandler(mHandler);
                updateThreadDetails();
            } else {
                Toast.makeText(this.getActivity(), (getResources().getText(R.string.cant_find_nxt_by_address) + " " + getArguments().getString(ARG_NXT_ID)), Toast.LENGTH_LONG).show();
            }
        }
        return view;
    }

    public void updateThreadDetails() {
        ThreadDisplayUpdate.updateView(view, "Send and Receive threads for " + myNXT.deviceName, myNXT.getSendThreadState());
        ((TextView)view.findViewById(R.id.thread_log)).setText(Html.fromHtml(myNXT.threadEventLog));
    }
}