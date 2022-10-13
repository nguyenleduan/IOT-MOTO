package nguyenleduan.app.mymotorcycle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
public class ScreenReceiver extends BroadcastReceiver {
    DataSetting dataSetting = new DataSetting();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            //TODO
            Log.d("Phone off screen","---------------------------------");
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Serviced service = new Serviced();
            service.sendSignal(dataSetting.returnData(DataSetting.mScreen));
            Log.d("Phone on screen","---------------------------------");
        }
        return;
    }
}