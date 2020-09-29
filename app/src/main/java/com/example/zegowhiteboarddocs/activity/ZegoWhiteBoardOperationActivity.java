package com.example.zegowhiteboarddocs.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.zegowhiteboarddocs.R;
import com.example.zegowhiteboarddocs.utils.FileUtil;

import org.json.JSONObject;


import java.util.HashMap;

import im.zego.zegodocs.IZegoDocsViewLoadListener;
import im.zego.zegodocs.IZegoDocsViewUploadListener;
import im.zego.zegodocs.ZegoDocsView;
import im.zego.zegodocs.ZegoDocsViewConstants;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.constants.ZegoPublisherState;
import im.zego.zegoexpress.constants.ZegoRoomState;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoUser;
import im.zego.zegowhiteboard.ZegoWhiteboardConstants;
import im.zego.zegowhiteboard.ZegoWhiteboardManager;
import im.zego.zegowhiteboard.ZegoWhiteboardView;
import im.zego.zegowhiteboard.callback.IZegoWhiteboardCreateListener;
import im.zego.zegowhiteboard.callback.IZegoWhiteboardDestroyListener;
import im.zego.zegowhiteboard.callback.IZegoWhiteboardGetListListener;
import im.zego.zegowhiteboard.model.ZegoWhiteboardViewModel;
import im.zego.zegodocs.ZegoDocsViewManager;

import static im.zego.zegodocs.ZegoDocsViewConstants.ZegoDocsViewSuccess;
import static im.zego.zegodocs.ZegoDocsViewConstants.ZegoDocsViewUploadStateConvert;
import static im.zego.zegodocs.ZegoDocsViewConstants.ZegoDocsViewUploadStateUpload;


public class ZegoWhiteBoardOperationActivity extends Activity implements View.OnClickListener {
    public final static String ROOMID = "zego_room_id";
    private String roomId;
    private FrameLayout frameLayout;
    private TextView brush;
    private TextView laser;
    private ZegoWhiteboardView mZegoWhiteboardView;
    private Button selectFileBt;
    private final static int REQUEST_CODE_UPLOAD = 100;
    private final static int REQUEST_CODE_PERMISSION = 101;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private ZegoDocsView docsView;
    private TextureView preView;
    private final static String STREAMID = "anjoyzhang";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zego_activity_white_board_operation);
        initView();
        checkOrRequestPermission();
        getIntentExtra();
        loginRoom();
    }

    private void getWhiteBoardList() {
        ZegoWhiteboardManager.getInstance().getWhiteboardViewList(new IZegoWhiteboardGetListListener() {
            @Override
            public void onGetList(int i, ZegoWhiteboardView[] zegoWhiteboardViews) {
                Log.i("anjoy", "onGetList" + zegoWhiteboardViews.length);
                if (zegoWhiteboardViews != null && zegoWhiteboardViews.length > 0) {
                    frameLayout.addView(zegoWhiteboardViews[0], ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                } else {
                    createWhiteBoardView();
                }
            }
        });
    }


    /** 校验并请求权限 */
    /**
     * Check and request permission
     */
    public boolean checkOrRequestPermission() {
        String[] PERMISSIONS_STORAGE = {
                "android.permission.CAMERA",
                "android.permission.RECORD_AUDIO"};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS_STORAGE, 101);
                return false;
            }
        }
        return true;
    }

    private void initPreVideoView() {
        ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
        if (engine == null) {
            Toast.makeText(this, "请初始化express SDK", Toast.LENGTH_SHORT).show();
            return;
        }
        engine.startPreview(new ZegoCanvas(preView));
    }

    private void initView() {
        preView = findViewById(R.id.ttv_pre_video);
        frameLayout = findViewById(R.id.fl_white_board_bg);
        brush = findViewById(R.id.brush);
        brush.setOnClickListener(this);
        laser = findViewById(R.id.laser);
        laser.setOnClickListener(this);
        findViewById(R.id.select).setOnClickListener(this);
        findViewById(R.id.drag).setOnClickListener(this);
        findViewById(R.id.text).setOnClickListener(this);
        findViewById(R.id.rectangle).setOnClickListener(this);
        findViewById(R.id.ellipse).setOnClickListener(this);
        findViewById(R.id.eraser).setOnClickListener(this);
        findViewById(R.id.line).setOnClickListener(this);
        findViewById(R.id.style).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.undo).setOnClickListener(this);
        findViewById(R.id.redo).setOnClickListener(this);
        findViewById(R.id.bt_select_file).setOnClickListener(this);
        docsView = new ZegoDocsView(this);
        frameLayout.addView(docsView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void createWhiteBoardView() {
        ZegoWhiteboardViewModel model = new ZegoWhiteboardViewModel();
        model.setAspectHeight(16);
        model.setAspectWidth(9);
        model.setRoomId(roomId);
        ZegoWhiteboardManager.getInstance().createWhiteboardView(model, new IZegoWhiteboardCreateListener() {
            @Override
            public void onCreate(int i, @Nullable ZegoWhiteboardView zegoWhiteboardView) {
                Log.i("anjoy", "状态：" + i);
                mZegoWhiteboardView = zegoWhiteboardView;
//                zegoWhiteboardView.setBackgroundColor(Color.parseColor("#f4f5f8"));
                frameLayout.addView(zegoWhiteboardView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        });

    }


    /*
    登陆房间
     */
    private void loginRoom() {
        ZegoExpressEngine.getEngine().loginRoom(roomId, new ZegoUser(System.currentTimeMillis() + "", System.currentTimeMillis() + "1"));
        ZegoExpressEngine.getEngine().setEventHandler(new IZegoEventHandler() {
            //todo :还有多个重写方法
            @Override
            public void onRoomStateUpdate(String roomID, ZegoRoomState state, int errorCode, JSONObject extendedData) {
                super.onRoomStateUpdate(roomID, state, errorCode, extendedData);
                Log.i("anjoy", "房间状态：" + state.value());
                if (state.value() == ZegoRoomState.CONNECTED.value()) {
                    Toast.makeText(ZegoWhiteBoardOperationActivity.this, "成功登陆房间", Toast.LENGTH_SHORT).show();
//                    createWhiteBoardView();
                    getWhiteBoardList();
                    initPreVideoView();

                }
            }

            @Override
            public void onPublisherStateUpdate(String streamID, ZegoPublisherState state, int errorCode, JSONObject extendedData) {
                super.onPublisherStateUpdate(streamID, state, errorCode, extendedData);
                Log.i("anjoy", "onPublisherStateUpdate" + "streamID=" + streamID + "state=" + state
                        + "errorCode=" + errorCode + "extendedData=" + extendedData);
            }
        });
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        roomId = intent.getStringExtra(ROOMID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPLOAD && resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();
            String filePath = FileUtil.getPath(this, fileUri);
            uploadFile(filePath);
            Log.i("anjoy", "选择的文件路径：" + filePath);
        }
    }

    private void uploadFile(String filePath) {
        ZegoDocsViewManager.getInstance().uploadFile(filePath, ZegoDocsViewConstants.ZegoDocsViewRenderTypeIMG, new IZegoDocsViewUploadListener() {
            @Override
            public void onUpload(int i, int i1, @NonNull HashMap<String, Object> hashMap) {
                Log.i("anjoy", i + "---" + i1);
                if (i1 == ZegoDocsViewSuccess) {
                    if (i == ZegoDocsViewUploadStateUpload) {

                    } else if (i == ZegoDocsViewUploadStateConvert) {
                        Toast.makeText(ZegoWhiteBoardOperationActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        String fileId = hashMap.get(ZegoDocsViewConstants.UPLOAD_FILEID).toString();
                        Log.i("anjoy", "fileID:" + fileId);
                        addDocsView(fileId);
                    }
                } else {
                    Toast.makeText(ZegoWhiteBoardOperationActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addDocsView(String fileID) {
        docsView.loadFile(fileID, "", new IZegoDocsViewLoadListener() {
            @Override
            public void onLoadFile(int i) {
                Log.i("anjoy", "加载文件的状态码：" + i);
                if (i == 0) {
                    ZegoWhiteboardViewModel model = new ZegoWhiteboardViewModel();
                    model.setAspectHeight(9);
                    model.setAspectWidth(16);
                    model.setRoomId(roomId);
                    model.setName(docsView.getFileName());
                    model.getFileInfo().setFileID(fileID);
                    model.getFileInfo().setFileType(docsView.getFileType());
                    ZegoWhiteboardManager.getInstance().createWhiteboardView(model, new IZegoWhiteboardCreateListener() {
                        @Override
                        public void onCreate(int i, @Nullable ZegoWhiteboardView zegoWhiteboardView) {
                            Log.i("anjoy", "展示转码文件：" + i);
                            frameLayout.addView(zegoWhiteboardView, 0, new LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.MATCH_PARENT));

                        }
                    });

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.brush:
                ZegoWhiteboardManager.getInstance().setToolType(ZegoWhiteboardConstants.ZegoWhiteboardViewToolPen);
                break;
            case R.id.laser:
                ZegoWhiteboardManager.getInstance().setToolType(ZegoWhiteboardConstants.ZegoWhiteboardViewToolLaser);
                break;
            case R.id.select:
                ZegoWhiteboardManager.getInstance().setToolType(ZegoWhiteboardConstants.ZegoWhiteboardViewToolSelector);
                break;
            case R.id.drag:
                ZegoWhiteboardManager.getInstance().setToolType(ZegoWhiteboardConstants.ZegoWhiteboardViewToolNone);
                break;
            case R.id.text:
                ZegoWhiteboardManager.getInstance().setToolType(ZegoWhiteboardConstants.ZegoWhiteboardViewToolText);
                break;
            case R.id.rectangle:
                ZegoWhiteboardManager.getInstance().setToolType(ZegoWhiteboardConstants.ZegoWhiteboardViewToolRect);
                break;
            case R.id.ellipse:
                ZegoWhiteboardManager.getInstance().setToolType(ZegoWhiteboardConstants.ZegoWhiteboardViewToolEllipse);
                break;
            case R.id.eraser:
                ZegoWhiteboardManager.getInstance().setToolType(ZegoWhiteboardConstants.ZegoWhiteboardViewToolEraser);
                break;
            case R.id.line:
                ZegoWhiteboardManager.getInstance().setToolType(ZegoWhiteboardConstants.ZegoWhiteboardViewToolLine);
                break;
            case R.id.style:
//                ZegoWhiteboardManager.getInstance().setToolType(ZegoWhiteboardConstants.);
                break;
            case R.id.clear:
                if (mZegoWhiteboardView != null) {
                    mZegoWhiteboardView.clear();
                }
                break;
            case R.id.undo:
                if (mZegoWhiteboardView != null) {
                    mZegoWhiteboardView.undo();
                }
                break;
            case R.id.redo:
                if (mZegoWhiteboardView != null) {
                    mZegoWhiteboardView.redo();
                }
                break;
            case R.id.bt_select_file:
                openFileDir();
                break;

        }
    }

    private void openFileDir() {
        int permission = ActivityCompat.checkSelfPermission(this,
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
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mZegoWhiteboardView != null) {
            ZegoWhiteboardManager.getInstance().destroyWhiteboardView(mZegoWhiteboardView.getWhiteboardViewModel().getWhiteboardID(), new IZegoWhiteboardDestroyListener() {
                @Override
                public void onDestroy(int i, long l) {
                    Log.i("anjoy", "销毁白板" + i + "---" + l);
                }
            });
        }
    }

}
