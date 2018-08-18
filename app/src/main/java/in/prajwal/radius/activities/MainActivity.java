package in.prajwal.radius.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.prajwal.radius.R;
import in.prajwal.radius.adapters.MoreOptionsAdapter;
import in.prajwal.radius.adapters.PropertyAdapter;
import in.prajwal.radius.model.Facility;
import in.prajwal.radius.model.FacilityOption;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout moreInfoRel, subContent, aboutMe;
    private RecyclerView propertyRecycler, noOfRoomsRecycler, moreOptionRecycler;
    private PropertyAdapter adapterProperty;
    private MoreOptionsAdapter adapterRooms, adapterOther;
    private List<FacilityOption> propertyList = new ArrayList<>();
    private List<FacilityOption> roomsList = new ArrayList<>();
    private List<FacilityOption> otherList = new ArrayList<>();
    private RequestQueue requestQueue;
    private JSONArray facilityArray, exclusionArray;
    private JSONObject propertyObject, roomObject, otherObject;
    private TextView iAmLookingFor;
    private ProgressBar progressBar;
    private ImageView backTint;
    private Button searchButton, myApps;
    private String responseUrl = "https://my-json-server.typicode.com/iranjith4/ad-assignment/db";
    private boolean isMoreOptionOpen = false;
    private Map<Integer, Integer> exclusionMap = new HashMap<>();
    public static int SELECTED_ROOM = -1, SELECTED_OTHER = -1, SELECTED_PROPERTY = -1;
    public int propertyKey, roomKey, otherKey;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        propertyRecycler = findViewById(R.id.propertyRecycler);
        iAmLookingFor = findViewById(R.id.iAmLookingFor);
        progressBar = findViewById(R.id.progressBar);
        moreInfoRel = findViewById(R.id.moreInfoRel);
        subContent = findViewById(R.id.subContent);
        noOfRoomsRecycler = findViewById(R.id.noOfRoomsRecycler);
        moreOptionRecycler = findViewById(R.id.moreOptionRecycler);
        backTint = findViewById(R.id.backTint);
        searchButton = findViewById(R.id.searchButton);
        aboutMe = findViewById(R.id.aboutMe);
        myApps = findViewById(R.id.myApps);
        adapterProperty = new PropertyAdapter(this, propertyList);
        adapterRooms = new MoreOptionsAdapter(this, roomsList, "rooms");
        adapterOther = new MoreOptionsAdapter(this, otherList, "other");
        final LinearLayoutManager propertyLayoutManager = new LinearLayoutManager(this);
        final LinearLayoutManager roomsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        final LinearLayoutManager otherLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        requestQueue = Volley.newRequestQueue(this);
        realm = Realm.getInstance(this);

        propertyRecycler.setLayoutManager(propertyLayoutManager);
        propertyRecycler.setAdapter(adapterProperty);

        noOfRoomsRecycler.setLayoutManager(roomsLayoutManager);
        noOfRoomsRecycler.setAdapter(adapterRooms);

        moreOptionRecycler.setLayoutManager(otherLayoutManager);
        moreOptionRecycler.setAdapter(adapterOther);

        realm.beginTransaction();
        RealmResults<Facility> responseFromDB = realm.where(Facility.class).equalTo("id", 0).findAll();
        if (responseFromDB.size() == 0) {
            getResponse(responseUrl);
        } else {
            Facility facility = responseFromDB.get(0);
            Calendar calendar = Calendar.getInstance();
            Date todayDate = calendar.getTime();
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String todayDateAsString = dateFormat.format(todayDate);
            if(todayDateAsString.equals(facility.getReloadDate())){ // checking whether today's date matches with that in DB, If yes then the data is reloaded.
                getResponse(responseUrl);
            }else {
                processResponse(facility.getJsonResponse());
            }
        }
        realm.cancelTransaction();

        backTint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMoreOptionOpen) {
                    subContent.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_bottom));
                    backTint.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moreInfoRel.setVisibility(View.GONE);
                        }
                    }, 500);
                    isMoreOptionOpen = false;
                    SELECTED_OTHER = -1;
                    SELECTED_ROOM = -1;
                    SELECTED_PROPERTY = -1;
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exclusionMap.containsKey(propertyKey)) {
                    int getPropertyExclusion = exclusionMap.get(propertyKey);
                    if (getPropertyExclusion == roomKey) {
                        if (propertyKey == 4) {
                            Toast.makeText(MainActivity.this, getString(R.string.land_cannot_have_rooms), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    if (getPropertyExclusion == otherKey) {
                        if (propertyKey == 3) {
                            Toast.makeText(MainActivity.this, R.string.boat_cannot_have_garage, Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
                if (exclusionMap.containsKey(roomKey)) {
                    int getRoomExclusion = exclusionMap.get(roomKey);
                    if (getRoomExclusion == otherKey) {
                        if (roomKey == 7) {
                            Toast.makeText(MainActivity.this, R.string.cannot_select_no_rooms_and_garage_together, Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
                Toast.makeText(MainActivity.this, R.string.search_request_successfully_recorded, Toast.LENGTH_LONG).show();
            }
        });

        myApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=7762059515051912810"));
                startActivity(browserIntent);
            }
        });
    }

    private void getResponse(String Url) { //Getting the response from the API using Volley
        progressBar.setVisibility(View.VISIBLE);
        iAmLookingFor.setVisibility(View.GONE);
        aboutMe.setVisibility(View.GONE);
        StringRequest jsObjRequest = new StringRequest(Request.Method.GET, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                processResponse(response);
                realm.beginTransaction();
                RealmResults<Facility> responseFromDB = realm.where(Facility.class).equalTo("id", 0).findAll();
                if (responseFromDB.size() != 0) {
                    responseFromDB.get(0).removeFromRealm();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date reloadDate = calendar.getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String reloadDateAsString = dateFormat.format(reloadDate); // Getting next day's date to reload the data
                Facility facility = realm.createObject(Facility.class);
                facility.setId(0);
                facility.setJsonResponse(response);
                facility.setReloadDate(reloadDateAsString);
                realm.commitTransaction();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, R.string.unable_to_load, Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsObjRequest);
    }

    private void prepareExclusion() {
        for (int i = 0; i < exclusionArray.length(); i++) {
            try {
                JSONArray subArray = exclusionArray.getJSONArray(i);
                exclusionMap.put(subArray.getJSONObject(0).getInt("options_id"), subArray.getJSONObject(1).getInt("options_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareProperties() {
        adapterProperty.clearAdapter();
        try {
            JSONArray propertiesAvailable = propertyObject.getJSONArray("options");
            for (int i = 0; i < propertiesAvailable.length(); i++) {
                FacilityOption facilityOption = new FacilityOption(propertiesAvailable.getJSONObject(i).getInt("id"),
                        propertiesAvailable.getJSONObject(i).getString("name"),
                        propertiesAvailable.getJSONObject(i).getString("icon"));
                propertyList.add(facilityOption);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapterProperty.notifyDataSetChanged();
    }

    public void openMoreInfo(int optionId) {
        isMoreOptionOpen = true;
        moreInfoRel.setVisibility(View.VISIBLE);
        subContent.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_bottom));
        backTint.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
        propertyKey = optionId;

        prepareRoomsInfo();
        prepareOtherInfo();
    }

    private void prepareRoomsInfo() {
        adapterRooms.clearAdapter();
        try {
            JSONArray roomsInfo = roomObject.getJSONArray("options");
            for (int i = 0; i < roomsInfo.length(); i++) {
                FacilityOption facilityOption = new FacilityOption(roomsInfo.getJSONObject(i).getInt("id"),
                        roomsInfo.getJSONObject(i).getString("name"),
                        roomsInfo.getJSONObject(i).getString("icon"));
                roomsList.add(facilityOption);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapterRooms.notifyDataSetChanged();
    }

    private void prepareOtherInfo() {
        adapterOther.clearAdapter();
        try {
            JSONArray otherInfo = otherObject.getJSONArray("options");
            for (int i = 0; i < otherInfo.length(); i++) {
                FacilityOption facilityOption = new FacilityOption(otherInfo.getJSONObject(i).getInt("id"),
                        otherInfo.getJSONObject(i).getString("name"),
                        otherInfo.getJSONObject(i).getString("icon"));
                otherList.add(facilityOption);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapterOther.notifyDataSetChanged();
    }

    private void processResponse(String response) {
        try {
            iAmLookingFor.setVisibility(View.VISIBLE);
            aboutMe.setVisibility(View.VISIBLE);
            JSONObject mainObject = new JSONObject(response);
            facilityArray = mainObject.getJSONArray("facilities");
            propertyObject = facilityArray.getJSONObject(0);
            prepareProperties();
            roomObject = facilityArray.getJSONObject(1);
            otherObject = facilityArray.getJSONObject(2);
            exclusionArray = mainObject.getJSONArray("exclusions");
            prepareExclusion();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        SELECTED_OTHER = -1;
        SELECTED_ROOM = -1;
        SELECTED_PROPERTY = -1;
        if (isMoreOptionOpen) { //This is to check if the bottom slider is open or not
            subContent.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_bottom));
            backTint.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    moreInfoRel.setVisibility(View.GONE);
                }
            }, 500);
            isMoreOptionOpen = false;
        } else {
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
