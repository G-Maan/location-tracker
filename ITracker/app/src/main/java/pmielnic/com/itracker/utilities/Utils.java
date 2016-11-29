package pmielnic.com.itracker.utilities;

import org.json.JSONException;
import org.json.JSONObject;

import pmielnic.com.itracker.model.Address;
import pmielnic.com.itracker.model.Location;
import pmielnic.com.itracker.model.User;

/**
 * Created by Pawel Mielniczuk on 2016-11-21.
 */
public class Utils {

    public static User parseJsonToUser(JSONObject obj) throws JSONException {
        User user = new User();
        user.setId(obj.getLong("id"));
        user.setName(obj.getString("name"));
        user.setEmail(obj.getString("email"));

        Location location = new Location();
        JSONObject locationObject = obj.getJSONObject("location");
        location.setLongitude(locationObject.getDouble("longitude"));
        location.setLatitude(locationObject.getDouble("latitude"));

        Address address = new Address();
        JSONObject addressObject = locationObject.getJSONObject("address");
        address.setCountry(addressObject.getString("country"));
        address.setCity(addressObject.getString("city"));
        address.setStreetName(addressObject.getString("streetName"));

        location.setAddress(address);
        user.setLocation(location);

        return user;
    }

}
