package nguyenleduan.app.mymotorcycle;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import java.util.logging.Logger;

public class obServervice extends ContentObserver {
    int previousVolume;
    Context context;

    public obServervice(Context c, Handler handler) {
        super(handler);
        context=c;
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
            Log.d("?????",">0");
    }
}
