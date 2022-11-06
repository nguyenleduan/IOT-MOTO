package nguyenleduan.app.mymotorcycle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.siddharthks.bubbles.FloatingBubbleConfig;
import com.siddharthks.bubbles.FloatingBubbleService;


public class SimpleService extends FloatingBubbleService {
    Button bt ;
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

    @Override
    protected FloatingBubbleConfig getConfig() {
        Context context = getApplicationContext();
        FloatingBubbleConfig bubble = new FloatingBubbleConfig.Builder()
                .bubbleIcon(ContextCompat.getDrawable(context, R.drawable.lock))
                .removeBubbleIcon(ContextCompat.getDrawable(context, com.siddharthks.bubbles.R.drawable.close_default_icon))
                .bubbleIconDp(54)
                .expandableView(getInflater().inflate(R.layout.sample_view, null))
                .removeBubbleIconDp(54)
                .paddingDp(4)
                .borderRadiusDp(0)
                .expandableColor(Color.WHITE)
                .triangleColor(0xFF215A64)
                .gravity(Gravity.END)
                .physicsEnabled(true)
                .moveBubbleOnTouch(false)
                .touchClickTime(500)
                .bubbleExpansionIcon(ContextCompat.getDrawable(context, com.siddharthks.bubbles.R.drawable.triangle_icon))
                .build();
//        bt = bubble.getExpandableView().findViewById(R.id.bt);
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("Button clieck ", "12");
//            }
//        });
        return bubble;
    }
}

