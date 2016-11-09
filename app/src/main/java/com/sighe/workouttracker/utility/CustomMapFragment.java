package com.sighe.workouttracker.utility;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
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
 * Created by dad on 11/2/2016.
 */

public class CustomMapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private static final String SUPPORT_MAP_BUNDLE_KEY = "MapOptions";
    private static CustomMapFragment mFragment;
    private static boolean mAddRouteInfo = false;
    private static Route mRoute;
    private OnCustomMapFragmentListener mCallback;

    public static CustomMapFragment newInstance() {
        mFragment = new CustomMapFragment();
        return mFragment;
    }

    public static CustomMapFragment newInstance(GoogleMapOptions options) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(SUPPORT_MAP_BUNDLE_KEY, options);

        mFragment = new CustomMapFragment();
        mFragment.setArguments(arguments);
        return mFragment;
    }

    public static CustomMapFragment newInstance(Route route) {
        mRoute = route;
        mAddRouteInfo = true;
        mFragment = new CustomMapFragment();
        return mFragment;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mAddRouteInfo) addRouteInfo(googleMap);
        if (mCallback != null) {
            mCallback.onMapReady(googleMap, mRoute);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnCustomMapFragmentListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass().getName() + " must implement OnCustomMapFragmentListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mFragment.getMapAsync(this);
        return view;
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

    public interface OnCustomMapFragmentListener {
        void onMapReady(GoogleMap googleMap, Route route);
    }

}
