package tandc.ramana.com.teacherandstudent;

/**
 * Created by india on 05-11-2016.
 */
public class Config {


    //static final  String  BASE_URL = "http://www.rayillusionstudios.com/push/";
    static  final String BASE_URL ="http://pmt.roopasoft.com:8000/push/";

    static final  String LOGIN_URL = BASE_URL+"login.php";

    static final   String  REGISTERATION_URL = BASE_URL+"register.php" ;
    static final   String  SEND_URL = BASE_URL+"send.php" ;
    static final String UPLODE_URL = "http://www.rayillusionstudios.com/push/upload.php";


    static final String GOOGLE_SENDER_ID = "611988245242";  // Place here your Google project Number

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Android Example";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.roopasoft.saveme.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";
}
