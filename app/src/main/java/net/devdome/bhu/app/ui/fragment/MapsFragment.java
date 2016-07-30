package net.devdome.bhu.app.ui.fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.devdome.bhu.app.Config;
import net.devdome.bhu.app.R;
import net.devdome.bhu.app.utility.PermissionUtils;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    Context context;

    public MapsFragment() {
    }

    public static MapsFragment getInstance() {
        return new MapsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.maps_fragment, container, false);
        ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        context = getActivity();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpMap() {

    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(Config.gpsLocation).title("Bingham University"));
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Config.gpsLocation, 15));
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        final PermissionCallback callback = new PermissionCallback() {
            @Override
            public void permissionGranted() {
                googleMap.setMyLocationEnabled(true);
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(getActivity(), "We can't find your locaation unless you grant permissions", Toast.LENGTH_SHORT).show();
            }
        };

        if (Nammu.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) || Nammu.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            googleMap.setMyLocationEnabled(true);
        } else if (Nammu.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.LOCATION_HARDWARE)) {
            PermissionUtils.showRationale(context, "You need to give permission to make calls.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Nammu.askForPermission(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,}, callback);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            });

        } else {
            Nammu.askForPermission(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,}, callback);
        }
    }
}
