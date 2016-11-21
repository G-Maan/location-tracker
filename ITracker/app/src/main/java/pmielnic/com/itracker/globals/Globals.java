package pmielnic.com.itracker.globals;

import android.app.Application;

import pmielnic.com.itracker.R;

/**
 * Created by Pawel on 2016-11-21.
 */
public class Globals extends Application {

    private String urlPrint = this.getResources().getString(R.string.url_base) + this.getResources().getString(R.string.url_print_user);
    private String urlSaveLocation = getResources().getString(R.string.url_base) + getResources().getString(R.string.url_save_location);
    private String urlFindUser = getResources().getString(R.string.url_base) + getResources().getString(R.string.url_find_user);
    private String urlAddUser = getResources().getString(R.string.url_base) + getResources().getString(R.string.url_add_friend);
    private String urlRemoveUser = getResources().getString(R.string.url_base) + getResources().getString(R.string.url_remove_friend);
    private String urlListFriends = getResources().getString(R.string.url_base) + getResources().getString(R.string.url_list_friends);
    private String urlSaveUser = getResources().getString(R.string.url_base) + getResources().getString(R.string.url_save_user);

    @Override
    public void onCreate() {
        super.onCreate();
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
