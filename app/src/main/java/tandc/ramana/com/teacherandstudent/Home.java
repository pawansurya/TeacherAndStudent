package tandc.ramana.com.teacherandstudent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.fi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by india on 05-11-2016.
 */
public class Home  extends Activity {
    private EditText messageET;

   TextView filePath;
    private ListView messagesContainer;
    private Button sendBtn,chooseBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    SharedPreferences sh;
    SharedPreferences.Editor ed;
    int i = 0;

    File file;



    String upLoadServerUri = null;

    private static final int PICK_FILE_REQUEST = 1;
    private static final String TAG = UplodeFile.class.getSimpleName();
    private String selectedFilePath;
    private String SERVER_URL = "http://coderefer.com/extras/UploadToServer.php";
    ImageView ivAttachment;
    Button bUpload;
    TextView tvFileName;
    ProgressDialog dialog;
    Config config;

    TextView  btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);


        sh = getSharedPreferences("Tands", Context.MODE_PRIVATE);
        String  message = getIntent().getExtras().getString("msg");
        if(message.equalsIgnoreCase("empty")){

        }else{
            savetoDB(message,"2");
        }

        ed= sh.edit();
        btnLogOut = (TextView)  findViewById(R.id.btnlogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  i= new Intent(Home.this,Login.class);
                ed.remove("id");
                ed.commit();
                startActivity(i);
            }
        });

        Log.e("usename",""+sh.getString("id",null));
        Log.e("usename",""+sh.getString("year",null));
        initControls();
        gettheValuesmethod();


    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);
      //  chooseBtn = (Button) findViewById(R.id.buttonChoose);
        tvFileName = (TextView)  findViewById(R.id.editTextName);
        bUpload = (Button)   findViewById(R.id.buttonUpload);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
       // companionLabel.setText("My Buddy");// Hard Coded
        gettheValuesmethod();

        tvFileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);
                AsyncTaskRunner as = new AsyncTaskRunner();
                as.execute();

                messageET.setText("");

                savetoDB(messageText,"1");

                gettheValuesmethod();

              //  displayMessage(chatMessage);


                //uplodeFile();



            }


        });

        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* if(selectedFilePath != null){
                    dialog = ProgressDialog.show(Home.this,"","Uploading File...",true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //creating new thread to handle Http Operations
                            //uploadFile(selectedFilePath);



                        }
                    }).start();
                }else{
                    Toast.makeText(Home.this, "Please choose a File First", Toast.LENGTH_SHORT).show();
                }*/

                file = new File(tvFileName.getText().toString());
                AsyncTaskRunneru us = new AsyncTaskRunneru(file);
                us.execute();

            }


        });
    }

    private void savetoDB(String  str,String type) {

        DataBase dbh = new DataBase(getApplicationContext());
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues  cv = new ContentValues();
        cv.put("type",type);
        cv.put("data",str);
        cv.put("fileType","1");
        db.insert("Data", null, cv);
        db.close();



    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }


    private void gettheValuesmethod() {

        DataBase dbh = new DataBase(getApplicationContext());
        SQLiteDatabase db = dbh.getWritableDatabase();
        String sql = "select * from Data";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            chatHistory = new ArrayList<ChatMessage>();


            if (c.moveToFirst()) {
                do {

                    String type = c.getString(0);
                    if (type.equalsIgnoreCase("1")) {
                        String data = c.getString(1);
                       // ChatMessage = new ArrayList<ChatMessage>();

                        ChatMessage msg = new ChatMessage();
                       // msg.setId(1);
                        msg.setMe(false);
                        msg.setMessage(data);
                        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                        chatHistory.add(msg);
                        i++;


                    } else {
                        String data = c.getString(1);
                        //chatHistory = new ArrayList<ChatMessage>();

                        ChatMessage msg = new ChatMessage();
                       // msg.setId(1);
                        msg.setMe(true);
                        msg.setMessage(data);
                        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                        chatHistory.add(msg);
                        i++;
                    }


                }
                while (c.moveToNext());
            }
            Log.e("data",""+chatHistory.size());
            adapter = new ChatAdapter(Home.this, new ArrayList<ChatMessage>());
            messagesContainer.setAdapter(adapter);

            for (int i = 0; i < chatHistory.size(); i++) {
                ChatMessage message = chatHistory.get(i);
                displayMessage(message);
            }


        } else {
            Toast.makeText(getApplicationContext(), "No values", Toast.LENGTH_LONG).show();
        }
        dbh.close();


    }


    public class AsyncTaskRunner extends AsyncTask<String, String, String> {


        protected ProgressDialog progressDialog;
        String message = messageET.getText().toString();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Home.this, "", "Loading..");


        }

        @Override
        protected String doInBackground(String... params) {


            // TODO Auto-generated method stub


            String result = null;
            HttpPost httppost;

            try {
                @SuppressWarnings("resource")
                HttpClient httpclient = new DefaultHttpClient();
                String url = config.SEND_URL;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
               // nameValuePairs.add(new BasicNameValuePair("msg", "hai ra how r you"));
                nameValuePairs.add(new BasicNameValuePair("msg",message));
                nameValuePairs.add(new BasicNameValuePair("user_id", sh.getString("id",null)));
               // nameValuePairs.add(new BasicNameValuePair("bt_year", sh.getString("year",null)));
                nameValuePairs.add(new BasicNameValuePair("bt_year", "IV"));




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

            try {

                JSONObject job = new JSONObject(result);
                if (job.getString("status").equalsIgnoreCase("1")) {

                } else {
                    Toast.makeText(getApplicationContext(), "sending fail", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent,"Choose File to Upload.."),PICK_FILE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_FILE_REQUEST){
                if(data == null){
                    //no data present
                    return;
                }


                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this,selectedFileUri);
                Log.i(TAG, "Selected File Path:" + selectedFilePath);

                if(selectedFilePath != null && !selectedFilePath.equals("")){
                    tvFileName.setText(selectedFilePath);
                }else{
                    Toast.makeText(this,"Cannot upload file to server",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public int uploadFile(final String selectedFilePath){

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(Config.UPLODE_URL);

                Log.e("ramanaaaaaaa",""+url);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
              //  connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("enctype", "multipart/form-data");
              //  connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("userfile",selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"userfile\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.e(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvFileName.setText("");
                            //tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/"+ fileName);
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Home.this,"File Not Found",Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(Home.this, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(Home.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            return serverResponseCode;
        }

    }

    public class AsyncTaskRunneru extends AsyncTask<String, String, String> {
        File file1;

        protected ProgressDialog progressDialog;

        public AsyncTaskRunneru(File file) {

            file1 = file;

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Home.this, "", "Loading..");


        }

        @Override
        protected String doInBackground(String... params) {


            // TODO Auto-generated method stub


            String result = null;
            HttpPost httppost;

            try {
                @SuppressWarnings("resource")
                HttpClient httpclient = new DefaultHttpClient();
                String url = config.UPLODE_URL;

                httppost = new HttpPost(url);

                //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httppost.addHeader("enctype", "multipart/form-data");



                MultipartEntity entity = new MultipartEntity();
                entity.addPart("myAudioFile", new FileBody(file1));
              //  httppost.setEntity(reqEntity);





                HttpResponse response = httpclient.execute(httppost);
               // HttpEntity entity = response.getEntity();
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

        }


    }
    }



