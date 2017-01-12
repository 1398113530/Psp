package org.ppsspp.ppsspp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;


import com.google.gson.Gson;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.xiaolu.psp9100220.R;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.ppsspp.ppsspp.util.Base;
import org.ppsspp.ppsspp.util.SecurityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


/**
 * psp游戏文件下载页面
 */
public class DownloadActivity extends Activity {
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private CircleProgress mProgressBar;
    private TextView mDownloadState;
    private Animation operatingAnim;
    private int downloadId;
    private String pspFileUrl = "http://test-gd1.xiaoji001.com/rom/psp/";    //9100220 9100075
    private String url2Xiaolu = "http://api.xiaolu123.com/game/simulator.php";
    private boolean isFromXiaolu = false;

    private FileDownloadListener fileDownloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            LogUtils.d(task.getFilename() + " : " + soFarBytes + " : " + totalBytes);
            mDownloadState.setText(R.string.game_resource_get);
            mProgressBar.setMax(100);
        }

        @Override
        protected void started(BaseDownloadTask task) {
            super.started(task);
            mDownloadState.setText(R.string.download_start);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            LogUtils.d(task.getFilename() + " : " + soFarBytes + " : " + totalBytes + "; 下载地址: " + task.getUrl());
            mDownloadState.setText(getString(R.string.download_game_file) + (int)((float)soFarBytes*100/totalBytes) + "%");
            int progress = (int) ((float)soFarBytes * 100/totalBytes);
            mProgressBar.setProgress(progress);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            LogUtils.d(task.getFilename());
            mProgressBar.setProgress(100);
            mDownloadState.setText(R.string.download_complete);
            LogUtils.i(getFilesDir().getPath());
            new UnzipTask().execute(getExternalFilesDir(null) + File.separator + "psp_game.zip");
        }

        @Override
        protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
            LogUtils.d(task.getFilename() + " : " + ex.getMessage() + " : " + retryingTimes);
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            LogUtils.d(task.getFilename() + " : " + soFarBytes + " : " + totalBytes);
            mDownloadState.setText(R.string.download_pause);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            LogUtils.d(task.getFilename() + " :　" + e.getMessage());
            if (isFromXiaolu) {
                mDownloadState.setText(R.string.download_error);
            }else {
                retryXiaoluUrl();
            }
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            LogUtils.d(task.getFilename());
            mDownloadState.setText(R.string.download_repeat);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);


        mProgressBar = (CircleProgress) this.findViewById(R.id.download_progress);
        mDownloadState = (TextView) this.findViewById(R.id.download_state);

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.download_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        String[] strs = getPackageName().split(".psp");
        pspFileUrl = pspFileUrl + strs[1] + ".zip";
//        pspFileUrl = "http://i5.market.mi-img.com/download/AppStore/0cbbb5e219c77d9ff963b52e28f50438a5f41aa0a/com.and.games505.TerrariaPaid.zip";
        // 判断iso文件是否存在
        File isoFile = new File(getFilesDir().getPath(), "2826.iso");
        if (isoFile.exists()) {
            goMain();
        }else {
            File obbDir = getObbDir();
            if (!obbDir.exists()){
                obbDir.mkdirs();
            }
            final File redirectFile = new File(obbDir, "redirect");
            final File[] zipFiles = obbDir.listFiles();
            if (redirectFile.exists()){
                String redirectStr = IOUtils.readFile(redirectFile.getPath());
                File obbZipFile = new File(redirectStr);
                if (obbZipFile.exists()){
                    new UnzipTask().execute(obbZipFile.getPath());
                }else {
                    LogUtils.w("shit, the zip file from redirect not found");
                }
            } else if (zipFiles != null && zipFiles.length > 0){
                new UnzipTask().execute(zipFiles[0].getPath());
            }else {
                // 权限适配
                askForStorageNetPermission();
            }
        }
    }

    public static final int REQUEST_CODE_STORAGE_PERMISSION = 2537;
    private void askForStorageNetPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }else {
                downloadId = FileDownloader.getImpl().create(pspFileUrl).setAutoRetryTimes(3)
                        .setPath(getExternalFilesDir(null) + File.separator + "psp_game.zip").setListener(fileDownloadListener).start();
            }
        }else{
            downloadId = FileDownloader.getImpl().create(pspFileUrl).setAutoRetryTimes(3)
                    .setPath(getExternalFilesDir(null) + File.separator + "psp_game.zip").setListener(fileDownloadListener).start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadId = FileDownloader.getImpl().create(pspFileUrl).setAutoRetryTimes(3)
                    .setPath(getExternalFilesDir(null) + File.separator + "psp_game.zip").setListener(fileDownloadListener).start();
        }else {
            finish();
        }
    }

    /**
     * 自己拼接游戏文件url下载失败，走小鹿服务端获取下载地址
     */
    private void retryXiaoluUrl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient client = new HttpClient();
                GetMethod method = new GetMethod(assembleUrl(url2Xiaolu));
                method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                        new DefaultHttpMethodRetryHandler(3, false));
                try {
                    int statusCode = client.executeMethod(method);
                    if (statusCode == HttpStatus.SC_OK) {
                        InputStream ins = method.getResponseBodyAsStream();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        in2out(ins, os);
                        byte[] buff = os.toByteArray();
                        String responseBody = new String(buff, "UTF-8");
                        if (responseBody != null) {
                            // 解密
                            responseBody = decodeResponse(responseBody);
                            if (!responseBody.equals("false")) {
                                Gson lGson = new Gson();
                                final ResponseResult lS = lGson.fromJson(responseBody, ResponseResult.class);
                                pspFileUrl = lS.getDownload_url();
                                FileDownloader.getImpl().create(pspFileUrl).setAutoRetryTimes(3)
                                        .setPath(getExternalFilesDir(null) + File.separator + "psp_game.zip").setListener(fileDownloadListener).start();
                                isFromXiaolu = true;
                            }else {
                                finish();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void in2out(InputStream ins, ByteArrayOutputStream os) throws Exception{
        byte[] buff = new byte[1024];
        int len;
        while ((len = ins.read(buff)) != -1) {
            os.write(buff, 0, len);
        }
    }

    public static String decodeResponse(String value) {
        byte[] data = Base.decode(value);
        byte[] resultData = SecurityUtils.decrypt(data);
        return new String(resultData);
    }

    private String assembleUrl(String url2Xiaolu) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("packname", this.getPackageName());
        String paramString = convertParams(params);
        paramString += "&key=" + getKyxKey(paramString);
        return url2Xiaolu + "?" + paramString;
    }

    private String getKyxKey(String params) {
        String first = getMd5("api.kuaiyouxi.com@youxikyxlaile");
        String second = params;
        String key = getMd5(first + second);
        return key;
    }

    private String getMd5(String value) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(value.getBytes());
            return toHexString(md5.digest());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    private String toHexString(byte[] digest) {
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (int i = 0; i < digest.length; i++) {
            sb.append(HEX_DIGITS[(digest[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[digest[i] & 0x0f]);
        }
        return sb.toString();
    }

    private String convertParams(HashMap<String, Object> params) {
        try {

            StringBuilder sb = new StringBuilder();

            if (params != null && params.size() > 0) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    Object value = entry.getValue();
                    value = value == null ? "" : value;
                    sb.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(String.valueOf(value), "UTF-8")).append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    private void goMain() {
        Intent intent2Ppsspp = new Intent(this, PpssppActivity.class);
        startActivity(intent2Ppsspp);
        this.finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("activity_destroy");
        FileDownloader.getImpl().pause(downloadId);
    }

    /**
     * 解压缩任务
     */
    class UnzipTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            // 让progress旋转起来
            if (mProgressBar != null && operatingAnim != null) {
                mProgressBar.startAnimation(operatingAnim);
            }
            mDownloadState.setText(R.string.load_game);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            File outFile = new File(getFilesDir().getPath(), "2826.iso");
            File file = new File(params[0]);
            if (outFile.exists()) {
                return true;
            }
            try {
                ZipFile zipFile = new ZipFile(file);
                for(Enumeration entries = zipFile.entries(); entries.hasMoreElements();){
                    ZipEntry entry = (ZipEntry)entries.nextElement();
                    String zipEntryName = entry.getName();
                    LogUtils.d("文件：" + zipEntryName);
                    if (zipEntryName.endsWith(".iso") ||
                            zipEntryName.endsWith(".cso") ||
                            zipEntryName.endsWith(".ISO") ||
                            zipEntryName.endsWith(".CSO") ||
                            zipEntryName.endsWith(".ELF") ||
                            zipEntryName.endsWith(".elf")) {
                        InputStream in = zipFile.getInputStream(entry);
                        OutputStream out = new FileOutputStream(outFile);
                        IOUtils.in2out(in, out);
                        // 移除zip包
                        IOUtils.deleteFile(file);
                        return true;
                    }
                }
            } catch (ZipException e) {
                e.printStackTrace();
                LogUtils.e(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e(e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (mProgressBar != null && operatingAnim != null) {
                mProgressBar.clearAnimation();
            }
            mDownloadState.setText(R.string.enter_game);
            if (aBoolean) {
                goMain();
            }else {
                LogUtils.d("没有正确解压iso文件");
            }
        }
    }
}
