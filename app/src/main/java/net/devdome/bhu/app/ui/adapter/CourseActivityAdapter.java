package net.devdome.bhu.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.devdome.bhu.app.R;
import net.devdome.bhu.app.db.realm.Course;
import net.devdome.bhu.app.db.realm.CurricEvent;
import net.devdome.bhu.app.ui.activity.CourseDetailActivity;
import net.devdome.bhu.app.utility.TimeUtils;

import java.util.List;

import io.realm.Realm;

public class CourseActivityAdapter extends RecyclerView.Adapter<CourseActivityAdapter.ViewHolder> {

    private Context context;
    private Realm realm;
    private List<CurricEvent> curricEvents;

    private CourseActivityAdapter(Context context, Course course) {
        this.context = context;
        realm = Realm.getDefaultInstance();
        curricEvents = realm.where(CurricEvent.class).equalTo("course.code", course.getCode()).findAll();
    }

    public static CourseActivityAdapter get(CourseDetailActivity courseDetailActivity, Course course) {
        return new CourseActivityAdapter(courseDetailActivity, course);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_course_activity, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tvTime.setText(curricEvents.get(position).getStarts_at());
        holder.tvVenue.setText(curricEvents.get(position).getVenue());
        String day = curricEvents.get(position).getDay().toLowerCase();
        holder.tvDay.setText(String.format("%s%s", day.substring(0, 1).toUpperCase(), day.substring(1)));
        holder.tvDuration.setText(TimeUtils.minutesToReadableDuration(curricEvents.get(position).getDuration()));
    }

    @Override
    public int getItemCount() {
        return curricEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvVenue, tvDay, tvDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvVenue = (TextView) itemView.findViewById(R.id.tv_venue);
            tvDay = (TextView) itemView.findViewById(R.id.tv_day);
            tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
        }
    }
}
