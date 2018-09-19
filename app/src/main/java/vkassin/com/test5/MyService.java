package vkassin.com.test5;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;

public class MyService extends ConnectionService {

    public static final String TAG = MyService.class.getName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "On Start");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Connection connection = super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request);
        Log.d(TAG, connection.getDisconnectCause().getReason());
        return connection;
    }

    @Override
    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        if (request != null) {
            Log.d(TAG, request.toString());
        }
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request);
    }

    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle
                                                         connectionManagerPhoneAccount, ConnectionRequest request) {
//        CallConnection callConnection = new CallConnection();
//        callConnection.setInitializing();
//        return callConnection;

        Log.d(TAG, "onCreateIncomingConnection");
        CallConnection connection = new CallConnection();
        connection.setInitializing();
        connection.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED);
        connection.setCallerDisplayName("TestID", TelecomManager.PRESENTATION_ALLOWED);

        Bundle extras = new Bundle();
        extras.putBoolean(Connection.EXTRA_ANSWERING_DROPS_FG_CALL, true);
        extras.putString(Connection.EXTRA_CALL_SUBJECT, "Test call subject text");
        connection.putExtras(extras);
        return connection;
    }



}
