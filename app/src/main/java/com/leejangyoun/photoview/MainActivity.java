package com.leejangyoun.photoview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.leejangyoun.photoview.write.modify.ModifyActivity;
import com.leejangyoun.photoview.write.write.WriteActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnClickWrite(View v) {
        startActivity(new Intent(getApplicationContext(), WriteActivity.class));
    }

    public void OnClickModify(View v) {
        startActivity(new Intent(getApplicationContext(), ModifyActivity.class));

    }
}
