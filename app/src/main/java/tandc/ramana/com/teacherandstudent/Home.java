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
import android.support.annotation.NonNull;
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


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
import java.io.InputStream;
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
public class Home extends Activity {
    private EditText messageET;

    TextView filePath;
    private ListView messagesContainer;
    private Button sendBtn, chooseBtn;
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

    TextView btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);


        sh = getSharedPreferences("Tands", Context.MODE_PRIVATE);
        String message = getIntent().getExtras().getString("msg");
        if (message.equalsIgnoreCase("empty")) {

        } else {
            savetoDB(message, "2");
        }

        ed = sh.edit();
        btnLogOut = (TextView) findViewById(R.id.btnlogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, Login.class);
                ed.remove("id");
                ed.commit();
                startActivity(i);
            }
        });

        Log.e("usename", "" + sh.getString("id", null));
        Log.e("usename", "" + sh.getString("year", null));
        initControls();
        gettheValuesmethod();


    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);
        //  chooseBtn = (Button) findViewById(R.id.buttonChoose);
        tvFileName = (TextView) findViewById(R.id.editTextName);
        bUpload = (Button) findViewById(R.id.buttonUpload);

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

                sendMessage(messageET.getText().toString());


            }


        });

        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedFilePath != null) {
                    dialog = ProgressDialog.show(Home.this, "", "Uploading File...", true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //creating new thread to handle Http Operations
//                            uploadFile(selectedFilePath);
                            try {
                                uploadFileToFirebase(selectedFilePath);
                            } catch (FileNotFoundException e) {
                                Log.e(TAG, "run: " + e.getLocalizedMessage());
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(Home.this, "Please choose a File First", Toast.LENGTH_SHORT).show();
                }

//                file = new File(tvFileName.getText().toString());
//                AsyncTaskRunneru us = new AsyncTaskRunneru(file);
//                us.execute();

            }


        });
    }

    private void sendMessage(String messageText) {
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
        savetoDB(messageText, "1");
        gettheValuesmethod();
    }

    private void savetoDB(String str, String type) {

        DataBase dbh = new DataBase(getApplicationContext());
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("data", str);
        cv.put("fileType", "1");
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
            Log.e("data", "" + chatHistory.size());
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
                nameValuePairs.add(new BasicNameValuePair("msg", message));
                nameValuePairs.add(new BasicNameValuePair("user_id", sh.getString("id", null)));
                nameValuePairs.add(new BasicNameValuePair("bt_year", sh.getString("year",null)));
//                nameValuePairs.add(new BasicNameValuePair("bt_year", "IV"));

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
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }


                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this, selectedFileUri);
                Log.i(TAG, "Selected File Path:" + selectedFilePath);

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    tvFileName.setText(selectedFilePath);
                } else {
                    Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void uploadFileToFirebase(String filePath) throws FileNotFoundException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        File file = new File(filePath);
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://test-bf857.appspot.com");
        StorageReference mountainsRef = storageRef.child("files/" + file.getName());


        InputStream stream = new FileInputStream(file);

        UploadTask task = mountainsRef.putStream(stream);


        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                final String url = String.valueOf(downloadUrl);
                Log.i(TAG, "onSuccess: " + downloadUrl.toString() + "  " + url);
                // Toast.makeText(Home.this, "Uploaded file", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageET.setText(url);
                        sendMessage(url);

                    }
                });

            }
        });

    }
}



