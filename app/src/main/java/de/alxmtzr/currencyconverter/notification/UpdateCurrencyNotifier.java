package de.alxmtzr.currencyconverter.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import de.alxmtzr.currencyconverter.MainActivity;
import de.alxmtzr.currencyconverter.R;

public class UpdateCurrencyNotifier {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "currency_update_channel";
    private static final String CHANNEL_DESCRIPTION = "Currency update notifications";

    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    public UpdateCurrencyNotifier(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_DESCRIPTION,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("Currency update finished")
                .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(resultPendingIntent);
    }

    public void showNotification() {
        notificationBuilder.setContentText("Currency exchange rates have been updated");
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
