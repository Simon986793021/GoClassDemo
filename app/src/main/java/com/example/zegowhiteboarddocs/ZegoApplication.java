package com.example.zegowhiteboarddocs;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import im.zego.zegodocs.ZegoDocsViewConfig;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegowhiteboard.ZegoWhiteboardManager;
import im.zego.zegowhiteboard.callback.IZegoWhiteboardInitListener;
import im.zego.zegodocs.ZegoDocsViewConstants;
import im.zego.zegodocs.ZegoDocsViewManager;

public class ZegoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ZegoExpressEngine engine = ZegoExpressEngine.createEngine(ZegoConfig.appID, ZegoConfig.appSign, true, ZegoScenario.GENERAL, this, null);

        ZegoWhiteboardManager.getInstance().init(this, new IZegoWhiteboardInitListener() {
            @Override
            public void onInit(int i) {
                Log.i("anjoy", "init:" + i);
            }
        });

        //初始化文件转码
        ZegoDocsViewConfig config = new ZegoDocsViewConfig();
        String dataFolder = Environment.getDataDirectory().getAbsolutePath();
        String cacheFolder = getCacheDir().getAbsolutePath();
        config.setAppID(ZegoConfig.appID);
        config.setAppSign(ZegoConfig.appSign);
        config.setDataFolder(this.getExternalFilesDir(null).getAbsolutePath());
        config.setCacheFolder(this.getExternalCacheDir().getAbsolutePath());
        config.setTestEnv(true);
        ZegoDocsViewManager.getInstance().init(config, errorCode -> {
            Log.i("anjoy", "init docsView result:" + errorCode);

        });
        Log.i("anjoyzhang","Version:"+ ZegoWhiteboardManager.getVersion());
        Log.i("anjoyzhang","---"+ZegoDocsViewManager.getInstance().getVersion());



    }
}
