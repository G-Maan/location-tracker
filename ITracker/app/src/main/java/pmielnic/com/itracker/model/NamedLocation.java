package pmielnic.com.itracker.model;

/**
 * Created by Pawel Mielniczuk on 2016-11-29.
 */

import com.google.android.gms.maps.model.LatLng;

/**
 * Location represented by a position ({@link com.google.android.gms.maps.model.LatLng} and a
 * name ({@link java.lang.String}).
 */
public class NamedLocation {

    public final String name;

    public final String streetName;

    public final LatLng location;

    public NamedLocation(String name, String streetName, LatLng location) {
        this.name = name;
        this.streetName = streetName;
        this.location = location;
    }

    @Override
    public String toString() {
        return "NamedLocation{" +
                "name='" + name + '\'' +
                ", location=" + location +
                '}';
    }
}
