package vkassin.com.test5;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telecom.Connection;
import android.util.Log;
import android.widget.VideoView;

@TargetApi(Build.VERSION_CODES.O)
public class CallConnection extends Connection {

    public CallConnection(){
        setConnectionProperties(PROPERTY_SELF_MANAGED);
//        onShowIncomingCallUi();
//        setAudioModeIsVoip(true);

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(MainActivity.context, Main2Activity.class);
        MainActivity.context.startActivity(intent);


    }

    @Override
    public void onAnswer(){
        Log.d("!!!!!", "onAnswer() called");
        //Accept the Call
    }


    @Override
    public void onShowIncomingCallUi() {
// Create an intent which triggers your fullscreen incoming call user interface.
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(MainActivity.context, Main2Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.context, 1, intent, 0);

        // Build the notification as an ongoing high priority item; this ensures it will show as
        // a heads up notification which slides down over top of the current content.
        final Notification.Builder builder = new Notification.Builder(MainActivity.context);
        builder.setOngoing(true);
        builder.setPriority(Notification.PRIORITY_HIGH);

        // Set notification content intent to take user to fullscreen UI if user taps on the
        // notification body.
        builder.setContentIntent(pendingIntent);
        // Set full screen intent to trigger display of the fullscreen UI when the notification
        // manager deems it appropriate.
        builder.setFullScreenIntent(pendingIntent, true);

        // Setup notification content.
        builder.setSmallIcon( R.drawable.ic_launcher_background );
        builder.setContentTitle("Your notification title");
        builder.setContentText("Your notification content.");

        // Use builder.addAction(..) to add buttons to answer or reject the call.

        NotificationManager notificationManager = MainActivity.context.getSystemService(
                NotificationManager.class);
        notificationManager.notify("ttt", 1, builder.build());
    }
}