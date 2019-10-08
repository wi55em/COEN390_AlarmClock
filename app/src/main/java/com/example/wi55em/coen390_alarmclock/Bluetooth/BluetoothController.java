package com.example.wi55em.coen390_alarmclock.Bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wi55em.coen390_alarmclock.MainActivity;
import com.example.wi55em.coen390_alarmclock.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BluetoothController {

	private static final int REQUEST_ENABLE_BT = 1;
	public static final String SerialPortUUID="0000dfb1-0000-1000-8000-00805f9b34fb";
	public static final String CommandUUID="0000dfb2-0000-1000-8000-00805f9b34fb";
	public static final String ModelNumberStringUUID="00002a24-0000-1000-8000-00805f9b34fb";

	AlertDialog mScanDeviceDialog;
	private BluetoothAdapter mBluetoothAdapter;
	BluetoothLeService mBluetoothLeService;
	private String serialReceivedText;
	private Context context;
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private Handler mHandler= new Handler();
	private String mDeviceName;
	private String mDeviceAddress;
	private boolean mScanning = false;
	public boolean mConnected = false;

	private int mBaudrate=115200;	//set the default baud rate to 115200
	private String mPassword="AT+PASSWOR=DFRobot\r\n";
	private String mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}
	private static BluetoothGattCharacteristic mSCharacteristic, mModelNumberCharacteristic, mSerialPortCharacteristic, mCommandCharacteristic;

	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

	public enum connectionStateEnum{isNull, isScanning, isToScan, isConnecting , isConnected, isDisconnecting};
	public connectionStateEnum mConnectionState = connectionStateEnum.isNull;

	private Runnable mConnectingOverTimeRunnable=new Runnable(){

		@Override
		public void run() {
			if(mConnectionState== connectionStateEnum.isConnecting)
				mConnectionState= connectionStateEnum.isToScan;
			onConectionStateChange(mConnectionState);
			//mBluetoothLeService.close();
		}};
	private Runnable mDisonnectingOverTimeRunnable=new Runnable(){

		@Override
		public void run() {
			if(mConnectionState== connectionStateEnum.isDisconnecting)
				mConnectionState= connectionStateEnum.isToScan;
			onConectionStateChange(mConnectionState);
			//mBluetoothLeService.close();
		}};

	public BluetoothController(final Context context, BluetoothLeService mBluetoothLeService) {
		this.context = context;
		this.mBluetoothLeService = mBluetoothLeService;
		onCreateProcess();
		//makeGattUpdateIntentFilter();
	}


	public void onCreateProcess() {

		serialBegin(mBaudrate);

		if(!initiate())
		{
			Toast.makeText(context, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			((Activity) context).finish();
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		mScanDeviceDialog = new AlertDialog.Builder(context).setTitle("BLE Device Scan...").setAdapter(mLeDeviceListAdapter, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final BluetoothDevice device = mLeDeviceListAdapter.getDevice(which);
				if (device == null)
					return;
				scanLeDevice(false);

				if(device.getName()==null || device.getAddress()==null)
				{
					mConnectionState= connectionStateEnum.isToScan;
					onConectionStateChange(mConnectionState);
				}

				else {

					System.out.println("onListItemClick " + device.getName().toString());
					System.out.println("Device Name:"+device.getName() + "   " + "Device Name:" + device.getAddress());

					mDeviceName = device.getName().toString();
					mDeviceAddress = device.getAddress().toString();

					/*final Intent intent = new Intent(context, DeviceControlActivity.class);
					intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
					intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
					context.startActivity(intent);*/

					Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
					context.bindService(gattServiceIntent, mServiceConnection, context.BIND_AUTO_CREATE);

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (mBluetoothLeService.connect(mDeviceAddress)) {
						mConnectionState= connectionStateEnum.isConnecting;
						onConectionStateChange(mConnectionState);
						mHandler.postDelayed(mConnectingOverTimeRunnable, 2000);
						context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
					} else {
						mConnectionState= connectionStateEnum.isToScan;
						onConectionStateChange(mConnectionState);
					}
				}
			}
		}).setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				System.out.println("mBluetoothAdapter.stopLeScan");
				mConnectionState = connectionStateEnum.isToScan;
				onConectionStateChange(mConnectionState);
				mScanDeviceDialog.dismiss();
				scanLeDevice(false);
			}}).create();
	}

	boolean initiate() {
		// Use this check to determine whether BLE is supported on the device.
		// Then you can
		// selectively disable BLE-related features.
		if (!context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			return false;
		}

		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			return false;
		}
		return true;
	}

	public void scan() {
		buttonScanOnClickProcess();
		//Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
		//context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	public void serialBegin(int baud){
		mBaudrate=baud;
		mBaudrateBuffer = "AT+CURRUART="+mBaudrate+"\r\n";
	}

	public void serialSend(String theString){
		if (mConnectionState == connectionStateEnum.isConnected) {
			mSCharacteristic.setValue(theString);
			mBluetoothLeService.writeCharacteristic(mSCharacteristic);
		}
	}

	ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			System.out.println("mServiceConnection onServiceConnected");
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				((Activity) context).finish();
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			System.out.println("mServiceConnection onServiceDisconnected");
			mBluetoothLeService = null;
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			((Activity) context).finish();
			return;
		}
	}

	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {											//Four connection state
			case isConnected:
				MainActivity.connectPage.setText("Connected");
				MainActivity.buttonScan.setText("Disconnect");
				MainActivity.connectionStatus.setText("Bluetooth Connected Successfully");
				Timer t = new Timer();
				t.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						serialSend("C");
					}
				}, 0 , 3000);

				break;
			case isConnecting:
				MainActivity.connectPage.setText("Connecting");
				MainActivity.buttonScan.setText("Connecting");
				break;
			case isToScan:
				MainActivity.connectPage.setText("Connect");
				MainActivity.buttonScan.setText("Scan");
				break;
			case isScanning:
				MainActivity.connectPage.setText("Scanning");
				MainActivity.buttonScan.setText("Scanning");
				break;
			case isDisconnecting:
				MainActivity.connectPage.setText("isDisconnecting");
				MainActivity.buttonScan.setText("isDisconnecting");
				MainActivity.connectionStatus.setText("Bluetooth Not Connected \n " +
						"Please scan and choose Bluno as a device");
				break;
			default:
				break;
		}
	}

	public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
		// TODO Auto-generated method stub
		MainActivity.serialReceivedText.setText(theString);							//append the text into the EditText
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
		((ScrollView)MainActivity.serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);
	}

	public void buttonScanOnClickProcess()
	{
		switch (mConnectionState) {
			case isNull:
				mConnectionState= connectionStateEnum.isScanning;
				onConectionStateChange(mConnectionState);
				scanLeDevice(true);
				mScanDeviceDialog.show();
				break;
			case isToScan:
				mConnectionState= connectionStateEnum.isScanning;
				onConectionStateChange(mConnectionState);
				scanLeDevice(true);
				mScanDeviceDialog.show();
				break;
			case isScanning:

				break;

			case isConnecting:

				break;
			case isConnected:
				mBluetoothLeService.disconnect();
				mHandler.postDelayed(mDisonnectingOverTimeRunnable, 10000);
				mConnectionState= connectionStateEnum.isDisconnecting;
				onConectionStateChange(mConnectionState);
				break;
			case isDisconnecting:

				break;

			default:
				break;
		}
	}

	void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			System.out.println("mBluetoothAdapter.startLeScan");
			if(mLeDeviceListAdapter != null) {
				mLeDeviceListAdapter.clear();
				mLeDeviceListAdapter.notifyDataSetChanged();
			}
			if(!mScanning) {
				mScanning = true;
				mBluetoothAdapter.startLeScan(mLeScanCallback);
			}
		} else {
			if(mScanning) {
				mScanning = false;
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		}
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@SuppressLint("DefaultLocale")
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			System.out.println("mGattUpdateReceiver->onReceive->action="+action);
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				mHandler.removeCallbacks(mConnectingOverTimeRunnable);
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				mConnectionState = connectionStateEnum.isToScan;
				onConectionStateChange(mConnectionState);
				mHandler.removeCallbacks(mDisonnectingOverTimeRunnable);
				mBluetoothLeService.close();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the user interface.
				for (BluetoothGattService gattService : mBluetoothLeService.getSupportedGattServices()) {
					System.out.println("ACTION_GATT_SERVICES_DISCOVERED  "+
							gattService.getUuid().toString());
				}
				getGattServices(mBluetoothLeService.getSupportedGattServices());
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				if(mSCharacteristic==mModelNumberCharacteristic)
				{
					if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toUpperCase().startsWith("DF BLUNO")) {
						mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, false);
						mSCharacteristic=mCommandCharacteristic;
						mSCharacteristic.setValue(mPassword);
						mBluetoothLeService.writeCharacteristic(mSCharacteristic);
						mSCharacteristic.setValue(mBaudrateBuffer);
						mBluetoothLeService.writeCharacteristic(mSCharacteristic);
						mSCharacteristic=mSerialPortCharacteristic;
						mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
						mConnectionState = connectionStateEnum.isConnected;
						onConectionStateChange(mConnectionState);

					}
					else {
						Toast.makeText(context, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
						mConnectionState = connectionStateEnum.isToScan;
						onConectionStateChange(mConnectionState);
					}
				}
				else if (mSCharacteristic==mSerialPortCharacteristic) {
					onSerialReceived(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
				}


				System.out.println("displayData "+intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

//            	mPlainProtocol.mReceivedframe.append(intent.getStringExtra(BluetoothLeService.EXTRA_DATA)) ;
//            	System.out.print("mPlainProtocol.mReceivedframe:");
//            	System.out.println(mPlainProtocol.mReceivedframe.toString());


			}
		}
	};

	private void getGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null) return;
		String uuid = null;
		mModelNumberCharacteristic=null;
		mSerialPortCharacteristic=null;
		mCommandCharacteristic=null;
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			uuid = gattService.getUuid().toString();
			System.out.println("displayGattServices + uuid="+uuid);

			List<BluetoothGattCharacteristic> gattCharacteristics =
					gattService.getCharacteristics();
			ArrayList<BluetoothGattCharacteristic> charas =
					new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				uuid = gattCharacteristic.getUuid().toString();
				if(uuid.equals(ModelNumberStringUUID)){
					mModelNumberCharacteristic=gattCharacteristic;
					System.out.println("mModelNumberCharacteristic  "+mModelNumberCharacteristic.getUuid().toString());
				}
				else if(uuid.equals(SerialPortUUID)){
					mSerialPortCharacteristic = gattCharacteristic;
					System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
				}
				else if(uuid.equals(CommandUUID)){
					mCommandCharacteristic = gattCharacteristic;
					System.out.println("mSerialPortCharacteristic  "+mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
				}
			}
			mGattCharacteristics.add(charas);
		}

		if (mModelNumberCharacteristic==null || mSerialPortCharacteristic==null || mCommandCharacteristic==null) {
			Toast.makeText(context, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
			mConnectionState = connectionStateEnum.isToScan;
			onConectionStateChange(mConnectionState);
		}
		else {
			mSCharacteristic=mModelNumberCharacteristic;
			mBluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
			mBluetoothLeService.readCharacteristic(mSCharacteristic);
		}

	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		int scan = 0;

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			((Activity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					scan++;
					if(scan <= 300) {
						System.out.println("mLeScanCallback onLeScan run ");
						mLeDeviceListAdapter.addDevice(device);
					} else {
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						mLeDeviceListAdapter.notifyDataSetChanged();
						scan = 0;
					}
				}
			});
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	public class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator =  ((Activity) context).getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				//System.out.println("Device: " + device.getName() + "name");
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				System.out.println("mInflator.inflate  getView");
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText(R.string.unknown_device);
			viewHolder.deviceAddress.setText(device.getAddress());

			return view;
		}
	}

}