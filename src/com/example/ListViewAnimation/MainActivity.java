package com.example.ListViewAnimation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-24
 * Time: 上午8:42
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setText("跳转");
        button.setOnClickListener(this);
        setContentView(button);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, MyActivity.class);
        startActivity(intent);
    }
}
