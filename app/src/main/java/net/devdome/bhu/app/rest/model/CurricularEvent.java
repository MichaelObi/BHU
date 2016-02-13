package net.devdome.bhu.app.rest.model;

import java.util.List;

public class CurricularEvent {
    List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }


    public class Data {
        int id;
        long duration;
        String day, type, starts_at;
        Course.Data course;
        Venue.Data venue;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStarts_at() {
            return starts_at;
        }

        public void setStarts_at(String starts_at) {
            this.starts_at = starts_at;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Course.Data getCourse() {
            return course;
        }

        public void setCourse(Course.Data course) {
            this.course = course;
        }

        public Venue.Data getVenue() {
            return venue;
        }

        public void setVenue(Venue.Data venue) {
            this.venue = venue;
        }
    }
}
