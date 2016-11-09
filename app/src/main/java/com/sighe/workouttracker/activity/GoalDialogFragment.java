package com.sighe.workouttracker.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sighe.workouttracker.R;
import com.sighe.workouttracker.data.Goal;
import com.sighe.workouttracker.utility.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by dad on 10/31/2016.
 */

public class GoalDialogFragment extends DialogFragment implements
        AdapterView.OnItemSelectedListener, View.OnClickListener, View.OnKeyListener {
    private static final int PLEASE_SELECT = 0;
    public static String GOAL = "goal";
    public static String TITLE = "title";
    protected Button mBtnSave;
    protected Button mBtnCancel;
    protected TextView mUOMTextView;
    protected EditText mETGoalValue;
    private Goal mGoal;
    private int mUOMDist;
    private int mUOMSpeed;
    private boolean mIsModeSelected = false;
    private boolean mIsIntervalSelected = false;
    private boolean mIsGoalTypeSelected = false;
    private int mSelectedModeType = 0;
    private int mSelectedInterval = 0;
    private int mSelectedGoalType = 0;
    private double mGoalValue = 0;
    private int mTitle;
    private GoalDialogFragmentListener listener;


    public GoalDialogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(GOAL)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mGoal = getArguments().getParcelable(GOAL);
            mUOMDist = getArguments().getInt(SettingsActivity.KEY_DISTANCE_UOM);
            mUOMSpeed = getArguments().getInt(SettingsActivity.KEY_SPEED_UOM);
            mTitle = getArguments().getInt(TITLE);
            if (mGoal != null) {
                mIsModeSelected = mIsGoalTypeSelected = mIsIntervalSelected = true;
                mSelectedModeType = mGoal.getModeType().value + 1;
                mSelectedInterval = mGoal.getIntervalType().value + 1;
                mSelectedGoalType = mGoal.getValueType().value + 1;
                mGoalValue = mGoal.getValue();

            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_goals_list, container);

        setUpUI(view);
        getDialog().setTitle(mTitle);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        listener = (GoalDialogFragmentListener) getActivity();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (GoalDialogFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement GoalDialogFragmentListner");
        }
    }

    private void setUpUI(View view) {
        Spinner mModeSelector;
        Spinner mValueTypeSelector;
        Spinner mIntervalSelector;

        NumberFormat nf = new DecimalFormat("##.00");

        mETGoalValue = (EditText) view.findViewById(R.id.etSetGoalValue);
        mETGoalValue.setText(nf.format(mGoalValue));
        mETGoalValue.setOnKeyListener(this);

        mUOMTextView = (TextView) view.findViewById(R.id.tvSetGoalValueUOM);
        mUOMTextView.setText(getUOM());

        mModeSelector = (Spinner) view.findViewById(R.id.spSetGoalModeType);
        String[] modeList = getActivity().getResources().getStringArray(R.array.goal_modes_array);
        ArrayAdapter<String> modes = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item,
                modeList);
        mModeSelector.setAdapter(modes);
        mModeSelector.setSelection(mSelectedModeType);
        mModeSelector.setOnItemSelectedListener(this);


        mValueTypeSelector = (Spinner) view.findViewById(R.id.spSetGoalValueType);
        ArrayAdapter<String> valueTypes = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.goal_value_types_array));
        mValueTypeSelector.setAdapter(valueTypes);
        mValueTypeSelector.setSelection(mSelectedGoalType);
        mValueTypeSelector.setOnItemSelectedListener(this);


        mIntervalSelector = (Spinner) view.findViewById(R.id.spSetGoalInterval);
        ArrayAdapter<String> intervalTypes = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.goal_intevals_array));
        mIntervalSelector.setAdapter(intervalTypes);
        mIntervalSelector.setSelection(mSelectedInterval);
        mIntervalSelector.setOnItemSelectedListener(this);

        mBtnSave = (Button) view.findViewById(R.id.btnSaveGoal);
        mBtnCancel = (Button) view.findViewById(R.id.btnCancelGoal);


        mBtnSave.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mBtnSave.setEnabled(canSave());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int itemId = parent.getId();
        boolean isSelected = false;
        isSelected = position != PLEASE_SELECT;
        switch (itemId) {
            case R.id.spSetGoalModeType:
                mIsModeSelected = isSelected;
                mSelectedModeType = position;
                break;
            case R.id.spSetGoalInterval:
                mIsIntervalSelected = isSelected;
                mSelectedInterval = position;
                break;
            case R.id.spSetGoalValueType:
                mIsGoalTypeSelected = isSelected;
                mSelectedGoalType = position;
                mUOMTextView.setText(getUOM());
                break;

        }

        mBtnSave.setEnabled(canSave());

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();

        if (itemId == R.id.btnSaveGoal) {
            if (mGoal == null) {
                mGoal = new Goal(0, mSelectedModeType - 1, mSelectedGoalType - 1,
                        mSelectedInterval - 1, Double.parseDouble(mETGoalValue.getText().toString()),
                        utils.getMidNight(new GregorianCalendar(TimeZone.getDefault()).getTimeInMillis(), false));
            } else {
                mGoal.setIntervalType(mSelectedInterval - 1);
                mGoal.setModeType(mSelectedModeType - 1);
                mGoal.setValueType(mSelectedGoalType - 1);
                mGoal.setValue(Double.parseDouble(mETGoalValue.getText().toString()));
            }

            listener.onFinishEditDialog(mGoal);
        }
        dismiss();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        mBtnSave.setEnabled(canSave());
        return false;
    }

    private boolean canSave() {
        return mIsGoalTypeSelected && mIsIntervalSelected && mIsModeSelected &&
                (Double.parseDouble(mETGoalValue.getText().toString()) > 0.0D);
    }

    private String getUOM() {
        switch (mSelectedGoalType) {
            case 0: //not selected
                return "";
            case 1:  //distance
                return utils.distUOMAbbrv[mUOMDist];
            case 2:  //time
                return "mins";
            case 3: //steps
                return "";
        }
        return null;
    }

    public interface GoalDialogFragmentListener {
        void onFinishEditDialog(Goal goal);
    }
}
