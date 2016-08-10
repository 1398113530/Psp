package org.ppsspp.ppsspp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.xiaolu.psp9100220.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import cn.fanrunqi.waveprogress.WaveProgressView;

/**
 * psp游戏文件下载页面
 */
public class DownloadActivity extends Activity {
    private CircleProgress mProgressBar;
    private TextView mDownloadState;
    private Animation operatingAnim;
    private int downloadId;
    private String pspFileUrl = "http://test-gd1.xiaoji001.com/rom/psp/";    //9100220 9100075

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
        downloadId = FileDownloader.getImpl().create(pspFileUrl).setAutoRetryTimes(3)
                .setPath("/sdcard/psp_game.zip").setListener(new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                LogUtils.d(task.getFilename() + " : " + soFarBytes + " : " + totalBytes);
                mDownloadState.setText("游戏资源获取 ...");
                mProgressBar.setMax(100);
            }

            @Override
             protected void started(BaseDownloadTask task) {
                super.started(task);
                mDownloadState.setText("开始下载 ...");
             }

                    @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                LogUtils.d(task.getFilename() + " : " + soFarBytes + " : " + totalBytes);
                mDownloadState.setText("下载游戏文件 " + (int)((float)soFarBytes*100/totalBytes) + "%");
                int progress = (int) ((float)soFarBytes * 100/totalBytes);
                mProgressBar.setProgress(progress);
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                LogUtils.d(task.getFilename());
                mProgressBar.setProgress(100);
                mDownloadState.setText("下载完成");
                new UnzipTask().execute("/sdcard/psp_game.zip");
            }

            @Override
            protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
                LogUtils.d(task.getFilename() + " : " + ex.getMessage() + " : " + retryingTimes);
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                LogUtils.d(task.getFilename() + " : " + soFarBytes + " : " + totalBytes);
                mDownloadState.setText("下载暂停");
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                LogUtils.d(task.getFilename() + " :　" + e.getMessage());
                mDownloadState.setText("下载错误");
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                LogUtils.d(task.getFilename());
                mDownloadState.setText("已经存在相同下载连接与相同存储路径的任务");
            }
        }).start();
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
            mDownloadState.setText("游戏加载中 ...");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            File outFile = new File(Environment.getExternalStorageDirectory(), "2826.iso");
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
            mDownloadState.setText("进入游戏 ...");
            if (aBoolean) {
                goMain();
            }else {
                LogUtils.d("没有正确解压iso文件");
            }
        }
    }
}
