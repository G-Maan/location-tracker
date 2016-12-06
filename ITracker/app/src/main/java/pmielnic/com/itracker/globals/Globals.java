package pmielnic.com.itracker.globals;

import android.app.Application;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import pmielnic.com.itracker.R;
import pmielnic.com.itracker.model.User;

/**
 * Created by Pawel on 2016-11-21.
 */
public class Globals extends Application {

    private String urlPrint;
    private String urlSaveLocation;
    private String urlFindUser;
    private String urlAddUser;
    private String urlRemoveUser;
    private String urlListFriends;
    private String urlSaveUser;
    private List<Marker> markerList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();

    public void setUrlPrint(String urlPrint) {
        this.urlPrint = urlPrint;
    }

    public void setUrlSaveLocation(String urlSaveLocation) {
        this.urlSaveLocation = urlSaveLocation;
    }

    public void setUrlFindUser(String urlFindUser) {
        this.urlFindUser = urlFindUser;
    }

    public void setUrlAddUser(String urlAddUser) {
        this.urlAddUser = urlAddUser;
    }

    public void setUrlRemoveUser(String urlRemoveUser) {
        this.urlRemoveUser = urlRemoveUser;
    }

    public void setUrlListFriends(String urlListFriends) {
        this.urlListFriends = urlListFriends;
    }

    public void setUrlSaveUser(String urlSaveUser) {
        this.urlSaveUser = urlSaveUser;
    }

    public String getUrlFindUser() {
        return urlFindUser;
    }

    public String getUrlPrint() {
        return urlPrint;
    }

    public String getUrlSaveLocation() {
        return urlSaveLocation;
    }

    public String getUrlAddUser() {
        return urlAddUser;
    }

    public String getUrlRemoveUser() {
        return urlRemoveUser;
    }

    public String getUrlListFriends() {
        return urlListFriends;
    }

    public String getUrlSaveUser() {
        return urlSaveUser;
    }

    public List<Marker> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(List<Marker> markerList) {
        this.markerList = new ArrayList<>();
        this.markerList = markerList;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = new ArrayList<>();
        this.userList = userList;
    }
}
