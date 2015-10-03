package net.devdome.bhu.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class PlayServicesUtil {

    public static Boolean checkPlayServices(final Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        final int connectionResult = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        switch (connectionResult) {
            case ConnectionResult.SUCCESS:
                return true;
            case ConnectionResult.SERVICE_DISABLED:
            case ConnectionResult.SERVICE_INVALID:
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Dialog dialog = googleApiAvailability.getErrorDialog(activity, connectionResult, 0);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        activity.finish();
                    }
                });
        }

        return false;
    }
}
