package com.example.zegowhiteboarddocs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zegowhiteboarddocs.R;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.entity.ZegoUser;

import static com.example.zegowhiteboarddocs.activity.ZegoWhiteBoardOperationActivity.ROOMID;

public class ZegoWhiteBoardActivity extends AppCompatActivity {
    private EditText roomIdEt;
    private Button loginRoomBt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zego_activity_white_board);
        roomIdEt = findViewById(R.id.et_zego_room_id);
        loginRoomBt = findViewById(R.id.bt_zego_login_room);
    }

    public void loginRoom(View view) {
        String roomId = roomIdEt.getText().toString().trim();
        if (roomId.isEmpty()){
            Toast.makeText(this,"房间号为空",Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this,ZegoWhiteBoardOperationActivity.class);
            intent.putExtra(ROOMID,roomId);
            startActivity(intent);
        }
    }
}
