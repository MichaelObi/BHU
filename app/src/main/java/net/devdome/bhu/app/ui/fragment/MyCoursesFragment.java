package net.devdome.bhu.app.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.ui.activity.BaseActivity;
import net.devdome.bhu.app.ui.activity.MyCoursesActivity;
import net.devdome.bhu.app.ui.adapter.CoursesAdapter;
import net.devdome.bhu.app.ui.components.DividerItemDecoration;

import io.realm.RealmChangeListener;

public class MyCoursesFragment extends Fragment implements RealmChangeListener {
    View view;
    RecyclerView rvCourses;
    CoursesAdapter coursesAdapter;
    ProgressBar progressBar;
    Context context;
    LinearLayout emptyLayout, courseListLayout;
    Button btnAddCourses;
    ViewGroup parent;

    public MyCoursesFragment() {
    }

    public static MyCoursesFragment getInstance() {
        return new MyCoursesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        parent = container;
        view = inflater.inflate(R.layout.fragment_my_courses, container, false);
        setHasOptionsMenu(true);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_courses);
        coursesAdapter = CoursesAdapter.get(context, false, false);
        btnAddCourses = (Button) view.findViewById(R.id.btn_add_courses);
        rvCourses = (RecyclerView) view.findViewById(R.id.rv_courses);
        rvCourses.setLayoutManager(new LinearLayoutManager(context));
        rvCourses.setAdapter(coursesAdapter);
        if (coursesAdapter.getItemCount() < 1) {
            emptyLayout = (LinearLayout) view.findViewById(R.id.layout_empty);
            emptyLayout.setVisibility(View.VISIBLE);
            courseListLayout = (LinearLayout) view.findViewById(R.id.layout_course_list);
            courseListLayout.setVisibility(View.GONE);
            btnAddCourses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyCoursesActivity) getActivity()).loadFragment(AddCoursesFragment.getInstance(), parent.getId(), false);
                }
            });
        }
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("My Courses");

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_my_courses, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_courses_edit:
                ((MyCoursesActivity) getActivity()).loadFragment(AddCoursesFragment.getInstance(), parent.getId(), false);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onChange() {
        coursesAdapter.notifyDataSetChanged();
    }
}
