package barman.utpal.voicerecorder;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListActivity.this, MainActivity.class));
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(new myAdapter(this));

    }


    // Class for a single row
    class singleRow {
        String title;
        String duration;
        singleRow(String title, String duration){
            this.title = title;
            this.duration = duration;

        }
    }


    // Custom Adapter
    class myAdapter extends BaseAdapter {

        ArrayList<singleRow> list;
        Context c;
        myAdapter(Context context){
            c = context;
            list = new ArrayList<singleRow>();
            Resources resources = context.getResources();
            String[] title = resources.getStringArray(R.array.title);
            String[] duration = resources.getStringArray(R.array.description);

            for(int i = 0; i < 6; i++){
                list.add(new singleRow(title[i], duration[i]));
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.list_single_row, parent, false);

            TextView title = (TextView) row.findViewById(R.id.textView_title);
            TextView duration = (TextView) row.findViewById(R.id.textView_duration);

            singleRow temp = list.get(position);
            title.setText(temp.title);
            duration.setText(temp.duration);

            return row;
        }
    }

}
