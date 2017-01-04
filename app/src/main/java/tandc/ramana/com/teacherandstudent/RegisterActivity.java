package tandc.ramana.com.teacherandstudent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegisterActivity  extends Activity{
	

	EditText  name,rollNumber,gmail,password;
	Spinner year,userType;
    Button  submit;
    TelephonyManager telephonyManager;
    String DeviceId;
    AsyncTask<Void, Void, Void> mRegisterTask;
	//private GoogleCloudMessaging gcm =null;
   String SENDER_ID = "639539913046";
	//String  SENDER_ID = "446888041326";
//	String  SENDER_ID = "611988245242";
	//String  SENDER_ID = "398933963650";
    String regId;
    SharedPreferences  sh;
    Editor  ed;
	Config  config;
	private GoogleCloudMessaging gcm;
	public String[] classType = {"I","II","III","IV"};
	public String[] type = {"student","teacher"};
	private String  strType,strYear;

  
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		config = new Config();
		initUI();
		ArrayAdapter<String>  adapterclass = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_item,android.R.id.text1,classType);
		ArrayAdapter<String>  adaptertype = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_item,android.R.id.text1,type);
		year.setAdapter(adapterclass);
		userType.setAdapter(adaptertype);
		strType= "student";
		strYear ="I";

		year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				//Toast.makeText(getApplicationContext(), year.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
				strYear= year.getSelectedItem().toString();

			}


			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				//Toast.makeText(getApplicationContext(),userType.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
				strType = userType.getSelectedItem().toString();
				if(strType.equalsIgnoreCase("teacher")){
					rollNumber.setHint("Emp ID");
					year.setVisibility(View.GONE);
				}else {
					rollNumber.setHint("Roll Number");
					year.setVisibility(View.VISIBLE);
				}
			}


			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		sh = getSharedPreferences("Tands", Context.MODE_PRIVATE);
		ed= sh.edit();
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		DeviceId = telephonyManager.getDeviceId();
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		gcm = GoogleCloudMessaging.getInstance(this);

		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				CommonUtilities.DISPLAY_MESSAGE_ACTION));

		GetGCM();

		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					AsyncTaskRunner as = new AsyncTaskRunner(regId);
					as.execute();
			}
		});
		
		
		
		
		
	}

	private void initUI() {

		name = (EditText) findViewById(R.id.Name);

		rollNumber = (EditText)  findViewById(R.id.rollNumber);
		year = (Spinner) findViewById(R.id.year);
		gmail = (EditText)  findViewById(R.id.gmail);
		userType = (Spinner) findViewById(R.id.u_type);
		password = (EditText)  findViewById(R.id.password);
		submit = (Button)  findViewById(R.id.submit);




	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */

			// Showing received message

			Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegi", "> " + e.getMessage());
		}
		super.onDestroy();

}


	
public class AsyncTaskRunner extends AsyncTask<String, String, String> {

	String strName = name.getText().toString();
	String strRollNumber = rollNumber.getText().toString();
	//String  strYear = year.getText().toString();
	String strMail = gmail.getText().toString();
	//String  strType = userType.getText().toString();
	String strPass = password.getText().toString();
	String  strregId ;
		
		protected ProgressDialog progressDialog;

	public AsyncTaskRunner(String regId) {

		strregId = regId
;
	}

	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(RegisterActivity.this, "", "Loading..");



			
		}

		@Override
		protected String doInBackground(String... params) {
			

			// TODO Auto-generated method stub
			
			
			
			String result = null;
			HttpPost httppost;

			try {
				@SuppressWarnings("resource")
				HttpClient httpclient = new DefaultHttpClient();
				String url = config.REGISTERATION_URL;
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("name",strName));
				nameValuePairs.add(new BasicNameValuePair("rollnumber",strRollNumber));
				nameValuePairs.add(new BasicNameValuePair("gcm_regid",strregId));
				nameValuePairs.add(new BasicNameValuePair("bt_year",strYear));
				nameValuePairs.add(new BasicNameValuePair("dev_id",DeviceId));
				nameValuePairs.add(new BasicNameValuePair("gmailid",strMail));
				nameValuePairs.add(new BasicNameValuePair("u_type",strType));
				nameValuePairs.add(new BasicNameValuePair("password",strPass));
				httppost = new HttpPost(url);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);
				
				Log.e("log_tag", "connection success ");
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection" + e.toString());
				
			}


			return result;
		}
		@Override
		protected void onPostExecute(String result) {
			// execution of result of Long time consuming operation
			System.out.println("Result:::::" + result);
			Log.e("Result:::",""+result);
			progressDialog.dismiss();

			try {

				JSONObject job = new JSONObject(result);
				if(job.getString("status").equalsIgnoreCase("1")){


					String id = job.getString("user_id");
					ed.putString("id",id);
					ed.putString("year",strYear);
					ed.commit();

					Intent  i = new Intent(RegisterActivity.this,Login.class);
					startActivity(i);
				}else{
					Toast.makeText(getApplicationContext(),"RegistrationFail",Toast.LENGTH_LONG).show();
				}
				
				
				
				
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	

}

	private void GetGCM() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					GCMHelper  gcmRegistrationHelper = new GCMHelper (
							getApplicationContext());

					 regId = gcmRegistrationHelper.GCMRegister(SENDER_ID);
					ed.putString("regId",regId);
					ed.commit();

					Log.e("ramanaaa",regId);

				} catch (Exception bug) {
					bug.printStackTrace();
				}

			}
		});

		thread.start();
	}

}
