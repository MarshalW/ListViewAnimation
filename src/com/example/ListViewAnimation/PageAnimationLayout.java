package com.example.ListViewAnimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-2-21
 * Time: 上午10:33
 * To change this template use File | Settings | File Templates.
 */
public class PageAnimationLayout extends FrameLayout {

    boolean started;

    public PageAnimationLayout(Context context) {
        super(context);
        this.init();
    }

    public PageAnimationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public PageAnimationLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    private void init() {
    }

    private Handler handler = new Handler();

    public void start(final long startDelay, final long duration) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                _start(startDelay - 100, duration);
            }
        }, 100);
    }

    private void _start(long startDelay, long duration) {
        if (!started) {
            View targetView = findViewById(R.id.targetView);

            PageAnimationView animationView = new PageAnimationView(this.getContext());
            animationView.setStartDelay(startDelay);
            animationView.setDuration(duration);

//            long time = System.currentTimeMillis();
//            Bitmap bitmap = loadBitmapFromView(targetView);
            targetView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(targetView.getDrawingCache());
            targetView.setDrawingCacheEnabled(false);

//            Log.d("pagedemo", ">>>>>>>>>>>>time: " + (System.currentTimeMillis() - time));
            animationView.setBitmap(bitmap);
            this.addView(animationView);

            if (targetView instanceof AnimationEndCallback) {
                animationView.setAnimationEndCallback((AnimationEndCallback) targetView);
            }

            animationView.start();
            this.started = true;
        }

    }

//    public static Bitmap loadBitmapFromView(View v) {
//        View p = (View) v.getParent();
//        Bitmap b = Bitmap.createBitmap(p.getLayoutParams().width, p.getLayoutParams().height, Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(b);
//        v.measure(
//                MeasureSpec.makeMeasureSpec(p.getLayoutParams().width, MeasureSpec.EXACTLY),
//                MeasureSpec.makeMeasureSpec(p.getLayoutParams().height, MeasureSpec.EXACTLY));
//        v.layout(0, 0, p.getLayoutParams().width, p.getLayoutParams().height);
//        v.draw(c);
//        return b;
//    }


//    public static void saveBitmap(Bitmap bitmap, String fileName) {
//        String filePath = Environment.getExternalStorageDirectory() + "/" + fileName;
//        try {
//            OutputStream stream = new FileOutputStream(filePath);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
//            stream.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public interface AnimationEndCallback {
        void callback();
    }
}


