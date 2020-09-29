package com.example.zegowhiteboarddocs.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.zegowhiteboarddocs.R;
import com.example.zegowhiteboarddocs.activity.ZegoDocsActivity;
import com.example.zegowhiteboarddocs.activity.ZegoWhiteBoardActivity;

public class MainActivity extends AppCompatActivity {
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private final static int REQUEST_CODE_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
    }

    /**
     * 请求读写权限
     */
    private void requestPermission() {
        int permission = ActivityCompat.checkSelfPermission(this,
                "android.permission.READ_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_CODE_PERMISSION);
        }
    }

    public void whiteBoard(View view) {
        Intent intent = new Intent(this, ZegoWhiteBoardActivity.class);
        startActivity(intent);
    }

    public void docsExchange(View view) {
        Intent intent = new Intent(this, ZegoDocsActivity.class);
        startActivity(intent);
    }
}