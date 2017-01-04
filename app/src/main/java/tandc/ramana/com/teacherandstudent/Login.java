package tandc.ramana.com.teacherandstudent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import com.google.android.gms.internal.ho;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    Button btnLogin;
    EditText  edUserName,edPassword;
    SharedPreferences sh;
    TextView  signUp;
    SharedPreferences.Editor ed;
    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUp= (TextView) findViewById(R.id.signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent   i = new Intent(Login.this,RegisterActivity.class);
                startActivity(i);
            }
        });
        config = new Config();

        sh = getSharedPreferences("Tands", Context.MODE_PRIVATE);
        ed= sh.edit();
       /* if(sh.getString("id", null) == null){
            Intent i = new Intent(Login.this,RegisterActivity.class);
            startActivity(i);

        }*/
        initUI();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AsyncTaskRunner1 kuk = new AsyncTaskRunner1();
                kuk.execute();

            }
        });








    }

    private void initUI() {

        edUserName = (EditText)  findViewById(R.id.luserName);
        edPassword = (EditText)  findViewById(R.id.lpassword);
        btnLogin  = (Button)   findViewById(R.id.btnLogin);
    }


    public class AsyncTaskRunner1 extends AsyncTask<String, String, String> {

        protected ProgressDialog progressDialog;
        String strRollNumber ,strPassword;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Login.this, "", "Loading..");
            strRollNumber = edUserName.getText().toString();
            strPassword = edPassword.getText().toString();
        }

        @Override
        protected String doInBackground(String... params) {


            // TODO Auto-generated method stub



            String result = null;
            HttpPost httppost;


            try {
                @SuppressWarnings("resource")
                HttpClient httpclient = new DefaultHttpClient();

				/*String url = "http://pmt.roopasoft.com/DeepSwamp_WEBAPI/api/UserRegister/"+firstName+"/"+lastName+"/"+UserName+"/"
						+email+"/"+password+"/"+cnpassword+"/"+ DeviceId ;*/
                String url = config.LOGIN_URL;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("rollnumber",strRollNumber));
                nameValuePairs.add(new BasicNameValuePair("password",strPassword));
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
            progressDialog.dismiss();
            Log.e("Result:::", "" + result);

            try {
				/*{"status":"Success","msg":"You are registered successfully","user_id":45}*/
                JSONObject job = new JSONObject(result);
                if (job.getString("status").equalsIgnoreCase("1")){

                    JSONArray arraydata = job.getJSONArray("data");
                    JSONObject  data = arraydata.getJSONObject(0);

                    String  id = data.getString("id");
                    String  u_type = data.getString("u_type");
                    String btyear = data.getString("bt_year");


                    ed.putString("id", id);
                    ed.putString("u_type",u_type);
                    ed.putString("year",btyear);
                    ed.commit();
                    Intent  homeIntent = new Intent(Login.this,Home.class);
                    homeIntent.putExtra("msg","empty");
                    startActivity(homeIntent);




                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }


}
