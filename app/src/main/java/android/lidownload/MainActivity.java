package android.lidownload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.ExternalStorageStats;
import android.download.DownloadListener;
import android.download.LiDownload;
import android.download.Listener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;

import java.io.Externalizable;
import java.io.File;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "MainActivity";
    private final static String url1 = "http://b9.market.mi-img.com/download/AppStore/07106345123d84a1118ef62a0cf3d8b0c8c10bb8d/com.bfhd.bookhome.apk";
    private final static String url2 = "http://b8.market.mi-img.com/download/AppStore/0f7f1a5b279ca44b3012bb8ca9fa7c00d278840cd/hw.code.learningcloud.apk";
    private final static String url3 = "http://b9.market.xiaomi.com/download/AppStore/011ac54a116d544b920574f23ee10f9b66e0779e9/io.suzhi8.H5870BC5B.apk";

    Button down;
    Button down1;
    Button down2;
    Button down3;
    LiDownload download;
    File file;

    long totalLength;
    String readableTotalLength;
    private DownloadTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        down = findViewById(R.id.down);
        down.setOnClickListener(this);

        down1 = findViewById(R.id.down_1);
        down1.setOnClickListener(this);

        down2 = findViewById(R.id.down_2);
        down2.setOnClickListener(this);

        down3 = findViewById(R.id.down_3);
        down3.setOnClickListener(this);

        File root = Environment.getExternalStorageDirectory().getAbsoluteFile();
        file = new File(root, "lidownload");
        download = new LiDownload.Builder()
                .setParentPathFile(file)
                .setMinIntervalMillisCallbackProcess(30)
                .setPassIfAlreadyCompleted(false)
                .builder();
        download.setListener(downloadListener1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.down_1:
                download.start(url1);
                break;
            case R.id.down_2:
//                DownloadTask task3 = new DownloadTask.Builder(url1, file)
////                    .setFilename(filename)
//                        // the minimal interval millisecond for callback progress
//                        .setMinIntervalMillisCallbackProcess(30)
//                        // do re-download even if the task has already been completed in the past.
//                        .setPassIfAlreadyCompleted(false)
//                        .build();
//
//                task3.enqueue(downloadListener4WithSpeed);
                download.start(url2);
                break;
            case R.id.down_3:
//                DownloadTask task2 = new DownloadTask.Builder(url2, file)
////                    .setFilename(filename)
//                        // the minimal interval millisecond for callback progress
//                        .setMinIntervalMillisCallbackProcess(30)
//                        // do re-download even if the task has already been completed in the past.
//                        .setPassIfAlreadyCompleted(false)
//                        .build();
//
//                task2.enqueue(downloadListener4WithSpeed);
                download.start(url3);
                break;
            case R.id.down:
                task = new DownloadTask.Builder(url3, file)
//                    .setFilename(filename)
                        // the minimal interval millisecond for callback progress
                        .setMinIntervalMillisCallbackProcess(30)
                        // do re-download even if the task has already been completed in the past.
                        .setPassIfAlreadyCompleted(false)
                        .build();

                task.enqueue(downloadListener4WithSpeed);
        }
    }

    private DownloadListener4WithSpeed downloadListener4WithSpeed = new DownloadListener4WithSpeed() {
        @Override
        public void taskStart(@NonNull DownloadTask task) {

        }

        @Override
        public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

        }

        @Override
        public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

        }

        @Override
        public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
            totalLength = info.getTotalLength();
            readableTotalLength = Util.humanReadableBytes(totalLength, true);
        }

        @Override
        public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {

        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
            final String readableOffset = Util.humanReadableBytes(currentOffset, true);
            final String progressStatus = readableOffset + "/" + readableTotalLength;
            final String speed = taskSpeed.speed();
            final String progressStatusWithSpeed = progressStatus + "(" + speed + ")";
            Log.d(TAG, ">>fetchProgress getId:" + task.getId() + ",currentOffset:" + currentOffset
                    + ",readableTotalLength:" + readableTotalLength
                    + ",totalLength:" + totalLength
                    + ",progressStatusWithSpeed:" + progressStatusWithSpeed);
        }

        @Override
        public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {

        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
            Log.d(TAG, ">>taskEnd" + task.getFile());
        }
    };

    private DownloadListener1 downloadListener1 = new DownloadListener1() {
        @Override
        public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
            Log.d(TAG, ">>taskStart:" + task.getUrl());
        }

        @Override
        public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {

        }

        @Override
        public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {

        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
            Log.d(TAG, ">>progress " + task.getId() + " currentOffset:" + currentOffset + ",totalLength:" + totalLength);
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
            Log.d(TAG, ">>taskEnd:" + task.getFile());
        }
    };

    private Listener listener = new Listener() {
        @Override
        public void taskStart(@NonNull DownloadTask task) {
            Log.d(TAG, ">>taskStart:" + task.getUrl());
        }

        @Override
        public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {
            Log.d(TAG, ">>connectTrialStart");
        }

        @Override
        public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
            Log.d(TAG, ">>connectTrialEnd");
        }

        @Override
        public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {
            Log.d(TAG, ">>downloadFromBeginning");
        }

        @Override
        public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
            Log.d(TAG, ">>downloadFromBreakpoint");
        }

        @Override
        public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
            Log.d(TAG, ">>connectStart");
        }

        @Override
        public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
            Log.d(TAG, ">>connectEnd");
        }

        @Override
        public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {
            Log.d(TAG, ">>fetchStart blockIndex:" + blockIndex + ",contentLength:" + contentLength);
        }

        @Override
        public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {
            Log.d(TAG, ">>fetchProgress getId:" + task.getId() + "blockIndex:" + blockIndex + ",increaseBytes:" + increaseBytes);
        }

        @Override
        public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {
            Log.d(TAG, ">>fetchEnd blockIndex:" + blockIndex + ",contentLength:" + contentLength);
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
            Log.d(TAG, ">>taskEnd" + task.getFile());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.cancel();
        }
    }
}
