package com.netas;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.google.android.gcm.GCMRegistrar;
import com.netas.receivers.MyDeviceAdminReceiver;
import com.netas.utilities.CommonUtilities;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class NetasDemoActivity extends Activity {

	private String deviceToken = null;

	Button btnToggleBluetooth = null;
	Button btnADBEnable = null;
	Button btnADBDisable = null;
	Button btnLockScreen = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btnToggleBluetooth = (Button) findViewById(R.id.buttonToggleBluetooth);
		btnADBEnable = (Button) findViewById(R.id.buttonADBEnable);
		btnADBDisable = (Button) findViewById(R.id.buttonADBDisable);
		btnLockScreen = (Button) findViewById(R.id.buttonSave);

		btnToggleBluetooth.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggleBluetooth();
			}
		});

		btnLockScreen.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				lockScreen();
			}
		});

		btnADBEnable.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setADBEnable(true);
			}
		});

		btnADBDisable.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setADBEnable(false);
			}
		});

		handlePushNotification();
	}

	public void lockScreen() {
		DevicePolicyManager policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName adminReceiver = new ComponentName(this, MyDeviceAdminReceiver.class);
		boolean admin = policyManager.isAdminActive(adminReceiver);
		if (admin) {
			policyManager.lockNow();
		} else {
			ComponentName mAdminName = new ComponentName(this, MyDeviceAdminReceiver.class);

			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					"Sistemi ele geçireceğim. Bilgin olsun istedim.");
			startActivityForResult(intent, 0);
		}
	}

	public void toggleBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.disable();
		} else {
			mBluetoothAdapter.enable();
		}
	}
	
	public void openBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.enable();
	}
	
	public void closeBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.disable();
	}

	public void setADBEnable(boolean enable) {
		// Bu metodun çalışabilmesi için <uses-permission
		// android:name="android.permission.WRITE_SECURE_SETTINGS"/>
		// eklenmelidir.
		// Ancak bu izinler sadece "system app" olan uygulamalar için
		// geçerlidir. Uygulamayı system app yapmak için cihazın ROOT yapıması
		// veya diğer bazı yöntemlerin kullanılması gerekiyor.

		if (enable) {
			// Settings.Secure.putInt(getContentResolver(),Settings.Secure.ADB_ENABLED,
			// 1);
		} else {
			// Settings.Secure.putInt(getContentResolver(),Settings.Secure.ADB_ENABLED,
			// 0);
		}

		Toast.makeText(this, "Bu özelliğin çalışması için cihazın ROOT olması gerekiyor", Toast.LENGTH_SHORT).show();
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, "mesaj geldi", duration);
			toast.show();
			receivePushNotification(intent.getExtras(), true);
		}
	};

	public void receivePushNotification(Bundle bundle, boolean isActive) {
		try {
			if (bundle != null) {
				String text = bundle.getString(CommonUtilities.EXTRA_MESSAGE);
				String param = bundle.getString(CommonUtilities.EXTRA_PARAM);

				if (text.compareTo("OpenBluetooth") == 0) {
					openBluetooth();
				}else if (text.compareTo("CloseBluetooth") == 0) {
					closeBluetooth();
				}
			}
		} catch (Exception ex) {
		}
	}

	private void handlePushNotification() {
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			try {
				GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
			} catch (Exception e) {
				Toast.makeText(this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		} else {
			setDeviceToken(regId);
		}
	}

	public void setDeviceToken(String regId) {
		this.deviceToken = regId;
		Toast.makeText(this, "Device Token : " + regId, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		Log.d(this.getClass().getName(), "On Destroy called.");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.d(this.getClass().getName(), "On Pause called.");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.d(this.getClass().getName(), "On Restart called.");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.d(this.getClass().getName(), "On Resume called.");
		super.onResume();
	}

	@Override
	protected void onStart() {
		Log.d(this.getClass().getName(), "On Start called.");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.d(this.getClass().getName(), "On Stop called.");
		super.onStop();
	}
}