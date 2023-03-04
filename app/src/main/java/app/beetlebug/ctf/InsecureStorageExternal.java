package app.beetlebug.ctf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import app.beetlebug.FlagCaptured;
import app.beetlebug.R;

public class InsecureStorageExternal extends AppCompatActivity {
    EditText m_pass, m_email;
    public static String flag_scores = "flag_scores";
    public static String ctf_score_external = "ctf_score_external";
    LinearLayout mFlagLayout;


    SharedPreferences sharedPreferences, preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insecure_storage_external);
        mFlagLayout = findViewById(R.id.flagLayout);

        mFlagLayout.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences(flag_scores, Context.MODE_PRIVATE);
        preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        boolean isAvailable= false;
        boolean isWritable= false;
        boolean isReadable= false;
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {
            // Operation possible - Read and Write
            isAvailable= true;
            isWritable= true;
            isReadable= true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Operation possible - Read Only
            isAvailable= true;
            isWritable= false;
            isReadable= true;
        } else {
            // SD card not available
            isAvailable = false;
            isWritable= false;
            isReadable= false;
        }


        if(Build.VERSION.SDK_INT>=21){
            Window window=this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }
    }

    public void saveCreds(View view) {

        m_pass = findViewById(R.id.editTextPassword);
        m_email = findViewById(R.id.editTextEmail);

        String email = m_email.getText().toString();
        String pass = m_pass.getText().toString();;
        mFlagLayout.setVisibility(View.VISIBLE);


        if (email.isEmpty()) {
            Toast.makeText(InsecureStorageExternal.this, "Enter email", Toast.LENGTH_SHORT).show();
            m_email.setError("Email cannot be blank");
        } else if (pass.isEmpty()){
            m_pass.setError("Enter your password");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Pass: " + pass);
            sb.append("\n");
            sb.append("Email: " + email);
            sb.append("\n");
            sb.append(getString(R.string._0x444123));

            String data = sb.toString();

            // getExternalStoragePublicDirectory() represents root of external storage, we are using DOWNLOADS
            // We can use following directories: MUSIC, PODCASTS, ALARMS, RINGTONES, NOTIFICATIONS, PICTURES, MOVIES
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            File file = new File(folder, "user.txt");
            writeTextData(file, data);
            Toast.makeText(this, "Data saved to" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        }

    }

    // writeTextData() method save the data into the file in byte format
    // It also toast a message "Done/filepath_where_the_file_is_saved"
    private void writeTextData(File file, String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            Toast.makeText(this, "Done" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void captureFlag(View view) {
        EditText m_flag = findViewById(R.id.flag);
        String pref_result = preferences.getString("4_ext_store", "");
        byte[] data = Base64.decode(pref_result, Base64.DEFAULT);
        String text = new String(data, StandardCharsets.UTF_8);

        if (m_flag.getText().toString().equals(text)) {
            float user_score_external_str = 6.25F;

            // save user score to shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(ctf_score_external, user_score_external_str);
            editor.apply();

            Intent ctf_captured = new Intent(InsecureStorageExternal.this, FlagCaptured.class);
            String intent_pref_str = Float.toString(user_score_external_str);
            ctf_captured.putExtra("intent_str", intent_pref_str);
            startActivity(ctf_captured);
        } else {
            m_flag.setError("Wrong answer");
        }

    }
}