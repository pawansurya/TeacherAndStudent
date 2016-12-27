package tandc.ramana.com.teacherandstudent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by india on 05-11-2016.
 */
public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    SharedPreferences sh;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                sh = getSharedPreferences("Tands", Context.MODE_PRIVATE);
                ed= sh.edit();

                if(sh.getString("id", null) == null){
                    Intent i = new Intent(SplashScreen.this,Login.class);
                    startActivity(i);

                }else{
                    Intent i = new Intent(SplashScreen.this,Home.class);
                    i.putExtra("msg","empty");
                    startActivity(i);

                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}