package rejasupotaro.rebuild.tools;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class MainThreadExecutor implements Executor {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable r) {
        HANDLER.post(r);
    }
}

