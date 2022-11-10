package nguyenleduan.app.mymotorcycle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.siddharthks.bubbles.FloatingBubbleConfig;
import com.siddharthks.bubbles.FloatingBubbleService;

import java.io.IOException;

import xdroid.toaster.Toaster;


public class SimpleService extends FloatingBubbleService {
    ImageView imgPowerWindow,imgStartUpWindow,imgCoiWindow,imgLockWindow,imagStartWindow,imgUnlockWindow ;
    DataSetting dataSetting = new DataSetting();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null)
            return START_NOT_STICKY;
        if (intent.getAction() == null)
            return super.onStartCommand(intent, flags, startId);
        switch (intent.getAction()){
            case "increase":
                Log.d("increase","-------------------------------");
                super.increaseNotificationCounterBy(1);
                break;
            case "decrease":
                Log.d("decrease","-------------------------------");
                super.decreaseNotificationCounterBy(1);
                break;
            case "updateIcon":
                Log.d("updateIcon","-------------------------------");
//                super.updateBubbleIcon(ContextCompat.getDrawable(getContext(), R.drawable.close_default_icon));
                break;
            case "restoreIcon":
                Log.d("restoreIcon","-------------------------------");
                super.restoreBubbleIcon();
                break;
            case "toggleExpansion":
                Log.d("toggleExpansion","-------------------------------");
                toggleExpansionVisibility();
                break;
        }
        return START_STICKY;
    }

    private void checkLock(){
        if(DataSetting.isLock){
            Toaster.toast("Xe đang khoá !!!\n Cần mở khoá");
        }
    }


    @Override
    protected FloatingBubbleConfig getConfig() {
        Context context = getApplicationContext();
        FloatingBubbleConfig bubble = new FloatingBubbleConfig.Builder()
                .bubbleIcon(ContextCompat.getDrawable(context, R.drawable.ic_logo_vuong))
                .removeBubbleIcon(ContextCompat.getDrawable(context, com.siddharthks.bubbles.R.drawable.close_default_icon))
                .bubbleIconDp(100)
                .expandableView(getInflater().inflate(R.layout.sample_view, null))
                .removeBubbleIconDp(54)
                .paddingDp(4)
                .borderRadiusDp(0)
                .expandableColor(Color.WHITE)
                .triangleColor(0xFF215A64)
                .gravity(Gravity.END)
                .physicsEnabled(false)
                .moveBubbleOnTouch(false)
                .touchClickTime(500)
                .bubbleExpansionIcon(ContextCompat.getDrawable(context, com.siddharthks.bubbles.R.drawable.triangle_icon))
                .build();

        imgUnlockWindow = bubble.getExpandableView().findViewById(R.id.imgUnlockWindow);
        imgStartUpWindow = bubble.getExpandableView().findViewById(R.id.imgStartUpWindow);
        imagStartWindow = bubble.getExpandableView().findViewById(R.id.imagStartWindow);
        imgLockWindow = bubble.getExpandableView().findViewById(R.id.imgLockWindow);
        imgCoiWindow = bubble.getExpandableView().findViewById(R.id.imgCoiWindow);
        imgPowerWindow = bubble.getExpandableView().findViewById(R.id.imgPowerWindow);
        imgStartUpWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLock();
                Log.d("Button imgStartUpWindow ", "12");
                sendSignal(dataSetting.returnData(DataSetting.mStartUp));
            }
        });
        imgPowerWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLock();
                Log.d("Button imgPowerWindow ", "12");
                sendSignal(dataSetting.returnData("Power 1"));
            }
        });
        imgPowerWindow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkLock();
                sendSignal("k");
                return true;
            }
        });
        imagStartWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLock();
                Log.d("Button imagStartWindow ", "12");
                sendSignal("o");

            }
        });
        imgCoiWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLock();
                Log.d("Button imgCoiWindow ", "12");
                sendSignal(dataSetting.returnData(DataSetting.mCoi));
            }
        });
        imgLockWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSetting.isLock = true;
                Log.d("Button imgLockWindow ", "12");
                sendSignal(dataSetting.returnData(DataSetting.mLock));
            }
        });
        imgUnlockWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSetting.isLock = false;
                Log.d("Button imgUnlockWindow ", "12");
                sendSignal("t");
            }
        });
        return bubble;
    }

    public void sendSignal ( String number ) {
        if ( DataSetting.btSocket != null ) {
            try {
                Log.d("Send data service:","---------"+number.toString().getBytes()+"---------");
                DataSetting.btSocket.getOutputStream().write(number.toString().getBytes());
            } catch (IOException e) {

                Log.d("Send data Error:","---------"+number.toString().getBytes()+"---------");
            }
        }
    }
}

