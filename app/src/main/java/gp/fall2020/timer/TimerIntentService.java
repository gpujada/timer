package gp.fall2020.timer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TimerIntentService extends IntentService {

    public static final String EXTRA_MILLIS_LEFT = "gp.fall2020.timer.extra.EXTRA_MILLIS_LEFT";
    private final String CHANNEL_ID_TIMER = "channel_timer";
    private final int NOTIFICATION_ID = 0;

    public TimerIntentService() {
        super("TimerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Get millis from the activity and start a new TimerModel
        long millisLeft = intent.getLongExtra(EXTRA_MILLIS_LEFT, 0);
        TimerModel timerModel = new TimerModel();
        timerModel.start(millisLeft);

        // Create notification channel
        createTimerNotificationChannel();

        while (timerModel.isRunning()) {
            try {
                createTimerNotification(timerModel.toString());
                Thread.sleep(1000);

                if (timerModel.getRemainingMilliseconds() == 0) {
                    timerModel.stop();
                    createTimerNotification("Timer is finished!");
                }
            } catch (InterruptedException e) {
            }
        }
    }

    private void createTimerNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) return;

        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_TIMER, name, importance);
        channel.setDescription(description);
        // Register channel with system
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void createTimerNotification(String text) {
        // Create notification with various properties
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_TIMER)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        // Get compatibility NotificationManager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // Post notification using ID.  If same ID, this notification replaces previous one
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
