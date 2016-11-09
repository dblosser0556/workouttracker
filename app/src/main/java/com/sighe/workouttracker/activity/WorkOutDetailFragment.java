package com.sighe.workouttracker.activity;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sighe.workouttracker.R;
import com.sighe.workouttracker.data.DBHelper;
import com.sighe.workouttracker.data.RouteDetail;
import com.sighe.workouttracker.data.WorkOut;
import com.sighe.workouttracker.utility.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import static com.sighe.workouttracker.utility.utils.getFormattedInterval;

/**
 * A fragment representing a single WorkOut detail screen.
 * This fragment is either contained in a {@link WorkOutListActivity}
 * in two-pane mode (on tablets) or a {@link WorkOutDetailActivity}
 * on handsets.
 */
public class WorkOutDetailFragment extends Fragment {
    public static final String WORKOUT = "workOut";

    private WorkOut mItem;
    private int mUOMDist;
    private int mUOMSpeed;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkOutDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(WORKOUT)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = getArguments().getParcelable(WORKOUT);
            mUOMDist = getArguments().getInt(SettingsActivity.KEY_DISTANCE_UOM);
            mUOMSpeed = getArguments().getInt(SettingsActivity.KEY_SPEED_UOM);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getStartDate());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.workout_detail, container, false);

        setUpUI(rootView);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.workout_detail_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        setupRecyclerView(recyclerView);
        return rootView;
    }

    private void setUpUI(View rootView) {
        NumberFormat numberFormat = new DecimalFormat("##0.00");

        TextView startTime = (TextView) rootView.findViewById(R.id.tvHisStartTime);
        startTime.setText(getString(R.string.start_time, mItem.getStartDate()));

        TextView endTime = (TextView) rootView.findViewById(R.id.tvHisEndTime);
        endTime.setText(getString(R.string.end_time, mItem.getEndDate()));

        TextView elapseTime = (TextView) rootView.findViewById(R.id.tvHisElapseTime);
        elapseTime.setText(getString(R.string.elapsed_time, getFormattedInterval(mItem.getElapseTime())));

        TextView stepsTotal = (TextView) rootView.findViewById(R.id.tvHisStepTotal);
        stepsTotal.setText(getString(R.string.step_total, numberFormat.format(mItem.getSteps())));

        TextView distTotal = (TextView) rootView.findViewById(R.id.tvHisDistanceTotal);

        distTotal.setText(
                getResources().getString(R.string.total_distance,
                        utils.distUOMAbbrv[mUOMDist],
                        numberFormat.format(mItem.getDistance() * utils.distUOMConv[mUOMDist])));

        TextView speed = (TextView) rootView.findViewById(R.id.tvHisAverageSpeed);

        speed.setText(getResources().getString(R.string.avg_speed, utils.speedUOMabbr[mUOMSpeed], numberFormat.format(
                (mUOMSpeed == 3)
                        ? utils.speedUOMconv[mUOMSpeed] / mItem.getAveSpeed()
                        : mItem.getAveSpeed() * utils.speedUOMconv[mUOMSpeed])));

        TextView stepsMin = (TextView) rootView.findViewById(R.id.tvHisAverageStepsPerMinute);
        stepsMin.setText(getString(R.string.avg_steps_min,
                numberFormat.format(mItem.getAveStepsPerMin())));

    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        DBHelper dbHelper = new DBHelper(getContext());

        recyclerView.setAdapter(new RouteDetailRecyclerViewAdapter(dbHelper.getRouteDetails(mItem.getEventNo())));
    }

    public class RouteDetailRecyclerViewAdapter
            extends RecyclerView.Adapter<RouteDetailRecyclerViewAdapter.ViewHolder> {

        private final List<RouteDetail> mValues;

        public RouteDetailRecyclerViewAdapter(List<RouteDetail> items) {
            mValues = items;
        }

        @Override
        public RouteDetailRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.workout_route_detail_content, parent, false);
            return new RouteDetailRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RouteDetailRecyclerViewAdapter.ViewHolder holder, int position) {
            NumberFormat locFormat = new DecimalFormat("##0.000000");
            NumberFormat numberFormat = new DecimalFormat("##0.00");
            holder.mItem = mValues.get(position);
            holder.mLat.setText(getString(R.string.locLat, locFormat.format(holder.mItem.getLocationLat())));
            holder.mLong.setText(getString(R.string.locLong, locFormat.format(holder.mItem.getLocationLon())));
            holder.mDist.setText(getString(R.string.incr_dist, numberFormat.format(holder.mItem.getDistIncr())));
            holder.mElapseTime.setText(getString(R.string.incr_time, getFormattedInterval(holder.mItem.getTimeIncr())));
            holder.mSteps.setText(getString(R.string.incr_steps, numberFormat.format(holder.mItem.getSteps())));


        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mLat;
            final TextView mLong;
            final TextView mDist;
            final TextView mElapseTime;
            final TextView mSteps;
            RouteDetail mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mLat = (TextView) view.findViewById(R.id.tvLat);
                mLong = (TextView) view.findViewById(R.id.tvLong);
                mDist = (TextView) view.findViewById(R.id.tvDist);
                mElapseTime = (TextView) view.findViewById(R.id.tvElapseTime);
                mSteps = (TextView) view.findViewById(R.id.tvSteps);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mItem.toString() + "'";
            }
        }
    }
}
