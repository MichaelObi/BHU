package net.devdome.bhu.app.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.db.realm.Course;
import net.devdome.bhu.app.ui.adapter.CourseActivityAdapter;
import net.devdome.bhu.app.ui.components.CustomLinearLayoutManager;

import io.realm.Realm;

public class CourseDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    String courseCode;
    TextView courseTitle;
    Realm realm;
    CourseActivityAdapter adapter;
    RecyclerView lectures;
    LinearLayout nothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        courseTitle = (TextView) findViewById(R.id.course_title);
        lectures = (RecyclerView) findViewById(R.id.course_lectures);
        nothing = (LinearLayout) findViewById(R.id.nothing_to_show);

        courseCode = getIntent().getStringExtra("course_code");
        realm = Realm.getDefaultInstance();
        Course course = realm.where(Course.class).equalTo("code", courseCode).findFirst();
        adapter = CourseActivityAdapter.get(this, course);
        if (adapter.getItemCount() > 0) {
//            nothing.setVisibility(View.GONE);
            CustomLinearLayoutManager layoutManager = new CustomLinearLayoutManager(this);
            layoutManager.setScrollable(true);
            lectures.setLayoutManager(layoutManager);
            lectures.setAdapter(adapter);
            lectures.setVisibility(View.VISIBLE);

        }


        courseTitle.setText(course.getTitle());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(courseCode);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
        }
        return true;
    }
}
