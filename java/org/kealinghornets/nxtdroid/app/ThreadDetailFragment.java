package org.kealinghornets.nxtdroid.app;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.kealinghornets.nxtdroid.NXT.NXTThread;
import org.kealinghornets.nxtdroid.NXT.NXTThreadManager;
import org.kealinghornets.nxtdroidproject.R;

/**
 * A fragment representing a single NXTThread detail screen. This fragment is
 * either contained in a {@link org.kealinghornets.nxtdroid.app.ThreadListActivity} in two-pane mode (on
 * tablets) or a {@link org.kealinghornets.nxtdroid.app.ThreadDetailActivity} on handsets.
 */
public class ThreadDetailFragment extends Fragment {

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

    private View view = null;

	/**
	 * The dummy content this fragment is presenting.
	 */
	private NXTThread mItem;

    private Handler mHandler;


    ThreadClickListener listener;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ThreadDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = NXTThreadManager.getThreadById(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.thread_detail_layout,
				container, false);

        ImageView image  = (ImageView)view.findViewById(R.id.thread_status_image);
        listener = new ThreadClickListener(mItem.getNXTThreadID());
        image.setOnClickListener(listener);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                post(new Runnable() {
                    public void run() {
                        updateThreadDetails();
                    }
                });
            }
        };

        NXTThreadManager.registerHandler(mHandler);
		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			updateThreadDetails();
		}

		return view;
	}

	private ScrollView scroll = null;

	public void updateThreadDetails() {
        if (mItem != null) {
            mItem = NXTThreadManager.getThreadById(mItem.getNXTThreadID());
            listener.id = mItem.getNXTThreadID();
            ThreadDisplayUpdate.updateView(view, mItem.getNXTThreadName() + (mItem.getNXT() != null ? (" on " + mItem.getNXT().deviceName) : " (disconnected)"), mItem.getNXTThreadState());
            TextView log = (TextView) view.findViewById(R.id.thread_log);
            log.setText(Html.fromHtml(mItem.logString));
            scroll = (ScrollView) view.findViewById(R.id.thread_log_scroller);
            scroll.post(new Runnable() {
                @Override
                public void run() {
                       scroll.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
	}
}
