package net.devdome.bhu.rest.model;

import java.util.List;

public class Post {

    private List<Data> data;

    public List<Data> getData() {
        return data;
    }
    public class Data {
        public int id;
        public String title;
        public String post_content;
        public String post_content_html;
        public String featured_image;
        public String author_id;
        public String author_first_name;
        public String author_last_name;
        public long created_at;

        public long getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(long updated_at) {
            this.updated_at = updated_at;
        }

        public long getCreated_at() {
            return created_at;
        }

        public void setCreated_at(long created_at) {
            this.created_at = created_at;
        }

        public long updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPost_content() {
            return post_content;
        }

        public void setPost_content(String post_content) {
            this.post_content = post_content;
        }

        public String getPost_content_html() {
            return post_content_html;
        }

        public void setPost_content_html(String post_content_html) {
            this.post_content_html = post_content_html;
        }

        public String getFeatured_image() {
            return featured_image;
        }

        public void setFeatured_image(String post_featured_image) {
            this.featured_image = post_featured_image;
        }

        public String getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }

        public String getAuthor_first_name() {
            return author_first_name;
        }

        public void setAuthor_first_name(String author_first_name) {
            this.author_first_name = author_first_name;
        }

        public String getAuthor_last_name() {
            return author_last_name;
        }

        public void setAuthor_last_name(String author_last_name) {
            this.author_last_name = author_last_name;
        }
    }
}
