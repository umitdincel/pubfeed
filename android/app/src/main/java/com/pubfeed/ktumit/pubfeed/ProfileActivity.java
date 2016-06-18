package com.pubfeed.ktumit.pubfeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ProfileActivity extends AppCompatActivity {

    EditText precision, name;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = (EditText) findViewById(R.id.name);
        precision = (EditText) findViewById(R.id.precision);
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToLocale();
            }
        });
        readFromLocale();
    }

    private void saveToLocale() {
        SharedPreferences sharedPref = getSharedPreferences("PUBFEED", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("pubfeed_name", name.getText().toString());
        editor.putString("pubfeed_precision", precision.getText().toString());
        editor.commit();

    }

    private void readFromLocale() {
        SharedPreferences sharedPref = getSharedPreferences("PUBFEED", Context.MODE_PRIVATE);
        name.setText(sharedPref.getString("pubfeed_name", ""));
        precision.setText(sharedPref.getString("pubfeed_precision", ""));
    }
}
