package com.sighe.workouttracker.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sighe.workouttracker.R;
import com.sighe.workouttracker.data.DBHelper;
import com.sighe.workouttracker.data.Route;
import com.sighe.workouttracker.data.RouteDetail;
import com.sighe.workouttracker.utility.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MapsActivity extends AppCompatActivity {
    public static final String ROUTE_NAME = "routeName";
    public static final String MAP_FRAGEMENT_TAG = "map";
    private int mMapCount = 0;

    private List<Route> mRoutes;

    private ListFragment mList;
    private MapAdapter mAdapter;
    private AbsListView.RecyclerListener mRecycleListener = new AbsListView.RecyclerListener() {

        @Override
        public void onMovedToScrapHeap(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder != null && holder.map != null) {
                // Clear the map and free up resources by changing the map type to none
                holder.map.clear();
                holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
            }

        }
    };




    /*private void addMapDetails(){
        DBHelper dbHelper = new DBHelper(this);

        for (int i = 0; i < mRoutes.size(); i++) {
            LatLng center = new LatLng(mRoutes.get(i).getCenterLat(), mRoutes.get(i).getCenterLon());
            mMap.get(i).addMarker(new MarkerOptions().position(center).title(mRoutes.get(i).getName()));
            mMap.get(i).moveCamera(CameraUpdateFactory.newLatLng(center));
            mMap.get(i).moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15f));

            List<RouteDetail> routeDetailList = dbHelper.getRouteDetails(mRoutes.get(i).getEventNo());

            if (routeDetailList != null) {
                // Add a marker in Sydney and move the camera
                PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE);

                for (RouteDetail routeDetail : routeDetailList) {
                    options.add(new LatLng(routeDetail.getLocationLat(), routeDetail.getLocationLon()));
                }

                Polyline line = mMap.get(i).addPolyline(options);
            }
        }
        
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_maps);
        DBHelper dbHelper = new DBHelper(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mRoutes = dbHelper.getRoutes();

        // Set a custom list adapter for a list of locations
        mAdapter = new MapAdapter(this, mRoutes);
        mList = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list);
        mList.setListAdapter(mAdapter);

        // Set a RecyclerListener to clean up MapView from ListView
        AbsListView lv = mList.getListView();
        lv.setRecyclerListener(mRecycleListener);

    }

    private void setMapLocation(GoogleMap map, Route route) {
        DBHelper dbHelper = new DBHelper(this);
        // Add a marker for this item and set the camera
        LatLng center = new LatLng(route.getCenterLat(), route.getCenterLon());

        map.addMarker(new MarkerOptions().position(center));

        List<RouteDetail> routeDetailList = dbHelper.getRouteDetails(route.getEventNo());

        if (routeDetailList != null) {
            // Add a marker in Sydney and move the camera
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (RouteDetail routeDetail : routeDetailList) {
                options.add(new LatLng(routeDetail.getLocationLat(), routeDetail.getLocationLon()));
                builder.include(new LatLng(routeDetail.getLocationLat(), routeDetail.getLocationLon()));
            }
            LatLngBounds bounds = builder.build();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
            map.addPolyline(options);
        }

        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    private class MapAdapter extends ArrayAdapter<Route> {

        private final HashSet<MapView> mMaps = new HashSet<MapView>();

        public MapAdapter(Context context, List<Route> locations) {
            super(context, R.layout.map_lite_list_row, R.id.lite_listrow_text, locations);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;

            // Check if a view can be reused, otherwise inflate a layout and set up the view holder
            if (row == null) {
                // Inflate view from layout file
                row = getLayoutInflater().inflate(R.layout.map_lite_list_row, null);

                // Set up holder and assign it to the View
                holder = new ViewHolder();
                holder.mapView = (MapView) row.findViewById(R.id.lite_listrow_map);
                holder.title = (TextView) row.findViewById(R.id.lite_listrow_text);
                // Set holder as tag for row for more efficient access.
                row.setTag(holder);

                // Initialise the MapView
                holder.initializeMapView();

                // Keep track of MapView
                mMaps.add(holder.mapView);
            } else {
                // View has already been initialised, get its holder
                holder = (ViewHolder) row.getTag();
            }

            // Get the Route for this item and attach it to the MapView
            Route item = getItem(position);
            holder.mapView.setTag(item);

            // Ensure the map has been initialised by the on map ready callback in ViewHolder.
            // If it is not ready yet, it will be initialised with the Route set as its tag
            // when the callback is received.
            if (holder.map != null) {
                // The map is already ready to be used
                setMapLocation(holder.map, item);
            }

            // Set the text label for this item
            holder.title.setText(item.getName());

            return row;
        }

        /**
         * Retuns the set of all initialised {@link MapView} objects.
         *
         * @return All MapViews that have been initialised programmatically by this adapter
         */
        public HashSet<MapView> getMaps() {
            return mMaps;
        }
    }

    class ViewHolder implements OnMapReadyCallback {

        MapView mapView;

        TextView title;

        GoogleMap map;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getApplicationContext());
            map = googleMap;
            Route data = (Route) mapView.getTag();
            if (data != null) {
                setMapLocation(map, data);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }

    }
}
