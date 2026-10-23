package io.github.sumyabazar1.weather;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Short-lived, bound-only IPC for CleanYourTV's optional Home panel. It accepts read-only refresh requests from
 * the installed launcher package, reads the city chosen in Weather, and fetches a small snapshot only while a
 * client is bound. It is never started at boot and has no periodic work of its own.
 */
public final class HomePanelService extends Service {
    public static final String ACTION = "io.github.sumyabazar1.weather.HOME_PANEL";
    public static final int REQUEST = 1;
    public static final int RESPONSE = 2;
    public static final int NO_CITY = HomePanelSnapshot.NO_CITY;
    public static final int OFFLINE = HomePanelSnapshot.OFFLINE;
    public static final int READY = HomePanelSnapshot.READY;
    public static final String KEY_STATUS = "status";
    public static final String KEY_CITY = "city";
    public static final String KEY_TEMP = "temp";
    public static final String KEY_FEELS = "feels";
    public static final String KEY_CODE = "code";
    public static final String KEY_DAY = "day";
    public static final String KEY_HUMIDITY = "humidity";
    public static final String KEY_WIND = "wind";
    private static final String TAG = "WeatherHomePanel";
    private static final String LAUNCHER_PACKAGE = "com.gathin.tvhome";
    private static final long REFRESH_MS = 5 * 60_000L;

    private final Handler main = new Handler(Looper.getMainLooper());
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Messenger endpoint = new Messenger(new Handler(Looper.getMainLooper(), this::handle));
    private long lastAttempt;
    private Bundle lastSnapshot;
    private boolean fetching;

    @Override
    public IBinder onBind(Intent intent) {
        return ACTION.equals(intent.getAction()) ? endpoint.getBinder() : null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // This endpoint is bind-only. Reject startService calls so an unrelated app cannot keep it alive.
        stopSelf(startId);
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // A new Home visibility session must get a fresh snapshot immediately.
        lastAttempt = 0;
        lastSnapshot = null;
        return false;
    }

    @Override
    public void onDestroy() {
        main.removeCallbacksAndMessages(null);
        io.shutdownNow();
        super.onDestroy();
    }

    private boolean handle(Message message) {
        if (message.what != REQUEST || message.replyTo == null || !isLauncher(message.sendingUid)) return true;
        long now = System.currentTimeMillis();
        if (fetching) return true;
        if (lastSnapshot != null && now - lastAttempt < REFRESH_MS) {
            reply(message.replyTo, lastSnapshot);
            return true;
        }
        lastAttempt = now;
        SharedPreferences place = getSharedPreferences("place", MODE_PRIVATE);
        if (!place.contains("name")) {
            reply(message.replyTo, HomePanelSnapshot.noCity().toBundle());
            return true;
        }
        double latitude = Double.longBitsToDouble(place.getLong("lat", 0));
        double longitude = Double.longBitsToDouble(place.getLong("lon", 0));
        String city = place.getString("name", "");
        Messenger target = message.replyTo;
        fetching = true;
        io.execute(() -> {
            Forecast.Report report = null;
            try {
                report = Forecast.fetch(latitude, longitude, Forecast.MIN_DAYS, false);
            } catch (IOException | JSONException | RuntimeException e) {
                Log.i(TAG, "Weather refresh unavailable", e);
            }
            Forecast.Report result = report;
            main.post(() -> {
                fetching = false;
                Bundle snapshot = result == null ? offline() : HomePanelSnapshot.ready(city, result).toBundle();
                lastSnapshot = result == null ? null : snapshot;
                reply(target, snapshot);
            });
        });
        return true;
    }

    private boolean isLauncher(int uid) {
        if (uid <= Process.INVALID_UID) return false;
        return isLauncherPackage(getPackageManager().getPackagesForUid(uid));
    }

    static boolean isLauncherPackage(String[] packages) {
        if (packages == null) return false;
        for (String candidate : packages) if (LAUNCHER_PACKAGE.equals(candidate)) return true;
        return false;
    }

    private static Bundle offline() {
        return HomePanelSnapshot.offline().toBundle();
    }

    private static void reply(Messenger target, Bundle data) {
        Message response = Message.obtain(null, RESPONSE);
        response.setData(data);
        try {
            target.send(response);
        } catch (android.os.RemoteException e) {
            Log.i(TAG, "Home panel client disconnected", e);
        }
    }
}
