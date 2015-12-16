package net.devdome.bhu.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.devdome.bhu.Config;
import net.devdome.bhu.R;
import net.devdome.bhu.rest.RestClient;
import net.devdome.bhu.rest.model.Course;
import net.devdome.bhu.rest.service.ApiService;
import net.devdome.bhu.ui.activity.BaseActivity;
import net.devdome.bhu.ui.activity.MyCoursesActivity;
import net.devdome.bhu.ui.adapter.CoursesAdapter;
import net.devdome.bhu.utility.NetworkUtilities;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddCoursesFragment extends Fragment {
    View view;
    RecyclerView rvCourses;
    CoursesAdapter coursesAdapter;
    ProgressBar progressBar;
    Context context;
    ViewGroup mContainer;

    public AddCoursesFragment() {
    }

    public static AddCoursesFragment getInstance() {
        return new AddCoursesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        mContainer = container;
        view = inflater.inflate(R.layout.fragment_all_courses, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_courses);
        coursesAdapter = CoursesAdapter.get(context, true, true);
        rvCourses = (RecyclerView) view.findViewById(R.id.rv_courses);
        rvCourses.setLayoutManager(new LinearLayoutManager(context));
        rvCourses.setAdapter(coursesAdapter);
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("Manage Courses");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_courses, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_courses_submit) {
            submitCourses();
        } else if (item.getItemId() == R.id.item_courses_refresh) {
            if (!NetworkUtilities.isNetworkEnabled(getActivity())) {
                Toast.makeText(getActivity(), "Cannot establish internet connection.", Toast.LENGTH_SHORT).show();
            }
            getCourses();
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitCourses() {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setMessage("Submitting your courses");
        dialog.show();
        ApiService apiService = new RestClient().getApiService();
        apiService.registerCourses(((BaseActivity) getActivity()).getAuthToken(), coursesAdapter.getRegisteredCourses(), new Callback<Course>() {
                    @Override
                    public void success(final Course course, Response response) {
                        new AsyncTask<Course, Void, Void>() {
                            @Override
                            protected Void doInBackground(Course... params) {
                                final List<Course.Data> data = params[0].getData();
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                RealmResults<net.devdome.bhu.db.realm.Course> courses = realm.where(net.devdome.bhu.db.realm.Course.class).findAll();
                                for (int i = 0; i < courses.size(); i++) {
                                    courses.get(i).setRegistered(false);
                                }
                                realm.commitTransaction();
                                for (int i = 0; i < data.size(); i++) {
                                    net.devdome.bhu.db.realm.Course mCourse = new net.devdome.bhu.db.realm.Course();
                                    mCourse.setCode(data.get(i).getCode());
                                    mCourse.setTitle(data.get(i).getTitle());
                                    mCourse.setRegistered(true);
                                    realm.beginTransaction();
                                    realm.copyToRealmOrUpdate(mCourse);
                                    realm.commitTransaction();
                                }
                                realm.close();
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                            }
                        }.execute(course);
                        dialog.dismiss();
                        ((MyCoursesActivity) getActivity()).loadFragment(MyCoursesFragment.getInstance(), mContainer.getId(), false);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        Toast.makeText(context, "An error occurred. Try again later", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        );
    }


    public void getCourses() {
        progressBar.setVisibility(View.VISIBLE);
        rvCourses.setVisibility(View.GONE);
        String authCode = ((BaseActivity) getActivity()).getAuthToken();
        if (authCode == null) {
            Toast.makeText(getActivity(), "An Authentication Error Occurred", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService apiService = new RestClient().getApiService();
        apiService.getAllCourses(authCode, new Callback<Course>() {
            @Override
            public void success(Course courses, Response response) {
                Log.i(Config.TAG, response.getBody().toString());
                new AsyncTask<Course, Void, Boolean>() {
                    @Override
                    protected void onPostExecute(Boolean completed) {
                        super.onPostExecute(completed);
                        progressBar.setVisibility(View.GONE);
                        coursesAdapter.notifyDataSetChanged();
                        rvCourses.setVisibility(View.VISIBLE);
                        if (completed) {
                            Toast.makeText(getActivity(), "Done Loading courses", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to load courses", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected Boolean doInBackground(Course... params) {
                        Course courses = params[0];
                        List<Course.Data> data = courses.getData();
                        Realm realm = Realm.getDefaultInstance();
                        for (Course.Data course : data) {
                            try {
                                net.devdome.bhu.db.realm.Course rmCourse = new net.devdome.bhu.db.realm.Course();
                                rmCourse.setCode(course.getCode());
                                rmCourse.setTitle(course.getTitle());
                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(rmCourse);
                                realm.commitTransaction();
                            } catch (Exception e) {
                                Log.e(Config.TAG, e.getMessage());
                                return false;
                            }
                        }
                        realm.close();

                        return true;
                    }
                }.execute(courses);


            }

            @Override
            public void failure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
