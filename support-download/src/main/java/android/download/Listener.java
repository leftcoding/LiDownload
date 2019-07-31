package android.download;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;

import java.util.List;
import java.util.Map;

public interface Listener extends DownloadListener {
    void taskStart(@NonNull DownloadTask task);

    /**
     * On start trial connect state.
     * <p/>
     * The trial connection is used for:
     * 1. check whether the local info is valid to resume downloading
     * 2. get the instance length of this resource.
     * 3. check whether the resource support accept range.
     *
     * @param task                the host task.
     * @param requestHeaderFields the request header fields for this connection.
     */
    void connectTrialStart(@NonNull DownloadTask task,
                           @NonNull Map<String, List<String>> requestHeaderFields);

    /**
     * On end trial connect state.
     * <p/>
     * The trial connection is used for:
     * 1. check whether the local info is valid to resume downloading
     * 2. get the instance length of this resource.
     * 3. check whether the resource support accept range.
     *
     * @param task                 the host task.
     * @param responseCode         the response code of this trial connection.
     * @param responseHeaderFields the response header fields for this trial connection.
     */
    void connectTrialEnd(@NonNull DownloadTask task,
                         int responseCode,
                         @NonNull Map<String, List<String>> responseHeaderFields);

    void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info,
                               @NonNull ResumeFailedCause cause);

    void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info);

    void connectStart(@NonNull DownloadTask task, @IntRange(from = 0) int blockIndex,
                      @NonNull Map<String, List<String>> requestHeaderFields);

    void connectEnd(@NonNull DownloadTask task, @IntRange(from = 0) int blockIndex,
                    int responseCode,
                    @NonNull Map<String, List<String>> responseHeaderFields);

    void fetchStart(@NonNull DownloadTask task, @IntRange(from = 0) int blockIndex,
                    @IntRange(from = 0) long contentLength);

    void fetchProgress(@NonNull DownloadTask task, @IntRange(from = 0) int blockIndex,
                       @IntRange(from = 0) long increaseBytes);

    void fetchEnd(@NonNull DownloadTask task, @IntRange(from = 0) int blockIndex,
                  @IntRange(from = 0) long contentLength);

    void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause,
                 @Nullable Exception realCause);
}
