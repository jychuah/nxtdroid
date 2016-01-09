package org.kealinghornets.nxtdroid.app.Joypad;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import org.kealinghornets.nxtdroid.NXT.NXT;
import org.kealinghornets.nxtdroid.NXT.NXTConnectionManager;
import org.kealinghornets.nxtdroid.NXT.NXTThread;
import org.kealinghornets.nxtdroid.NXT.NXTThreadManager;
import org.kealinghornets.nxtdroid.app.ThreadClickListener;
import org.kealinghornets.nxtdroid.app.ThreadDisplayUpdate;
import org.kealinghornets.nxtdroidproject.R;

/**
 * Created by jychuah on 11/13/13.
 */
public class JoypadActivity extends Activity {
    static JoypadView joypad;
    int numButtons = 6;
    JoypadNXTThread jpThread;
    JPButton[] jpButtons = new JPButton[6];
    Handler mHandler;
    boolean init = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_joypad);
        joypad = (JoypadView)findViewById(R.id.joypad_dpad);
        if (!init) {
            init = true;

            NXT nxt = NXTConnectionManager.getDefaultNXT();
            if (nxt != null) {
                jpThread = new JoypadNXTThread();
                jpThread.setNXT(nxt);
                jpThread.joypad = joypad;
                jpThread.start();

                for (int i = 0; i < numButtons; i++) {
                    jpButtons[i] = new JPButton();
                }

                jpButtons[0].view = findViewById(R.id.joypad_button_1_container);
                jpButtons[1].view = findViewById(R.id.joypad_button_2_container);
                jpButtons[2].view = findViewById(R.id.joypad_button_3_container);
                jpButtons[3].view = findViewById(R.id.joypad_button_4_container);
                jpButtons[4].view = findViewById(R.id.joypad_button_5_container);
                jpButtons[5].view = findViewById(R.id.joypad_button_6_container);

                Button1Thread b1 = new Button1Thread();
                b1.setNXT(NXTConnectionManager.getDefaultNXT());
                jpButtons[0].id = b1.getNXTThreadID();

                Button2Thread b2 = new Button2Thread();
                b2.setNXT(NXTConnectionManager.getDefaultNXT());
                jpButtons[1].id = b2.getNXTThreadID();

                Button3Thread b3 = new Button3Thread();
                b3.setNXT(NXTConnectionManager.getDefaultNXT());
                jpButtons[2].id = b3.getNXTThreadID();

                Button4Thread b4 = new Button4Thread();
                b4.setNXT(NXTConnectionManager.getDefaultNXT());
                jpButtons[3].id = b4.getNXTThreadID();

                Button5Thread b5 = new Button5Thread();
                b5.setNXT(NXTConnectionManager.getDefaultNXT());
                jpButtons[4].id = b5.getNXTThreadID();

                Button6Thread b6 = new Button6Thread();
                b6.setNXT(NXTConnectionManager.getDefaultNXT());
                jpButtons[5].id = b6.getNXTThreadID();

                for (int i = 0; i < numButtons; i++) {
                    jpButtons[i].view.setOnClickListener(new ThreadClickListener(jpButtons[i].id));
                }
                updateButtons();

                mHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        post(new Runnable() {
                            public void run() {
                                updateButtons();
                            }
                        });
                    }
                };

                NXTThreadManager.registerHandler(mHandler);
        }
        }
    }

    public void updateButtons() {
        for (int i = 0; i < numButtons; i++) {
            NXTThread thread = NXTThreadManager.getThreadById(jpButtons[i].id);
            if (thread != null) {
                ThreadDisplayUpdate.updateView(jpButtons[i].view, thread.getNXTThreadName(), thread.getNXTThreadState());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        jpThread.running = false;
        for (int i = 0; i < numButtons; i++) {
            NXTThread thread = NXTThreadManager.getThreadById(jpButtons[i].id);
            thread.end();
        }
    }

    class JPButton {
        View view = null;
        String id = "";
    }
}
