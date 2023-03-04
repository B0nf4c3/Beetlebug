package app.beetlebug.ctf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.StandardCharsets;

import app.beetlebug.FlagCaptured;
import app.beetlebug.R;

public class InsecureLoggingActivity extends AppCompatActivity {

    Button m_pay, m_flg;
    EditText m_expires, m_cvv, m_card_number, flg ;
    boolean isAllFieldsChecked = false;
    public static String flag_scores = "flag_scores";
    public static String ctf_score_log = "ctf_score_log";
    SharedPreferences sharedPreferences, preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insecure_logging);
        m_pay = (Button) findViewById(R.id.buttonPay);
        m_card_number = (EditText) findViewById(R.id.editTextCardNumber);
        m_expires = (EditText) findViewById(R.id.editTextExpires);
        m_cvv = (EditText) findViewById(R.id.editTextCvv);
        m_pay = findViewById(R.id.buttonPay);
        m_flg = findViewById(R.id.button);

        LinearLayout lin = findViewById(R.id.layoutCtf);
        lin.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences(flag_scores, Context.MODE_PRIVATE);
        preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        m_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllFieldsChecked = CheckAllFields();
                lin.setVisibility(View.VISIBLE);



                if (isAllFieldsChecked) {
                    String card = m_card_number.getText().toString();
                    m_card_number.setError("An Error Occurred");
                    Log.e("beetle-log", "Transaction Failed - on " + "Card Number: " + card + "\n" + "flg: " + getString(R.string._0x532123));
                }
            }
        });

        if(Build.VERSION.SDK_INT>=21){
            Window window=this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));
        }

    }

    public void captureFlag(View view) {
        flg = findViewById(R.id.flag);
        String result = flg.getText().toString();
        String pref_result = preferences.getString("9_log", "");
        byte[] data = Base64.decode(pref_result, Base64.DEFAULT);
        String text = new String(data, StandardCharsets.UTF_8);

        if (result.equals(text)) {
            float user_score_log = 6.25F;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(ctf_score_log, user_score_log);
            editor.commit();

            Intent ctf_captured = new Intent(InsecureLoggingActivity.this, FlagCaptured.class);
            String intent_pref_str = Float.toString(user_score_log);
            ctf_captured.putExtra("intent_str", intent_pref_str);
            startActivity(ctf_captured);
        } else if (result.isEmpty()) {
            flg.setError("Try again");
        }
    }


    private boolean CheckAllFields(){
        if (m_card_number.length() == 0) {
            m_card_number.setError("This field is required");
            return false;
        }

        if (m_expires.length() == 0) {
            m_expires.setError("This field is required");
            return false;
        }

        if (m_expires.length() == 0) {
            m_expires.setError("This field is required");
            return false;
        }

        if (m_cvv.length() == 0) {
            m_cvv.setError("This field is required");
            return false;
        }

        return true;
    }

}