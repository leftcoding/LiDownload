### liDownload

基于 `OkDownload` 做二次封装

### 使用

##### 1.初始化

```java
private final static String URL3 = "http://b9.market.xiaomi.com/download/AppStore/011ac54a116d544b920574f23ee10f9b66e0779e9/io.suzhi8.H5870BC5B.apk";

LiDownload download = new LiDownload.Builder()
        .setParentPathFile(file) // 设置下载的文件路径
        .setMinIntervalMillisCallbackProcess(30) // 设置 progress 回调间隔时间
        .setPassIfAlreadyCompleted(false) // 已下载完文件，是否重复下载
        .setTag(TAG) // 设置tag
        .builder();
download.setListener(downloadListener1);
```

##### 2.设置监听

```java
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
         Log.d(TAG, ">>taskEnd" + task.getFile() + ",cause:" + cause.toString() + ",realCause:" + realCause.toString());
         if (cause == EndCause.COMPLETED) {
                // ... do end things
         }
    }
};
```

##### 3.开始下载

```java
download.start(URL3);
```

