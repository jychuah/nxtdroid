package org.kealinghornets.nxtdroid.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kealinghornets.nxtdroid.NXT.NXTThread;
import org.kealinghornets.nxtdroidproject.R;

/**
 * Created by jychuah on 11/3/13.
 */
public class ThreadDisplayUpdate {
    private static Drawable play_icon;
    private static Drawable running_icon;
    private static Drawable stopped_icon;
    private static Animation rotate;

    private static void getIcons(Context c) {

        if (play_icon == null) {
            play_icon = c.getResources().getDrawable(android.R.drawable.ic_media_play);
            rotate = AnimationUtils.loadAnimation(c, R.anim.rotate_anim);
            running_icon = c.getResources().getDrawable(android.R.drawable.ic_menu_rotate);
            stopped_icon = c.getResources().getDrawable(android.R.drawable.checkbox_on_background);
        }
    }

    public static void updateView(View view, String name, int state) {
        TextView text = (TextView)view.findViewById(R.id.thread_name);
        text.setText(name);
        icon((ImageView)view.findViewById(R.id.thread_status_image), state);
        ((LinearLayout)view.findViewById(R.id.thumbnail)).postInvalidate();
        ((LinearLayout)view.findViewById(R.id.thumbnail)).invalidate();
    }

    private static void icon(ImageView image, int state) {

        getIcons(image.getContext());
        if (state == NXTThread.READY) {
            image.setImageDrawable(play_icon);
            image.clearAnimation();
        }
        if (state == NXTThread.RUNNING) {
            image.setImageDrawable(running_icon);
            image.startAnimation(rotate);
        }
        if (state == NXTThread.STOPPED) {
            image.setImageDrawable(stopped_icon);
            image.clearAnimation();
        }
    }
}
