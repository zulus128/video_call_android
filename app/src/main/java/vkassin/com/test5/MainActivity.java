package vkassin.com.test5;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static Context context;

    /**
     * Listener to detect incoming calls.
     */
    private class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // called when someone is ringing to this phone

                    Toast.makeText(MainActivity.this,
                            "Incoming: "+incomingNumber,
                            Toast.LENGTH_LONG).show();

                    incomingCall();
                    break;
            }
        }
    }

    private TelecomManager manager;
    private PhoneAccountHandle phoneAccountHandle;
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        manager = (TelecomManager) getSystemService(TELECOM_SERVICE);
//        phoneAccountHandle = new PhoneAccountHandle(new ComponentName(getPackageName(), MyService.class.getName()), "myServiceId");
        phoneAccountHandle = new PhoneAccountHandle(
                new ComponentName(this.getApplicationContext(), MyService.class),
                "example");
//        PhoneAccount.Builder builder = PhoneAccount.builder(phoneAccountHandle,   "a123");
//        builder.setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER | PhoneAccount.CAPABILITY_CONNECTION_MANAGER | PhoneAccount.CAPABILITY_VIDEO_CALLING );
//        builder.setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER);
//        builder.setCapabilities(PhoneAccount.CAPABILITY_CONNECTION_MANAGER);
//        PhoneAccount phoneAccount = builder.build();
        PhoneAccount phoneAccount = PhoneAccount.builder(phoneAccountHandle, "example")
                .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED ).build();

        registerPhoneAccount(phoneAccount);

        final Button button = findViewById(R.id.call_btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                call();
            }
        });
        final Button inButton = findViewById(R.id.incall_btn);
        inButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                incomingCall();

//                Intent startConnectionServiceIntent = new Intent(MainActivity.this, ConnectionService.class);
//                Context context = getApplicationContext();
//                context.startService(startConnectionServiceIntent);
            }
        });

        CallStateListener callStateListener = new CallStateListener();
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    public void registerPhoneAccount(PhoneAccount account) {
        // Enforce the requirement that a connection service for a phone account has the correct
        // permission.
        if (!phoneAccountRequiresBindPermission(account.getAccountHandle())) {
            Log.w("!!!!!",
                    "Phone account does not have BIND_TELECOM_CONNECTION_SERVICE permission.");
            throw new SecurityException("PhoneAccount connection service requires "
                    + "BIND_TELECOM_CONNECTION_SERVICE permission.");
        }
//        addOrReplacePhoneAccount(account);
        manager.registerPhoneAccount(account);

    }

    private List<ResolveInfo> resolveComponent(PhoneAccountHandle phoneAccountHandle) {
        return resolveComponent(phoneAccountHandle.getComponentName(),
                phoneAccountHandle.getUserHandle());
    }

    private List<ResolveInfo> resolveComponent(ComponentName componentName,
                                               UserHandle userHandle) {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(ConnectionService.SERVICE_INTERFACE);
        intent.setComponent(componentName);
        try {
//            if (userHandle != null) {
//                return pm.queryIntentServicesAsUser(intent, 0, userHandle.getIdentifier());
//            } else {
                return pm.queryIntentServices(intent, 0);
//            }
        } catch (SecurityException e) {
            Log.v("!!!!!", "is not visible for the calling user");
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Determines if the connection service specified by a {@link PhoneAccountHandle} requires the
     * {@link Manifest.permission#BIND_TELECOM_CONNECTION_SERVICE} permission.
     *
     * @param phoneAccountHandle The phone account to check.
     * @return {@code True} if the phone account has permission.
     */
    public boolean phoneAccountRequiresBindPermission(PhoneAccountHandle phoneAccountHandle) {
        List<ResolveInfo> resolveInfos = resolveComponent(phoneAccountHandle);
        if (resolveInfos.isEmpty()) {
            Log.w("!!!!!", "phoneAccount not found");
            return false;
        }
        for (ResolveInfo resolveInfo : resolveInfos) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo == null) {
                return false;
            }
            if (/*!Manifest.permission.BIND_CONNECTION_SERVICE.equals(serviceInfo.permission) && */
                    !Manifest.permission.BIND_TELECOM_CONNECTION_SERVICE.equals(
                            serviceInfo.permission)) {
                // The ConnectionService must require either the deprecated BIND_CONNECTION_SERVICE,
                // or the public BIND_TELECOM_CONNECTION_SERVICE permissions, both of which are
                // system/signature only.
                return false;
            }
        }
        return true;
    }

    public void call() {
        Bundle test = new Bundle();
        test.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
        this.checkCallPermission();
        manager.placeCall(Uri.parse("tel:" + "+905355671752"), test);

    }


    public  void incomingCall() {
//        PhoneAccount.Builder builder = new PhoneAccount.Builder(phoneAccountHandle, "CustomAccount");
//        builder.setCapabilities(PhoneAccount.CAPABILITY_CONNECTION_MANAGER);
//        PhoneAccount phoneAccount = builder.build();
//        manager.registerPhoneAccount(phoneAccount);

//        PhoneAccountHandle phoneAccountHandle1 = new PhoneAccountHandle(new ComponentName(getPackageName(), MyService.class.getName()), "myServiceId");
//
        Bundle extras = new Bundle();
        Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, "+905355671752", null);
        extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri);
        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
//        manager.addNewIncomingCall(phoneAccountHandle1, extras);
//        TelecomManager telecomManager = (TelecomManager) getSystemService(this.TELECOM_SERVICE);
//        telecomManager.addNewIncomingCall(phoneAccountHandle, extras);


//        PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(
//                new ComponentName(this.getApplicationContext(), MyService.class),
//                "example");
//
//
//        Bundle extras = new Bundle();
//        Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, "11223344", null);
//        extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri);
        manager.addNewIncomingCall(phoneAccountHandle, extras);
    }

    private void checkCallPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                /**
                 *
                 */
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("This permission was denied earlier by you. This permission is required to call from app .So, in order to use this feature please allow this permission by clicking ok.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        2);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},2);
            }
        }else {
            Toast.makeText(this,"Permission Aleardy granted",Toast.LENGTH_LONG).show();
        }
    }
}
