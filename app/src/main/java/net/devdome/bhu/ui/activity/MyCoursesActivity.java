package net.devdome.bhu.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import net.devdome.bhu.R;
import net.devdome.bhu.ui.adapter.CoursesAdapter;
import net.devdome.bhu.ui.fragment.MyCoursesFragment;

public class MyCoursesActivity extends BaseActivity {

    ProgressBar progressBar;
    RecyclerView rvCourses;
    CoursesAdapter coursesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        Slide enterSlide = new Slide();
        enterSlide.setSlideEdge(Gravity.START);
        getWindow().setEnterTransition(enterSlide);

        Slide returnSlide = new Slide();
        returnSlide.setSlideEdge(Gravity.START);
        getWindow().setReturnTransition(returnSlide);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Fragment fragment = MyCoursesFragment.getInstance();
        fragment.setHasOptionsMenu(true);
        //Load Fragment
        loadFragment(fragment, R.id.courses_container, false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_courses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }


}
