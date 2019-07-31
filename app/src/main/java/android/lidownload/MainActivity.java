package android.lidownload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.download.LiDownload;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.liulishuo.okdownload.DownloadTask;
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

import java.io.File;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "MainActivity";
    private final static String url1 = "http://b9.market.mi-img.com/download/AppStore/07106345123d84a1118ef62a0cf3d8b0c8c10bb8d/com.bfhd.bookhome.apk";
    private final static String url2 = "http://b8.market.mi-img.com/download/AppStore/0f7f1a5b279ca44b3012bb8ca9fa7c00d278840cd/hw.code.learningcloud.apk";
    private final static String URL3 = "http://b9.market.xiaomi.com/download/AppStore/011ac54a116d544b920574f23ee10f9b66e0779e9/io.suzhi8.H5870BC5B.apk";
    private final static String url4 = "https://ali-fir-pro-binary.fir.im/3a0cdaed66ed97d176fe9bf21f468faf5578d428.apk?auth_key=1564125122-0-0-86a99a75d25df67e792fd50b1eebb8f9";

    Button down;
    Button down1;
    Button down2;
    Button down3;
    Button clear;
    Button clearAll;
    Button clearTag;

    LiDownload download;
    File file;

    long totalLength;
    String readableTotalLength;
    private DownloadTask task;
    String curUrl;

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

        clear = findViewById(R.id.clear);
        clear.setOnClickListener(this);

        clearAll = findViewById(R.id.clear_all);
        clearAll.setOnClickListener(this);

        clearTag = findViewById(R.id.clear_tag);
        clearTag.setOnClickListener(this);


        File root = Environment.getExternalStorageDirectory().getAbsoluteFile();
        file = new File(root, "lidownload");
        download = new LiDownload.Builder()
                .setParentPathFile(file)
                .setMinIntervalMillisCallbackProcess(30)
                .setPassIfAlreadyCompleted(false)
                .setTag(TAG)
                .builder();
        download.setListener(downloadListener1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_tag:
                download.clearByTag(TAG);
                break;
            case R.id.clear_all:
                download.clear();
                break;
            case R.id.clear:
                download.clear(curUrl);
                break;
            case R.id.down_1:
                curUrl = url1;
                download.start(url1);
                break;
            case R.id.down_2:
                curUrl = url2;
                download.start(url2);
                break;
            case R.id.down_3:
                curUrl = URL3;
                download.start(URL3);
                break;
            case R.id.down:
                curUrl = URL3;
                task = new DownloadTask.Builder(URL3, file)
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
            Log.d(TAG, ">>taskEnd" + task.getFile() + ",cause:" + cause.toString() + ",realCause:" + realCause.toString());
            if (cause == EndCause.COMPLETED) {
                // ... do end things
            }
        }
    };

    private DownloadListener1 downloadListener1 = new DownloadListener1() {
        @Override
        public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
            Log.d(TAG, ">>taskStart:" + task.getUrl());
        }

        @Override
        public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
            Log.d(TAG, ">>taskEnd" + task.getFile() + ",cause:" + cause.toString());
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
            Log.d(TAG, ">>taskEnd" + task.getFile() + ",cause:" + cause.toString());
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
