package net.devdome.bhu.app.rest.model;

public class Venue {

    public class Data {
        int venue_id;
        int capacity;
        String name;

        public int getVenue_id() {
            return venue_id;
        }

        public int getCapacity() {
            return capacity;
        }

        public String getName() {
            return name;
        }
    }
}
