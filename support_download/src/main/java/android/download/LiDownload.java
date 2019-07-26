package android.download;

import android.text.TextUtils;
import android.util.Log;

import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.StatusUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class LiDownload {
    private Map<Object, DownloadTask> taskMap = new HashMap<>();
    private int minIntervalMillisCallbackProcess;
    private boolean wifiRequired;
    private Object tag;
    private boolean passIfAlreadyCompleted;
    private File parentPathFile;

    private DownloadListener listener;

    public LiDownload(int minIntervalMillisCallbackProcess, boolean wifiRequired, Object tag, boolean passIfAlreadyCompleted, File parentPathFile) {
        this.minIntervalMillisCallbackProcess = minIntervalMillisCallbackProcess;
        this.wifiRequired = wifiRequired;
        this.tag = tag;
        this.passIfAlreadyCompleted = passIfAlreadyCompleted;
        this.parentPathFile = parentPathFile;
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    public void start(String url) {
        enqueue(url, null, false);
    }

    public void start(String url, boolean isBroke) {
        enqueue(url, null, isBroke);
    }

    private void enqueue(String url, String fileName, boolean isBroke) {
        if (TextUtils.isEmpty(url)) {
            throw new RuntimeException("url can't be null");
        }
        String md5 = fileToMD5(url);
        String tag = md5 == null ? url : md5;
        DownloadTask _task = taskMap.get(tag);
        boolean isRunning = isTaskRunning(_task);
        if (isRunning) {
            if (!isBroke) {
                return;
            }
            _task.cancel();
        }
        DownloadTask task = new DownloadTask.Builder(url, parentPathFile)
                .setFilename(fileName)
                .setMinIntervalMillisCallbackProcess(minIntervalMillisCallbackProcess)
                .setPassIfAlreadyCompleted(wifiRequired)
                .build();
        task.enqueue(listener);
        taskMap.put(tag, task);
    }

    public static class Builder {
        private int minIntervalMillisCallbackProcess;
        private boolean wifiRequired;
        private Object tag;
        private boolean passIfAlreadyCompleted;
        private File parentPathFile;

        public Builder() {
        }

        public Builder setMinIntervalMillisCallbackProcess(int minIntervalMillisCallbackProcess) {
            this.minIntervalMillisCallbackProcess = minIntervalMillisCallbackProcess;
            return this;
        }

        public Builder setWifiRequired(boolean wifiRequired) {
            this.wifiRequired = wifiRequired;
            return this;
        }

        public Builder setTag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder setPassIfAlreadyCompleted(boolean passIfAlreadyCompleted) {
            this.passIfAlreadyCompleted = passIfAlreadyCompleted;
            return this;
        }

        public Builder setParentPathFile(File parentPathFile) {
            if (parentPathFile.isFile()) {
                throw new IllegalArgumentException("parent path only accept directory path");
            }
            this.parentPathFile = parentPathFile;
            return this;
        }

        public LiDownload builder() {
            return new LiDownload(minIntervalMillisCallbackProcess, wifiRequired, tag, passIfAlreadyCompleted, parentPathFile);
        }
    }

    public static String fileToMD5(String filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(filePath.getBytes());
            byte[] md5Bytes = digest.digest();
            return convertHashToString(md5Bytes);
        } catch (Exception e) {
            return null;
        }
    }

    private static String convertHashToString(byte[] md5Bytes) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            buf.append(Integer.toString((md5Bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return buf.toString().toUpperCase();
    }

    private boolean isTaskRunning(DownloadTask task) {
        if (task == null) return false;
        final StatusUtil.Status status = StatusUtil.getStatus(task);
        return status == StatusUtil.Status.PENDING || status == StatusUtil.Status.RUNNING;
    }
}
