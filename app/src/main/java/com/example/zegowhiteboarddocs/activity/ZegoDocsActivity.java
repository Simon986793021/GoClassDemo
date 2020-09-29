package com.example.zegowhiteboarddocs.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.zegowhiteboarddocs.R;
import com.example.zegowhiteboarddocs.ZegoConfig;
import com.example.zegowhiteboarddocs.utils.FileUtil;


import java.net.URI;
import java.security.Permission;
import java.util.HashMap;

import im.zego.zegodocs.IZegoDocsViewListener;
import im.zego.zegodocs.IZegoDocsViewLoadListener;
import im.zego.zegodocs.IZegoDocsViewUploadListener;
import im.zego.zegodocs.ZegoDocsView;
import im.zego.zegodocs.ZegoDocsViewConfig;
import im.zego.zegodocs.ZegoDocsViewConstants;
import im.zego.zegodocs.ZegoDocsViewManager;

import static im.zego.zegodocs.ZegoDocsViewConstants.ZegoDocsViewSuccess;
import static im.zego.zegodocs.ZegoDocsViewConstants.ZegoDocsViewUploadStateConvert;
import static im.zego.zegodocs.ZegoDocsViewConstants.ZegoDocsViewUploadStateUpload;


public class ZegoDocsActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_UPLOAD = 100;
    private final static int REQUEST_CODE_PERMISSION = 101;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private ZegoDocsView docsView;
    private FrameLayout frameLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zego_activity_docs);
        docsView = new ZegoDocsView(this);
        frameLayout = findViewById(R.id.container);
        frameLayout.addView(docsView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ActivityCompat.checkSelfPermission(ZegoDocsActivity.this,
                        "android.permission.READ_EXTERNAL_STORAGE");
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    //打开文件目录
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, REQUEST_CODE_UPLOAD);
                } else {
                    ActivityCompat.requestPermissions(ZegoDocsActivity.this, PERMISSIONS_STORAGE, REQUEST_CODE_PERMISSION);
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPLOAD && resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();
            String filePath = FileUtil.getPath(this, fileUri);
            uploadFile(filePath);
        }
    }

    private void uploadFile(String filePath) {
        ZegoDocsViewManager.getInstance().uploadFile(filePath, ZegoDocsViewConstants.ZegoDocsViewRenderTypeVector, new IZegoDocsViewUploadListener() {
            @Override
            public void onUpload(int i, int i1, @NonNull HashMap<String, Object> hashMap) {
                Log.i("anjoy", i + "---" + i1);
                if (i1 == ZegoDocsViewSuccess) {
                    if (i == ZegoDocsViewUploadStateUpload) {

                    } else if (i == ZegoDocsViewUploadStateConvert) {
                        Toast.makeText(ZegoDocsActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        String fileId = hashMap.get(ZegoDocsViewConstants.UPLOAD_FILEID).toString();
                        Log.i("anjoy", "fileID:" + fileId);
                        addDocsView(fileId);
                    }
                } else {
//                    Toast.makeText(ZegoDocsActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void addDocsView(String fileID) {
        docsView.loadFile(fileID, "", new IZegoDocsViewLoadListener() {
            @Override
            public void onLoadFile(int i) {

            }
        });
    }
}
