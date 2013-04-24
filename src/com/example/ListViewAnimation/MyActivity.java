package com.example.ListViewAnimation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my);

        ViewGroup rootView= (ViewGroup) findViewById(R.id.myRootView);

        ListView listView = new ListView(this);
        listView.setAdapter(new MyListViewAdapter(this, 0, new ArrayList<String>() {
            {
                this.add("hello1");
                this.add("hello2");
                this.add("hello3");
                this.add("hello4");
                this.add("hello5");
                this.add("hello6");
            }
        }));

        listView.setBackgroundColor(Color.WHITE);
        rootView.addView(listView);

        GLSurfaceView surfaceView = new GLSurfaceView(this);
        surfaceView.setEGLContextClientVersion(2);

        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        surfaceView.setZOrderOnTop(true);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);

        surfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                glClearColor(0, 0, 0, 0);
            }

            @Override
            public void onSurfaceChanged(GL10 gl10, int width, int height) {
                glViewport(0, 0, width, height);
            }

            @Override
            public void onDrawFrame(GL10 gl10) {
                glClear(GL_COLOR_BUFFER_BIT);
            }
        });
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        rootView.addView(surfaceView);
    }

    class MyListViewAdapter extends ArrayAdapter<String> {

        public MyListViewAdapter(Context context, int textViewResourceId, List objects) {
            super(context, textViewResourceId, objects);
        }

        private Handler handler = new Handler();

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                view = View.inflate(MyActivity.this, R.layout.item, null);

                ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.itemContent);
                Log.d("listview", "view group: " + viewGroup);
            }

            TextView itemTextView = (TextView) view.findViewById(R.id.itemText);
            itemTextView.setText(getItem(position));

            final View v=view;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewGroup group= (ViewGroup) v.findViewById(R.id.itemContent);
                    View.inflate(MyActivity.this, R.layout.gl, group);
//                    PageAnimationLayout pageAnimationLayout= (PageAnimationLayout) View.inflate(MyActivity.this, R.layout.gl, group);
                    PageAnimationLayout pageAnimationLayout = (PageAnimationLayout) v.findViewById(R.id.targetViewGroup);
                    pageAnimationLayout.start(100, 400);
                }
            }, 0);


            return view;
        }
    }
}
