package com.sighe.workouttracker.utility;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sighe.workouttracker.data.DBHelper;
import com.sighe.workouttracker.data.Route;
import com.sighe.workouttracker.data.RouteDetail;

import java.util.List;

/**
 * Created by dad on 11/3/2016.
 */

public class CustomMapGalleryFragment extends SupportMapFragment implements OnMapReadyCallback {
    private static final String SUPPORT_MAP_BUNDLE_KEY = "MapOptions";
    private CustomMapGalleryFragment mFragment;
    private boolean mAddRouteInfo = false;
    private Route mRoute;
    private CustomMapGalleryFragmentListener mCallback;

    public CustomMapGalleryFragment() {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mRoute = getArguments().getParcelable("Route");
        if (mRoute != null) mAddRouteInfo = true;
        mFragment = new CustomMapGalleryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (CustomMapGalleryFragmentListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass().getName() + " must implement OnGoogleMapFragmentListener");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mAddRouteInfo) addRouteInfo(googleMap);
        if (mCallback != null) {
            mCallback.onMapReady(googleMap);
        }
    }

    private void addRouteInfo(GoogleMap map) {
        DBHelper dbHelper = new DBHelper(getContext());

        LatLng center = new LatLng(mRoute.getCenterLat(), mRoute.getCenterLon());
        map.addMarker(new MarkerOptions().position(center).title(mRoute.getName()));
        map.moveCamera(CameraUpdateFactory.newLatLng(center));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15f));

        List<RouteDetail> routeDetailList = dbHelper.getRouteDetails(mRoute.getEventNo());

        if (routeDetailList != null) {
            // Add a marker in Sydney and move the camera
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE);

            for (RouteDetail routeDetail : routeDetailList) {
                options.add(new LatLng(routeDetail.getLocationLat(), routeDetail.getLocationLon()));
            }

            Polyline line = map.addPolyline(options);
        }
    }

    public interface CustomMapGalleryFragmentListener {
        void onMapReady(GoogleMap googleMap);
    }
}
