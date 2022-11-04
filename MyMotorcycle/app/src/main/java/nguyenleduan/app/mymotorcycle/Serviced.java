package nguyenleduan.app.mymotorcycle;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import xdroid.toaster.Toaster;

public class Serviced extends Service {
    Timer timer;
    TimerTask timerTask;
    public static int count = 1;
    BluetoothAdapter myBluetoothConnect = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket btSocket = null;
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
        startTimerCallApi();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }


    public void startTimerCallApi() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Count", "---- " + count);
                if(count == DataSetting.timeSearch){
//                    Toaster.toast("App đang hoạt động...");
                    timer.cancel();
                    sendSignal("x");
                    count=0;
                    return;
                }else{
                    count++;
                }
            }
        };
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void sendReturn(){
        try {
            String number = "x";
            Log.d("Send data service:","------------Send return ---------");
            DataSetting.btSocket.getOutputStream().write(number.toString().getBytes());
        }catch (Exception e){
            DataSetting.isConnect = false;
        }
    }
    public void sendSignal ( String number ) {
        Log.d("Send data service:","---------"+number+"-------c--");
        if ( DataSetting.btSocket != null ) {
            try {
                Log.d("Send data service:","---------"+number.toString().getBytes()+"-------c--");
                DataSetting.btSocket.getOutputStream().write(number.toString().getBytes());
                startTimerCallApi();
            } catch (IOException e) {
                DataSetting.btSocket = null;
                DataSetting.isConnect = false;
                if(!DataSetting.addressConnect.isEmpty()){
                    new ConnectBT().execute();
                }else{
                    Log.d("Restart counter:","- =========== Send error=================-------");
                    startTimerCallApi();
                }
            }
        }else{
            if(!DataSetting.addressConnect.isEmpty()){
                new ConnectBT().execute();
            }else{
                Log.d("Restart counter:","- =========== address null =================-------");
                startTimerCallApi();
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID) ;
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("asdasdasd"))
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... devices) {
            Log.d("Quet xe","_______________________");
            try {
                if (DataSetting.btSocket == null || !DataSetting.isConnect  ) {
                    myBluetoothConnect = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dis = myBluetoothConnect.getRemoteDevice(DataSetting.addressConnect);
                    if (ActivityCompat.checkSelfPermission( getBaseContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    }
                    DataSetting.btSocket = dis.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    DataSetting.btSocket.connect();

                    Log.d("Connect service:","-----------------------------");
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                DataSetting.isConnect = false;
//                Toaster.toast("Kết nối thất bại !\n Vui lòng kiểm tra lại");

                Log.d("Restart counter:","- ============== connect fail ==============-------");
                Timer timers = new Timer();
                timerTask = new TimerTask() {
                    public void run() {
                        timers.cancel();
                        new ConnectBT().execute();
                        Log.d("Restart counter:","- ============== connect fail == reconnect=== "+DataSetting.addressConnect+"=========-------");
                        return;
                    }
                };
                timers.schedule(timerTask, 1000, 1000);
            } else {
                sendReturn();
                Log.d("Restart counter:","- ============== connect ok ==============-------");
                startTimerCallApi();
            }
        }
    }
}