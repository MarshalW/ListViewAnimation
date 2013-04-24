package com.example.ListViewAnimation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-2-19
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class PageAnimationView extends GLSurfaceView {
    //动画时长500ms
    private long duration = 1000;
    //延时2000ms执行
    private long startDelay = 1000;
    //动画定时对象
    private ValueAnimator animator;

    private CurlRenderer renderer;

    private PageAnimationLayout.AnimationEndCallback animationEndCallback;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            PageAnimationView.this.setVisibility(View.INVISIBLE);
        }
    };

    private Bitmap bitmap;

    public void setAnimationEndCallback(PageAnimationLayout.AnimationEndCallback animationEndCallback) {
        this.animationEndCallback = animationEndCallback;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStartDelay(long startDelay) {
        this.startDelay = startDelay;
    }

    public PageAnimationView(Context context) {
        super(context);
        this.init();
    }

    public PageAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        //使用OpenGL ES 2.0
        this.setEGLContextClientVersion(2);

        //设置透明背景
        this.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        this.setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.renderer = new CurlRenderer();
        this.setRenderer(this.renderer);
        this.renderer.getMesh().setFlipTexture(false);

        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        this.renderer.setMargins(.01f, .01f, .01f, .01f);

        animator = ValueAnimator.ofFloat(1f, -1f);
        animator.setDuration(this.duration);
        animator.setStartDelay(startDelay);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                renderer.setPositionFactor((Float)valueAnimator.getAnimatedValue());
                requestRender();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ViewGroup viewGroup=(ViewGroup)getParent();

                if(viewGroup!=null){
                    View view=viewGroup.findViewById(R.id.targetView);
                    view.setAlpha(1);
                    handler.sendMessageDelayed(new Message(),0);

                    if (animationEndCallback!=null){
                        animationEndCallback.callback();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.renderer.getMesh().getTexturePage().setTexture(bitmap);
    }

    public void start(){
        animator.start();
    }
}
