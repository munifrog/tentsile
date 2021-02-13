package com.munifrog.design.tetheredtenttriangulator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    private final Clearing mClearing = new Clearing();

    private int mCanvasLeft = 0;
    private int mCanvasTop = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner models = findViewById(R.id.sp_models);
        String [] array = getResources().getStringArray(R.array.tent_models);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.text_spinner,
                array
        );
        arrayAdapter.setDropDownViewResource(R.layout.text_spinner);
        models.setAdapter(arrayAdapter);

        ImageButton knownConfigurations = findViewById(R.id.im_tents);
        knownConfigurations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClearing.nextConfiguration();
            }
        });

        ImageView iv_clearing = findViewById(R.id.iv_clearing);
        iv_clearing.setImageDrawable(mClearing);
        iv_clearing.setContentDescription(getResources().getString(R.string.desc_clearing));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ImageView iv_clearing = findViewById(R.id.iv_clearing);
        int [] viewLoc = new int[2];
        iv_clearing.getLocationOnScreen(viewLoc);
        mCanvasLeft = viewLoc[0];
        mCanvasTop = viewLoc[1];
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x, y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getX();
                y = (int) event.getY();
                mClearing.selectTether(x - mCanvasLeft,y - mCanvasTop);
                break;
            case MotionEvent.ACTION_UP:
                x = (int) event.getX();
                y = (int) event.getY();
                mClearing.releaseTether();
                break;
            case MotionEvent.ACTION_MOVE:
                x = (int) event.getX();
                y = (int) event.getY();
                mClearing.updateTether(x - mCanvasLeft, y - mCanvasTop);
                break;
        }
        return super.onTouchEvent(event);
    }
}