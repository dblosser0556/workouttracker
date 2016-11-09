package com.sighe.workouttracker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.sighe.workouttracker.R;
import com.sighe.workouttracker.data.DBHelper;
import com.sighe.workouttracker.data.WorkOut;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

/**
 * An activity representing a list of WorkOuts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WorkOutDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class WorkOutListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View recyclerView = findViewById(R.id.workout_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.workout_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        DBHelper dbHelper = new DBHelper(this);

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(dbHelper.getWorkOuts()));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<WorkOut> mValues;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
        NumberFormat nf = new DecimalFormat("##.00");

        public SimpleItemRecyclerViewAdapter(List<WorkOut> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.workout_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mStartTime.setText(sdf.format(holder.mItem.getStartDateMilli()));
            holder.mEndTime.setText(sdf.format(holder.mItem.getEndDateMilli()));
            holder.mTotalDistance.setText(nf.format(holder.mItem.getDistance()));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(WorkOutDetailFragment.WORKOUT, holder.mItem);
                        arguments.putInt(SettingsActivity.KEY_DISTANCE_UOM,
                                getIntent().getIntExtra(SettingsActivity.KEY_DISTANCE_UOM, 0));
                        arguments.putInt(SettingsActivity.KEY_SPEED_UOM,
                                getIntent().getIntExtra(SettingsActivity.KEY_SPEED_UOM, 0));
                        WorkOutDetailFragment fragment = new WorkOutDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.workout_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, WorkOutDetailActivity.class);
                        intent.putExtra(WorkOutDetailFragment.WORKOUT, holder.mItem);
                        intent.putExtra(SettingsActivity.KEY_DISTANCE_UOM,
                                getIntent().getIntExtra(SettingsActivity.KEY_DISTANCE_UOM, 0));
                        intent.putExtra(SettingsActivity.KEY_SPEED_UOM,
                                getIntent().getIntExtra(SettingsActivity.KEY_SPEED_UOM, 0));
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mStartTime;
            public final TextView mEndTime;
            public final TextView mTotalDistance;
            public WorkOut mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mStartTime = (TextView) view.findViewById(R.id.tvStartTime);
                mEndTime = (TextView) view.findViewById(R.id.tvEndTime);
                mTotalDistance = (TextView) view.findViewById(R.id.tvDistanceTotal);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mStartTime.toString() + "'";
            }
        }
    }
}
