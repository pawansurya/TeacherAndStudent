package tandc.ramana.com.teacherandstudent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

/**
 * Created by india on 06-11-2016.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {        ComponentName comp = new ComponentName(context.getPackageName(),
            GcmIntentServic.class.getName());        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
        Toast.makeText(context, "wow received push notification", Toast.LENGTH_LONG).show();
    }
}

