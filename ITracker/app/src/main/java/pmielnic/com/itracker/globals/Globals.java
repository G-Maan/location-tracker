package pmielnic.com.itracker.globals;

import android.app.Application;

import pmielnic.com.itracker.R;

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
}
