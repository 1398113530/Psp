package org.ppsspp.ppsspp;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by jalen on 2016/8/9.
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 仅仅是缓存Application的Context，不耗时
         */
        FileDownloader.init(getApplicationContext(),
                new FileDownloadHelper.OkHttpClientCustomMaker() { // is not has to provide.
                    @Override
                    public OkHttpClient customMake() {
                        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        builder.connectTimeout(15_000, TimeUnit.MILLISECONDS);
                        builder.proxy(Proxy.NO_PROXY);
                        return builder.build();
                    }
                });
    }
}
