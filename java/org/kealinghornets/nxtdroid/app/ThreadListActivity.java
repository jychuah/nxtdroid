package org.kealinghornets.nxtdroid.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.kealinghornets.nxtdroid.MyThreadList;
import org.kealinghornets.nxtdroid.app.Joypad.JoypadActivity;
import org.kealinghornets.nxtdroid.app.bluetooth.BluetoothConnectActivityReceiver;
import org.kealinghornets.nxtdroid.app.bluetooth.BluetoothConnector;
import org.kealinghornets.nxtdroid.app.bluetooth.BluetoothDevicePicker;
import org.kealinghornets.nxtdroid.NXT.NXT;
import org.kealinghornets.nxtdroid.NXT.NXTConnectionManager;
import org.kealinghornets.nxtdroid.NXT.NXTThreadManager;
import org.kealinghornets.nxtdroidproject.R;

/**
 * An activity representing a list of NXTThreads. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ThreadDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ThreadListFragment} and the item details (if present) is a
 * {@link ThreadDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ThreadListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class ThreadListActivity extends Activity implements
		ThreadListFragment.Callbacks, BluetoothConnector {

	private final BroadcastReceiver mBluetoothPickerReceiver = new BluetoothConnectActivityReceiver(
			this);
	
	private NXT myNXT = null;
    private NXTConnectionManager manager = new NXTConnectionManager(this);
    private MyThreadList mtl;
    private String deviceName = null;
    private connectAsyncTask mConnectTask;

    /**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_nxtthread_list);

		if (findViewById(R.id.nxtthread_detail_container) != null) {
			mTwoPane = true;
			((ThreadListFragment) getFragmentManager()
					.findFragmentById(R.id.nxtthread_list))
					.setActivateOnItemClick(true);
		}
        if (savedInstanceState == null) {
            mtl = new MyThreadList();
        }

	}

	/**
	 * Callback method from {@link ThreadListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(ThreadDetailFragment.ARG_ITEM_ID, id);
			ThreadDetailFragment fragment = new ThreadDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.nxtthread_detail_container, fragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					ThreadDetailActivity.class);
			detailIntent.putExtra(ThreadDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}

    public void bluetoothMenuClick(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
    }

    public void bluetoothDevicesClick(View view) {
        registerReceiver(mBluetoothPickerReceiver, new IntentFilter(
                BluetoothDevicePicker.ACTION_DEVICE_SELECTED));
        startActivity(new Intent(BluetoothDevicePicker.ACTION_LAUNCH)
                .putExtra(BluetoothDevicePicker.EXTRA_NEED_AUTH, false)
                .putExtra(BluetoothDevicePicker.EXTRA_FILTER_TYPE,
                        BluetoothDevicePicker.FILTER_TYPE_ALL)
                .setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
    }

    public void sendQueueClick(View view) {
        if (myNXT != null) {
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(SendQueueFragment.ARG_NXT_ID, myNXT.deviceAddress);
                SendQueueFragment fragment = new SendQueueFragment();
                fragment.setArguments(arguments);
                getFragmentManager().beginTransaction()
                        .replace(R.id.nxtthread_detail_container, fragment)
                        .commit();

            } else {
                Intent detailIntent = new Intent(this,
                        SendQueueActivity.class);
                detailIntent.putExtra(SendQueueFragment.ARG_NXT_ID, myNXT.deviceAddress);
                startActivity(detailIntent);
            }
        }
    }

    public void joypadClick(View view) {
        Intent intent = new Intent(this, JoypadActivity.class);
        startActivity(intent);
    }

	public void connectToService(String deviceName, String deviceAddress) {
        this.deviceName = deviceName;
        mConnectTask = new connectAsyncTask();
        mConnectTask.execute(deviceAddress);
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_exit:
                int x = 1 / 0;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void connectTaskResult(NXT nxt) {
        if (nxt == null) {
            Log.d("NXT", "Failed to connect");
            Toast.makeText(this, (String)getResources().getText(R.string.connect_failure) + " " + deviceName, Toast.LENGTH_LONG).show();
        } else {
            myNXT = nxt;
            Log.d("NXT", "Connected to " + myNXT.deviceName);
            ((Button)findViewById(R.id.button_send_queue)).setEnabled(true);
            ((Button)findViewById(R.id.button_joypad_control)).setEnabled(true);
            registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    unregisterReceiver(this);
                    ((Button) findViewById(R.id.button_send_queue)).setEnabled(false);
                    ((Button) findViewById(R.id.button_joypad_control)).setEnabled(false);
                    Toast.makeText(context, myNXT.deviceName + " disconnected", Toast.LENGTH_LONG);
                }
            }, new IntentFilter(NXTConnectionManager.LCP_DISCONNECT));
            NXTThreadManager.setAllThreadsNXT(myNXT);
            NXTConnectionManager.setDefaultNXT(myNXT);
            NXTThreadManager.notifyHandlers();
            MediaPlayer player = MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI);
            player.start();
            Toast.makeText(this, (String) getResources().getText(R.string.connect_succeed) + " " + myNXT.deviceName, Toast.LENGTH_LONG).show();
        }
    }

    private class connectAsyncTask extends AsyncTask<String, String, NXT> {
        @Override
        protected NXT doInBackground(String... strings) {
            return manager.connect(strings[0]);
        }

        @Override
        protected void onPostExecute(NXT result) {
            connectTaskResult(result);
        }
    }

}
