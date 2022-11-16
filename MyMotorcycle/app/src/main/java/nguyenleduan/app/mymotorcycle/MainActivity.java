package nguyenleduan.app.mymotorcycle;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.siddharthks.bubbles.FloatingBubblePermissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private String address = "";
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    Intent mServiceIntent;
    private Serviced mYourService;
    ImageView imgDisconnectBluetooth, imgBT, imgUnLock, imgLock, imgSetting, imgSetTime, imgStartUp, imgCoi, imgOnOff, imgConnect;
    private ProgressDialog progress;
    BluetoothAdapter myBluetoothConnect = null;
    TextView tvAddress, tvStartUp, tvCoi;
    BluetoothSocket btSocket = null;
    DataSetting dataSetting = new DataSetting();
    ArrayList list = new ArrayList();
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final String MY_PREFS_NAME = "MyPrefsFile";
    Vibrator v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingBubblePermissions.startPermissionRequest(this);
        setUp();
        getCache();
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.my));
        mYourService = new Serviced();
        mServiceIntent = new Intent(this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            startService(mServiceIntent);
        }
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth device not available", Toast.LENGTH_LONG).show();
        } else if (!myBluetooth.isEnabled()) {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //check
            }
            startActivityForResult(turnBTon, 1);
        }
        pairedDevicesList();
        Log.d("MMMMMM----------Address", DataSetting.addressConnect + " -----????");
        if (DataSetting.btSocket != null && DataSetting.addressConnect != null && !DataSetting.addressConnect.isEmpty() && DataSetting.isConnect) {
            new ConnectBT().execute();
        }
        DataSetting.mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        startService(new Intent(getApplicationContext(), SimpleService.class));
    }

    ////
    private boolean isSimpleServiceStopped() {
        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SimpleService.class.getName().equals(service.service.getClassName()))
                return false;
        }
        return true;
    }

    private void rung(long time) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(time);
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private boolean sendSignal(String number) {
        Log.d("check send data ", number);
        if (DataSetting.btSocket != null) {
            try {
                DataSetting.btSocket.getOutputStream().write(number.toString().getBytes());
//                Toast.makeText(MainActivity.this, "Đã gửi dữ liệu", Toast.LENGTH_SHORT).show();
                return true;
            } catch (IOException e) {
                msg("Error");
            }
        }
        return false;
    }

    Dialog dialog;

    void dialogBluetooth() {
        Dialog dialogBT = new Dialog(MainActivity.this);
        dialogBT.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBT.setCancelable(false);
        dialogBT.setContentView(R.layout.dialog_bluetooth);
        ImageView imgOK = dialogBT.findViewById(R.id.imgOKST);
        Spinner spList = dialogBT.findViewById(R.id.spList);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spList.setAdapter(adapter);
        spList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String info = ((TextView) view).getText().toString();
                if (info.equals("Chọn Bluetooth")) {
//                        Disconnect();
//                    DataSetting.addressConnect = "";
//                    DataSetting.isConnect = false;
//                    tvAddress.setText("Chưa có kết nối :(");
                } else {
                    DataSetting.addressConnect = info.substring(info.length() - 17);
                    new ConnectBT().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imgOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBT.dismiss();
            }
        });
        dialogBT.show();
    }

    void dialogChangeDelay() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_set_time);
        EditText edit = dialog.findViewById(R.id.edt);
        ImageView imgOK = dialog.findViewById(R.id.imgOKST);
        edit.setText("" + DataSetting.timeSearch);
        imgOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSetting.timeSearch = Integer.parseInt(edit.getText() + "");
                Save();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void dialogSetting() {
        Dialog mDialog = new Dialog(MainActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_setting);
        mDialog.setCancelable(true);
        ImageView imgOK = mDialog.findViewById(R.id.imgOKST);
        Spinner spStartUp = mDialog.findViewById(R.id.spStartUp);
        Spinner spCoi = mDialog.findViewById(R.id.spCoi);
        Spinner spScreen = mDialog.findViewById(R.id.spScreen);
        Spinner spFunction = mDialog.findViewById(R.id.spFunction);
        ArrayAdapter adapterFunction = new ArrayAdapter(this, android.R.layout.simple_spinner_item, DataSetting.listFunction);
        adapterFunction.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spFunction.setAdapter(adapterFunction);
        spFunction.setSelection(dataSetting.getIndexList(DataSetting.listFunction, DataSetting.mFunction));
        spFunction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String info = ((TextView) view).getText().toString();
                if (!info.equals("Không sử dụng")) {
                    DataSetting.mFunction = info;
                    Log.d("-------mFunction", DataSetting.mFunction + "");
                } else {
                    DataSetting.mFunction = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, DataSetting.listCoi);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spCoi.setAdapter(adapter);
        spCoi.setSelection(dataSetting.getIndexList(DataSetting.listCoi, DataSetting.mCoi));
        spCoi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String info = ((TextView) view).getText().toString();
                if (!info.equals("Chọn setting")) {
                    DataSetting.mCoi = info;
                    Log.d("-------Coi", DataSetting.mCoi + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imgOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save();
                rung(500);
                tvStartUp.setText(DataSetting.mStartUp + "");
                tvCoi.setText(DataSetting.mCoi + "");
                mDialog.dismiss();
            }
        });
        ArrayAdapter adapterStartUp = new ArrayAdapter(this, android.R.layout.simple_spinner_item, DataSetting.listStartUp);
        adapterStartUp.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spStartUp.setAdapter(adapterStartUp);
        spStartUp.setSelection(dataSetting.getIndexList(DataSetting.listStartUp, DataSetting.mStartUp));
        spStartUp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String info = ((TextView) view).getText().toString();
                if (!info.equals("Chọn setting")) {
                    DataSetting.mStartUp = info;
                    Log.d("-------Start up", DataSetting.mStartUp + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter adapterStartScreen = new ArrayAdapter(this, android.R.layout.simple_spinner_item, DataSetting.listScreen);
        adapterStartScreen.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spScreen.setAdapter(adapterStartScreen);
        spScreen.setSelection(dataSetting.getIndexList(DataSetting.listScreen, DataSetting.mScreen));
        spScreen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String info = ((TextView) view).getText().toString();
                DataSetting.mScreen = info;
                Log.d("-------Csreen", DataSetting.mScreen + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imgOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save();
                tvStartUp.setText(DataSetting.mStartUp + "");
                tvCoi.setText(DataSetting.mCoi + "");
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    public void getCache() {
        try {
            SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            DataSetting.addressConnect = sharedPref.getString(DataSetting.KeyAddress, null);
            DataSetting.mCoi = sharedPref.getString(DataSetting.KeyCoi, "Nhịp 1");
            DataSetting.mPower = sharedPref.getString(DataSetting.KeyPower, "Power 1");
            DataSetting.mStartUp = sharedPref.getString(DataSetting.KeyStartUp, "Đề 1s");
            DataSetting.mScreen = sharedPref.getString(DataSetting.KeyScreen, "Không sử dụng");
            DataSetting.mFunction = sharedPref.getString(DataSetting.KeyFunction, "Đề xe 2s");
            DataSetting.timeSearch = sharedPref.getInt(DataSetting.KeytimeSearch, 20);

            tvStartUp.setText(DataSetting.mStartUp + "");
            tvCoi.setText(DataSetting.mCoi + "");

        } catch (Exception e) {
            Log.d("Error cache", "" + e);
        }
    }

    public void Save() {
        SharedPreferences mPrefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putInt(DataSetting.KeytimeSearch, DataSetting.timeSearch);
        prefsEditor.putString(DataSetting.KeyAddress, DataSetting.addressConnect);
        prefsEditor.putString(DataSetting.KeyStartUp, DataSetting.mStartUp);
        prefsEditor.putString(DataSetting.KeyCoi, DataSetting.mCoi);
        prefsEditor.putString(DataSetting.KeyPower, DataSetting.mPower);
        prefsEditor.putString(DataSetting.KeyScreen, DataSetting.mScreen);
        prefsEditor.putString(DataSetting.KeyFunction, DataSetting.mFunction);
        prefsEditor.apply();
        Log.d("Save cache SMS", "----------Save cache success");
    }

    public void setValue() {
        if (DataSetting.isConnect) {
            tvAddress.setText(DataSetting.addressConnect + "\n" + "Đã kết nối");
        } else {
            tvAddress.setText("Mất kết nối");
        }
    }

    private void setUp() {
        imgOnOff = findViewById(R.id.imgOnOff);
        imgUnLock = findViewById(R.id.imgUnLock);
        imgConnect = findViewById(R.id.imgConnect);
        tvStartUp = findViewById(R.id.tvStartUp);
        tvCoi = findViewById(R.id.tvCoi);
        imgCoi = findViewById(R.id.imgCoi);
        imgStartUp = findViewById(R.id.imgStartUp);
        imgSetTime = findViewById(R.id.imgSetTime);
        tvAddress = findViewById(R.id.tvAddress);
        imgSetting = findViewById(R.id.imgSetting);
        imgBT = findViewById(R.id.imgBT);
        imgLock = findViewById(R.id.imgLock);
        imgDisconnectBluetooth = findViewById(R.id.imgDisconnectBluetooth);
        imgLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rung(300);
                if (sendSignal(dataSetting.returnData(DataSetting.mLock))) {
                    Disconnect();
                }
            }
        });
        imgUnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataSetting.isConnect) {
                    sendSignal("t");
                    /// un lock
                } else {
                    msg("chưa kết nối");
                }
                rung(300);
            }
        });
        imgSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogChangeDelay();
            }
        });
        imgConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reconnect
                DataSetting.isDisconnect = false;
                if (DataSetting.addressConnect == null || DataSetting.addressConnect.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Chưa có địa chỉ bluetooth", Toast.LENGTH_SHORT).show();
                } else {
                    new ConnectBT().cancel(false);
                    new ConnectBT().execute();
                }
            }
        });
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSetting();
            }
        });
        tvStartUp.setText(DataSetting.mStartUp + "");
        tvCoi.setText(DataSetting.mCoi + "");
        imgCoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rung(300);
                sendSignal(dataSetting.returnData(DataSetting.mCoi));
//                Log.d("SEND DATA Coi", "" + dataSetting.returnData(DataSetting.mCoi));
            }
        });
        imgStartUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rung(300);
                sendSignal(dataSetting.returnData(DataSetting.mStartUp));
                Log.d("SEND DATA Start Up", "" + dataSetting.returnData(DataSetting.mStartUp));
            }
        });
        imgOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rung(300);
                sendSignal("i");
                Log.d("SEND DATA Power", "" + dataSetting.returnData(DataSetting.mPower));
            }
        });
        imgOnOff.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                rung(600);
                sendSignal("k");
                Log.d("SEND DATA", "w");
                return true;
            }
        });
        imgBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBluetooth();
            }
        });
        imgDisconnectBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSetting.isConnect = false;
                DataSetting.isDisconnect = true;
                tvAddress.setText("Chưa có kết nối :(");
                Disconnect();
                rung(800);
            }
        });
        if (DataSetting.isConnect) {
            tvAddress.setText(DataSetting.addressConnect + "\n" + "Đã kết nối :(");
        } else {
            tvAddress.setText("Chưa có kết nối");
        }

    }

    private void Disconnect() {
        if (DataSetting.btSocket != null) {
            try {
                if (DataSetting.isConnect) {
                    tvAddress.setText("Chưa có kết nối");
                    msg("Đã ngắt kết nối !");
                    DataSetting.isConnect = false;
                    DataSetting.btSocket.close();
                }
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void pairedDevicesList() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // check
        }
        try {
            pairedDevices = myBluetooth.getBondedDevices();
            list.clear();
            list.add("Chọn Bluetooth");
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice bt : pairedDevices) {
                    list.add(bt.getName().toString() + "\n" + bt.getAddress().toString());
                }
            } else {
                Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

        }
    }


    private void bluetoothConnect() {
        try {
            if (DataSetting.btSocket == null || !DataSetting.isConnect) {
                myBluetoothConnect = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice dis = myBluetoothConnect.getRemoteDevice(DataSetting.addressConnect);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                }
                DataSetting.btSocket = dis.createInsecureRfcommSocketToServiceRecord(myUUID);
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                DataSetting.btSocket.connect();
                Log.d("Bluetooth connect", "success");
                // TODO check connect ok send data
            }
        } catch (IOException e) {
            String a = "2";
            Log.d("asdas", "asdasd");
//            ConnectSuccess = false;
        }

    }


    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "Đang kết nối...", "Xin chờ !!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (DataSetting.btSocket == null || !DataSetting.isConnect) {
                    myBluetoothConnect = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dis = myBluetoothConnect.getRemoteDevice(DataSetting.addressConnect);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    }
                    DataSetting.btSocket = dis.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    DataSetting.btSocket.connect();
                }
            } catch (IOException e) {
                myBluetoothConnect = null;
                DataSetting.btSocket = null;
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                msg("Kết nối thất bại !\n Vui lòng kiểm tra lại");
            } else {
                msg("Connected");
                if (dialog != null) {
                    dialog.dismiss();
                }
                DataSetting.isConnect = true;
                tvAddress.setText(DataSetting.addressConnect + "\n" + "Đã kết nối");
                sendSignal("x");
                Save();
            }
            progress.dismiss();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Running", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

}