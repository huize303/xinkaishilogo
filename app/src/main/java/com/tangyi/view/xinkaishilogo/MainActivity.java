package com.tangyi.view.xinkaishilogo;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    boolean mOnCreate = true;
    LogoProgressView mLogoProgressView;

    private LogoProgressView.EndInterface mNext = new EndListener() {
        @Override
        public void end() {
            super.end();
        }
    };

    @Override
    protected void onPause() {
        if (mOnCreate) {
            mLogoProgressView.stopAnimation();
        }
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mLogoProgressView = (LogoProgressView) findViewById(R.id.logo_progress_view);
        mLogoProgressView.setZOrderOnTop(true);
        mLogoProgressView.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mOnCreate) {
            // show animation when first time to create this
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mLogoProgressView.startAnimation(mNext);
                }
            });
        } else {
            mNext.end();
        }
    }

    private class EndListener implements LogoProgressView.EndInterface {

        @Override
        public void end() {
            mOnCreate = false;
        }
    }
}
