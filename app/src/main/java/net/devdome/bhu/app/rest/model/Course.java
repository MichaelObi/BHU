package net.devdome.bhu.app.rest.model;

import java.util.List;

public class Course {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public class Data {
        String title;
        String code;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
