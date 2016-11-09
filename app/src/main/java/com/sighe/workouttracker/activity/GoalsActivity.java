package com.sighe.workouttracker.activity;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;


import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sighe.workouttracker.R;
import com.sighe.workouttracker.data.DBHelper;
import com.sighe.workouttracker.data.Goal;
import com.sighe.workouttracker.utility.utils;


import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.List;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

public class GoalsActivity extends AppCompatActivity implements GoalDialogFragment.GoalDialogFragmentListener {
    public static final String TAG = "GoalsActivity";

    private int mDistUOMpref;
    private int mSpeedUOMpref;

    private View mRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpEditor(null, R.string.goal_add_goal_title);


            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDistUOMpref = getIntent().getIntExtra(SettingsActivity.KEY_DISTANCE_UOM, 0);
        mSpeedUOMpref = getIntent().getIntExtra(SettingsActivity.KEY_SPEED_UOM, 0);

        mRecyclerview = findViewById(R.id.content_goals);
        assert mRecyclerview != null;
        setupRecyclerView((RecyclerView) mRecyclerview);
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

        recyclerView.setAdapter(new SimpleGoalRecyclerViewAdapter(dbHelper.getGoals()));
    }

    @Override
    public void onFinishEditDialog(Goal goal) {
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.saveOrUpdateGoal(goal);
        setupRecyclerView((RecyclerView) mRecyclerview);
    }

    public void popUpEditor(Goal goal, int title) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(GoalDialogFragment.GOAL, goal);
        arguments.putInt(SettingsActivity.KEY_DISTANCE_UOM,
                getIntent().getIntExtra(SettingsActivity.KEY_DISTANCE_UOM, 0));
        arguments.putInt(SettingsActivity.KEY_SPEED_UOM,
                getIntent().getIntExtra(SettingsActivity.KEY_SPEED_UOM, 0));
        arguments.putInt(GoalDialogFragment.TITLE, title);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        GoalDialogFragment goalDialogFragment = new GoalDialogFragment();

        goalDialogFragment.setArguments(arguments);
        goalDialogFragment.show(ft, "dialog");
    }

    public class SimpleGoalRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleGoalRecyclerViewAdapter.ViewHolder> {

        private final List<Goal> mValues;
        NumberFormat nf = new DecimalFormat("#0.00");

        public SimpleGoalRecyclerViewAdapter(List<Goal> items) {
            mValues = items;
        }

        @Override
        public SimpleGoalRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.goals_list_content, parent, false);
            return new SimpleGoalRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleGoalRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mModeType.setText(holder.mItem.getModeType().toString());
            holder.mValueType.setText(holder.mItem.getValueType().toString());
            holder.mInterval.setText(holder.mItem.getIntervalType().toString());
            holder.mValue.setText(nf.format(holder.mItem.getValue()));
            holder.mUOM.setText(setUOM(holder.mItem.getValueType().value));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popUpEditor(holder.mItem, R.string.goal_update_goal_title);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public String setUOM(int valueType) {
            if (valueType == utils.GoalType.distance.value) {
                return utils.distUOMAbbrv[mDistUOMpref];
            } else if (valueType == utils.GoalType.time.value) {
                return "min";
            } else return "";
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mModeType;
            public final TextView mValueType;
            public final TextView mInterval;
            public final TextView mValue;
            public final TextView mUOM;
            public Goal mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mModeType = (TextView) view.findViewById(R.id.tvGoalModeType);
                mValueType = (TextView) view.findViewById(R.id.tvGoalValueType);
                mInterval = (TextView) view.findViewById(R.id.tvGoalInterval);
                mValue = (TextView) view.findViewById(R.id.tvGoalValue);
                mUOM = (TextView) view.findViewById(R.id.tvGoalValueUOM);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mModeType.toString() + "'";
            }


        }


    }


}
