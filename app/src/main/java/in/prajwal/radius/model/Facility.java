package in.prajwal.radius.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by prajwalgs on 18/08/18.
 */

public class Facility extends RealmObject {

    @PrimaryKey
    private int id;
    private String jsonResponse, reloadDate;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getJsonResponse() {
        return jsonResponse;
    }

    public void setJsonResponse(final String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    public String getReloadDate() {
        return reloadDate;
    }

    public void setReloadDate(final String reloadDate) {
        this.reloadDate = reloadDate;
    }
}
