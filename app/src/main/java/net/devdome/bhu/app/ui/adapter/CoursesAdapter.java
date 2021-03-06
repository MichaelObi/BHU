package net.devdome.bhu.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.db.realm.Course;
import net.devdome.bhu.app.ui.activity.CourseDetailActivity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CourseViewHolder> {

    Boolean registered, all, checkable;
    Realm mRealm;
    private List<String> registeredCourses;
    private Context context;
    private List<Course> courses;

    /**
     * Returns an Adapter for a RecyclerView.
     *
     * @param context context
     * @param all     Check if to return get courses or only registered courses
     */
    private CoursesAdapter(Context context, Boolean all, boolean checkable) {
        this.checkable = checkable;
        this.context = context;
        registered = !all; //To show registered courses or get courses
        this.all = all;

        mRealm = Realm.getDefaultInstance();

        registeredCourses = new ArrayList<>(10);

        //Check if to return get courses or only registered courses
        if (all) {
            courses = mRealm.where(Course.class).findAll();
        } else {
            courses = mRealm.where(Course.class).equalTo("registered", true).findAll();
        }
    }

    /**
     * Static method to get Adapter
     *
     * @param context context
     * @param all     Check if to return get courses or only registered courses
     */
    public static CoursesAdapter get(Context context, Boolean all, boolean checkable) {
        return new CoursesAdapter(context, all, checkable);
    }


    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.card_course, parent, false);
        return new CourseViewHolder(v);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRealm.close();
    }

    @Override
    public void onBindViewHolder(final CourseViewHolder holder, int position) {
        if (courses.get(position).isRegistered()) {
            if (holder.btnSubscribe != null) {
                holder.btnSubscribe.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_circle_24dp));
            }
            registeredCourses.add(courses.get(position).getCode());
        }
        holder.tvCourseCode.setText(courses.get(position).getCode());
        holder.tvCourseTitle.setText(courses.get(position).getTitle());
        holder.setShowButton(checkable);
        if (checkable) {
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (registeredCourses.contains(holder.tvCourseCode.getText().toString())) {
                        //Remove Course from list
                        registeredCourses.remove(holder.tvCourseCode.getText().toString());
                        holder.btnSubscribe.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_circle_24dp));
                    } else {
                        registeredCourses.add(holder.tvCourseCode.getText().toString());
                        holder.btnSubscribe.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_circle_24dp));
                    }
                }
            });
        } else {
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, CourseDetailActivity.class);
                    i.putExtra("course_code", holder.tvCourseCode.getText().toString());
                    context.startActivity(i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public List<String> getRegisteredCourses() {
        return this.registeredCourses;
    }


    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseCode, tvCourseTitle;
        View v;
        ImageButton btnSubscribe;

        public CourseViewHolder(View itemView) {
            super(itemView);
            v = itemView;
            tvCourseCode = (TextView) itemView.findViewById(R.id.course_code);
            tvCourseTitle = (TextView) itemView.findViewById(R.id.course_title);
            btnSubscribe = (ImageButton) itemView.findViewById(R.id.btn_add_course);
        }

        public void setShowButton(Boolean showButton) {
            if (showButton) {
                btnSubscribe.setVisibility(View.VISIBLE);
            } else {
                btnSubscribe.setVisibility(View.GONE);
            }
        }
    }
}