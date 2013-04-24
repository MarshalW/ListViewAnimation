package com.example.ListViewAnimation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.main);

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

        setContentView(listView);
    }

    class MyListViewAdapter extends ArrayAdapter<String> {

        public MyListViewAdapter(Context context, int textViewResourceId, List objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                view = View.inflate(MyActivity.this, R.layout.item, null);

                ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.itemContent);
                viewGroup.addView(new MyGLView(MyActivity.this));
            }

            TextView itemTextView = (TextView) view.findViewById(R.id.itemText);
            itemTextView.setText(getItem(position));

            return view;
        }
    }
}
