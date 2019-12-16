package com.example.newweek3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity; //unused
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private AdView mAdView;

    CampDBHandler MyDB;

    TextView idView;
    EditText nameBox;
    EditText stateBox;
    EditText favBox;
    EditText priceBox;
    EditText addressBox;
    EditText ratingBox;
    Button addbtn;
    Button searchbtn;
    Button deletebtn;
    Button detailbtn;

    private LoginButton loginButton;
    private CircleImageView circleImageView;
    private TextView txtName,txtEmail;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext()); //initialize facebooksdk //deprecated function. Might need to look into this.

        //find the objects from the xml for facebook login api
        loginButton = findViewById(R.id.login_button);
        txtName = findViewById(R.id.profile_name);
        txtEmail = findViewById(R.id.profile_email);
        circleImageView = findViewById(R.id.profile_image);

        callbackManager = CallbackManager.Factory.create(); //to register callback so that the app can communicate with the facebook login.
        loginButton.setReadPermissions(Arrays.asList("email","public_profile")); // another deprecated function.
        // Register the callback function to the facebook loginbutton.
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            //empty functions. Need to be removed
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        MyDB = new CampDBHandler(MainActivity.this, null, null, 1);
        MyDB.getWritableDatabase(); // create database


        idView = (TextView) findViewById(R.id.campID);
        nameBox = (EditText) findViewById(R.id.campName);
        stateBox = (EditText) findViewById(R.id.campState);
        favBox = (EditText) findViewById(R.id.campFav);

        priceBox = (EditText) findViewById(R.id.campPrice);
        addressBox = (EditText) findViewById(R.id.campAddress);
        ratingBox = (EditText) findViewById(R.id.campRating);

        addbtn = (Button) findViewById(R.id.btnA);
        searchbtn = (Button) findViewById(R.id.btnS);
        deletebtn = (Button) findViewById(R.id.btnD);
        detailbtn = (Button) findViewById(R.id.btnDt);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCamp(v);
            }
        });
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCamp(v);
            }
        });
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCamp(v);
            }
        });
        detailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailCamp(v);

                Intent i = new Intent(getApplicationContext(), PopActivity.class);
                startActivity(i);
            }
        });

        // this line finds the google map in the activity_main.xml and assigns it to the variable
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //this line makes the map asynchronous
        mapFragment.getMapAsync(this);

        //initialize Admob API with the given key.
        MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713");
        // find the adView object in the xml and assign it to the variable
        mAdView = findViewById(R.id.adView);
        //Request for the ad.
        AdRequest adRequest = new AdRequest.Builder().build();
        // place the requested ad to the mAdView xml object.
        mAdView.loadAd(adRequest);

        //placed for future use.
        mAdView.setAdListener(new AdListener(){

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        //get everything back to unlogged state when the user logs out of facebook account.
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null)
            {
                txtName.setText("");
                txtEmail.setText("");
                circleImageView.setImageResource(0);
                Toast.makeText(MainActivity.this,"User Logged out",Toast.LENGTH_LONG).show();
            }
            else
                loadUserProfile(currentAccessToken);
        }
    };

    private void loadUserProfile(AccessToken newAccessToken){
        //send request for user information.
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                //get the requested user info and assign the data to respective textbox in the xml.
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/" +id+ "/picture?type=normal"; //get the user facebook photo
                    txtEmail.setText(email);
                    txtName.setText(first_name +" " +last_name);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();

                    Glide.with(MainActivity.this).load(image_url).into(circleImageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name, last_name, email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //this function runs when the map is ready
        mMap = googleMap;

        // Add a marker in a camping site in NH, and move the camera.
        LatLng NHCamping = new LatLng(42.9449126,-71.3009338); //specifies the location // i would like to make this dynamic.
        //i would want this to update the coordinates according to the campsites.
        //adds marker in the map with the title
        mMap.addMarker(new MarkerOptions().position(NHCamping).title("Marker in NHCamping"));
        //move the camera to the location specificed above.
        mMap.moveCamera(CameraUpdateFactory.newLatLng(NHCamping));
    }

    public void addCamp(View view) {
        CampDBHandler dbHandler = new CampDBHandler(this, null, null, 1);
        int fav = Integer.parseInt(favBox.getText().toString());
        int rating = Integer.parseInt(ratingBox.getText().toString());
        Camp camp = new Camp(stateBox.getText().toString(), nameBox.getText().toString(), fav,
                priceBox.getText().toString(), addressBox.getText().toString(),rating );
        dbHandler.addCamp(camp);
        nameBox.setText("");
        stateBox.setText("");
        favBox.setText("");
        priceBox.setText("");
        addressBox.setText("");
        ratingBox.setText("");
    }
    public void searchCamp(View view) {
        CampDBHandler dbHandler = new CampDBHandler(this, null, null, 1);
        Camp camp = dbHandler.searchCamp(nameBox.getText().toString());
        if (camp != null) {
            idView.setText(String.valueOf(camp.getID()));
            stateBox.setText(String.valueOf(camp.getState()));
            favBox.setText(String.valueOf(camp.getFav()));
        } else {
            idView.setText("Camp not found.");
        }
    }
    public void deleteCamp(View view) {
        CampDBHandler dbHandler = new CampDBHandler(this, null, null, 1);
        boolean result = dbHandler.deleteCamp(nameBox.getText().toString());
        if (result)
        {
            idView.setText("Camp Deleted");
            nameBox.setText("");
            stateBox.setText("");
            favBox.setText("");
            priceBox.setText("");
            addressBox.setText("");
            ratingBox.setText("");
        }
        else
            idView.setText("Camp not found.");

    }

    public void detailCamp(View view) {
        CampDBHandler dbHandler = new CampDBHandler(this, null, null, 1);
        Camp camp = dbHandler.searchCamp(nameBox.getText().toString());
        if (camp != null) {
            priceBox.setText(String.valueOf(camp.getPrice()));
            addressBox.setText(String.valueOf(camp.getAddress()));
            ratingBox.setText(String.valueOf(camp.getRating()));
            nameBox.clearFocus();
            //onBackPressed();
        } else {
            idView.setText("Camp not found.");
        }
    }

    //unused function.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // perform your action here

    }
}
