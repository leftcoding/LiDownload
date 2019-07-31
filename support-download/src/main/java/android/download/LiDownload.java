package android.download;

import android.text.TextUtils;

import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.StatusUtil;

import java.io.File;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class LiDownload {
    private Map<String, HashMap<String, DownloadTask>> taskMap = new HashMap<>();
    private int minIntervalMillisCallbackProcess;
    private boolean wifiRequired;
    private String tag;
    private boolean passIfAlreadyCompleted;
    private File parentPathFile;

    private DownloadListener listener;

    public LiDownload(int minIntervalMillisCallbackProcess, boolean wifiRequired, String tag, boolean passIfAlreadyCompleted, File parentPathFile) {
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
        enqueue(url, null);
    }

    public void start(String url, String fileName) {
        enqueue(url, fileName);
    }

    private void enqueue(String url, String fileName) {
        if (TextUtils.isEmpty(url)) {
            throw new RuntimeException("url can't be null");
        }
        String md5 = fileToMD5(url);
        String key = md5 == null ? url : md5;
        HashMap<String, DownloadTask> tasks = taskMap.get(tag);
        DownloadTask downloadTask = null;
        if (tasks != null && !tasks.isEmpty()) {
            downloadTask = tasks.get(key);
        }
        boolean isRunning = isTaskRunning(downloadTask);
        if (isRunning) {
            return;
        }
        if (passIfAlreadyCompleted) {
            if (downloadTask != null) {
                downloadTask.enqueue(listener);
                return;
            }
        }

        DownloadTask task = new DownloadTask.Builder(url, parentPathFile)
                .setFilename(fileName)
                .setMinIntervalMillisCallbackProcess(minIntervalMillisCallbackProcess)
                .setPassIfAlreadyCompleted(passIfAlreadyCompleted)
                .setWifiRequired(wifiRequired)
                .build();
        task.enqueue(listener);
        if (tasks == null) {
            tasks = new HashMap<>();
        }
        tasks.put(key, task);
        taskMap.put(tag, tasks);
    }

    public void clear() {
        DownloadTask downloadTask;
        HashMap<String, DownloadTask> tasks;
        for (Map.Entry<String, HashMap<String, DownloadTask>> entry : taskMap.entrySet()) {
            tasks = entry.getValue();
            if (tasks != null && !tasks.isEmpty()) {
                for (Map.Entry<String, DownloadTask> task : tasks.entrySet()) {
                    downloadTask = task.getValue();
                    downloadTask.cancel();
                }
            }
        }
    }

    public void clearByTag(String tag) {
        HashMap<String, DownloadTask> tasks;
        for (Map.Entry<String, HashMap<String, DownloadTask>> entry : taskMap.entrySet()) {
            tasks = entry.getValue();
            if (tasks != null && !tasks.isEmpty() && TextUtils.equals(tag, entry.getKey())) {
                for (Map.Entry<String, DownloadTask> task : tasks.entrySet()) {
                    task.getValue().cancel();
                }
            }
        }
    }

    public void clear(String url) {
        HashMap<String, DownloadTask> tasks;
        for (Map.Entry<String, HashMap<String, DownloadTask>> entry : taskMap.entrySet()) {
            tasks = entry.getValue();
            if (tasks != null && !tasks.isEmpty() && TextUtils.equals(entry.getKey(), tag)) {
                for (Map.Entry<String, DownloadTask> task : tasks.entrySet()) {
                    if (TextUtils.equals(tag(url), task.getKey())) {
                        final DownloadTask downloadTask = task.getValue();
                        downloadTask.cancel();
                        return;
                    }
                }
            }
        }
    }

    public String getTag() {
        return tag;
    }

    private String tag(String url) {
        String md5 = fileToMD5(url);
        return md5 == null ? url : md5;
    }

    public static class Builder {
        private int minIntervalMillisCallbackProcess;
        private boolean wifiRequired;
        private String tag = String.valueOf(System.currentTimeMillis());
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

        public Builder setTag(String tag) {
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

    private static String fileToMD5(String filePath) {
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
        StringBuilder buf = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            buf.append(Integer.toString((md5Byte & 0xff) + 0x100, 16).substring(1));
        }
        return buf.toString().toUpperCase();
    }

    private boolean isTaskRunning(DownloadTask task) {
        if (task == null) return false;
        final StatusUtil.Status status = StatusUtil.getStatus(task);
        return status == StatusUtil.Status.PENDING || status == StatusUtil.Status.RUNNING;
    }
}
