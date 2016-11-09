package com.sighe.workouttracker.utility;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.sighe.workouttracker.R;

/**
 * Created by dad on 11/2/2016.
 */

public class DismissiblePopup extends android.widget.PopupWindow {
    Context context;
    Button btnDismiss;
    EditText etValue;
    View popupView;
    Button btnOK;
    String mValue;


    public DismissiblePopup(Context context, int popup_layout) {
        super(context);

        this.context = context;
        popupView = LayoutInflater.from(context).inflate(popup_layout,
                null);
        setContentView(popupView);
        btnOK = (Button) popupView.findViewById(R.id.btnPopupOK);
        btnDismiss = (Button) popupView.findViewById(R.id.btnPopupDismiss);
        etValue = (EditText) popupView.findViewById(R.id.etValue);

        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        // Closes the popup window when touch outside of it - when looses focus
        setOutsideTouchable(true);
        setFocusable(true);

        // Removes default black background
        setBackgroundDrawable(new BitmapDrawable());
        if (btnDismiss != null)
            btnDismiss.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {

                    dismiss();

                }
            });

        if (btnOK != null)
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etValue != null)
                        mValue = etValue.getText().toString();
                    dismiss();
                }
            });
        // Closes the popup window when touch it
        /*
         * this.setTouchInterceptor(new View.OnTouchListener() {
		 *
		 * @Override public boolean onTouch(View v, MotionEvent event) {
		 *
		 * if (event.getAction() == MotionEvent.ACTION_MOVE) { dismiss(); }
		 * return true; } });
		 */
    } // End constructor

    // Attaches the view to its parent anchor-view at position x and y
    public void show(View anchor, int x, int y) {
        showAtLocation(anchor, Gravity.CENTER, x, y);
    }

    public String getValue() {
        return mValue;
    }
}


