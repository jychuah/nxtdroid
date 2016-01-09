package org.kealinghornets.nxtdroid.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.kealinghornets.nxtdroid.NXT.NXTThread;
import org.kealinghornets.nxtdroid.NXT.NXTThreadManager;
import org.kealinghornets.nxtdroidproject.R;

public class ThreadDisplayAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater;
    private static Drawable play_icon;
    private static Drawable running_icon;
    private static Animation rotate;

	public ThreadDisplayAdapter(Activity activity) {
		this.activity = activity;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NXTThreadManager.registerHandler(new Handler() {
            public void handleMessage(Message msg) {
                notifyDataSetChanged();
            }
        });
        rotate = AnimationUtils.loadAnimation(activity, R.anim.rotate_anim);
        play_icon = activity.getResources().getDrawable(android.R.drawable.ic_media_play);
        running_icon = activity.getResources().getDrawable(android.R.drawable.ic_menu_rotate);
	}
	
	@Override
	public int getCount() {
		return NXTThreadManager.getNumThreads();
	}

	@Override
	public Object getItem(int n) {
		return NXTThreadManager.getThreadByIndex(n);
	}

	@Override
	public long getItemId(int n) {
		return n;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.list_row, null);
		}

        NXTThread item = NXTThreadManager.getThreadByIndex(position);

        String name = item.getNXTThreadName();
        if (item.getNXT() != null) {
            name += " on " + item.getNXT().deviceName;
        } else {
            name += " (disconnected)";
        }

        ThreadDisplayUpdate.updateView(view, name, item.getNXTThreadState());

        ImageView image = (ImageView)view.findViewById(R.id.thread_status_image);
        image.setOnClickListener(new ThreadClickListener(item.getNXTThreadID()));

		return view;
	}

}
