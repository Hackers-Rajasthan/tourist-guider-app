package com.walkandrock.touristguiderapp;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


public class CityFragment extends Fragment {

    private static JSONObject cityData;
    private static JSONObject userData;
    final CityFragment curr = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(cityData == null || userData==null) {
            fetchData();
            RelativeLayout layout = new RelativeLayout(getActivity());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            layout.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            ProgressBar bar = new ProgressBar(getActivity());
            bar.setIndeterminate(true);
            bar.setLayoutParams(params);
            layout.addView(bar);
            return layout;

        }
        ScrollView scroll = getScrollView();

        RelativeLayout layout = getRelativeLayout();
        scroll.addView(layout);

        int n_cities = 0;
        try {
            n_cities = cityData.getJSONArray("Cities").length();
        } catch (Exception e) {
        }
        for (int i = 0; i < n_cities; i++) {
            RelativeLayout city = getCityLayout(i);
            layout.addView(city);

            ImageButton image = getImageButton(i);
            addCityListener(image, i);
            city.addView(image);

            TextView name = getCityNameTextView(i);
            city.addView(name);

            TextView subText = getSubscribeTextView();
            city.addView(subText);

            Switch subSwitch = getSubSwitch(i);
            addSwitchListener(subSwitch, i);
            city.addView(subSwitch);
        }


        return scroll;
    }

    //Function to fetch cities list for given user using thread (Fills up cityData and userData)
    private void fetchData() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL obj = new URL(Constant.SERVER_URL + "/user/" + AccessToken.getCurrentAccessToken().getUserId() + "/cities");
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    cityData = new JSONObject(readAll(in));
                    AddEventFragment.cityData = cityData;
                    Log.d("Login", responseCode + " is response code");
                } catch (Exception e) {
                    Log.d("Login", e.toString() + " in City List");
                }

                try {
                    URL obj = new URL(Constant.SERVER_URL + "/user/" + AccessToken.getCurrentAccessToken().getUserId());
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    userData = new JSONObject(readAll(in));
                    Log.d("Login", responseCode + " is response code");
                } catch (Exception e) {
                    Log.d("Login", e.toString() + " in user List");
                }

                Fragment fc = new CityFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_layout, fc);
                ft.remove(curr);
                ft.commit();
            }
        });
        t.start();
    }

    //Function to create outer main ScrollView
    private ScrollView getScrollView() {
        ScrollView scroll = new ScrollView(getActivity());
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(240, 240, 240));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        scroll.setLayoutParams(params);
        return scroll;
    }

    //Function to create the RelativeLayout to be direct child of ScrollView
    private RelativeLayout getRelativeLayout() {
        RelativeLayout layout = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        layout.setLayoutParams(params);
        return layout;
    }

    //Function to get RelativeLayout for a city
    private RelativeLayout getCityLayout(int i) {
        RelativeLayout city = new RelativeLayout(getActivity());
        city.setPadding(0, 0, 0, 0);
        city.setElevation(6);
        city.setBackgroundColor(Color.WHITE);
        city.setId(i + 1);
        RelativeLayout.LayoutParams cityParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        if (i != 0)
            cityParams.addRule(RelativeLayout.BELOW, i);
        int n_cities = 0;
        try {
            n_cities = cityData.getJSONArray("Cities").length();
        } catch (Exception e) {
        }
        if (i == 0)
            cityParams.setMargins(20, 40, 20, 20);
        else if (i == n_cities - 1)
            cityParams.setMargins(20, 20, 20, 40);
        else
            cityParams.setMargins(20, 20, 20, 20);
        city.setLayoutParams(cityParams);
        return city;
    }

    //Function to create imageButton for city
    private ImageButton getImageButton(int i) {
        ImageButton image = new ImageButton(getActivity());
        image.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        image.setLayoutParams(imageParams);
        String url = "";
        try {
            url = cityData.getJSONArray("Cities").getJSONObject(i).getString("main_photo_url");
        } catch (Exception e) {
        }
        Picasso.with(getActivity())
                .load(url)
                .into(image);
        image.setAdjustViewBounds(true);
        image.setPadding(0, 0, 0, 0);
        imageParams.setMargins(0, 0, 0, 0);
        image.setId(1);
        return image;
    }

    //Function to add event listener to the image of city
    private void addCityListener(ImageButton image, final int i) {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new AttractionFragment();
                String city_id = "";
                try {
                    city_id = cityData.getJSONArray("Cities").getJSONObject(i).getString("key");
                } catch (Exception e) {
                }
                ((AttractionFragment) f).city_id = city_id;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_layout, f);
                ft.remove(curr);
                ft.commit();
            }
        });
    }

    //Function to get TextView of city name
    private TextView getCityNameTextView(int i) {
        TextView name = new TextView(getActivity());
        String city_name = "";
        try {
            city_name = cityData.getJSONArray("Cities").getJSONObject(i).getString("name");
        } catch (Exception e) {
        }
        name.setText(city_name);
        name.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        name.setLayoutParams(nameParams);
        name.setTextSize(22);
        name.setTypeface(null, Typeface.BOLD);
        nameParams.setMargins(30, 30, 0, 0);
        return name;
    }

    //Function to get subscribe text view
    private TextView getSubscribeTextView() {
        TextView subText = new TextView(getActivity());
        subText.setText("Subscribe");
        subText.setTextColor(Color.rgb(100, 100, 100));
        RelativeLayout.LayoutParams subTextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        subText.setLayoutParams(subTextParams);
        subTextParams.addRule(RelativeLayout.BELOW, 1);
        subTextParams.setMargins(20, 20, 0, 20);
        return subText;
    }

    //Function to create subscription button
    private Switch getSubSwitch(int i) {
        Switch subSwitch = new Switch(getActivity());
        Boolean isSubscribed = false;
        String city_id = "";
        try {
            city_id = cityData.getJSONArray("Cities").getJSONObject(i).getString("key");
        } catch (Exception e) {
        }
        JSONArray sub_list = null;
        try {
            sub_list = userData.getJSONArray("subscription_list");
        } catch (Exception e) {
        }
        for (int j = 0; j < sub_list.length(); j++) {
            String id = "";
            try {
                id = sub_list.getString(j);
            } catch (Exception e) {
            }
            if (id.equals(city_id))
                isSubscribed = true;
        }
        subSwitch.setChecked(isSubscribed);
        RelativeLayout.LayoutParams subSwitchParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        subSwitch.setLayoutParams(subSwitchParams);
        subSwitchParams.addRule(RelativeLayout.BELOW, 1);
        subSwitchParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        subSwitchParams.setMargins(0, 20, 20, 20);
        return subSwitch;
    }

    //Function to add listener to subscription switch
    private void addSwitchListener(final Switch s, final int i) {
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String city_id = "";
                        try {
                            city_id = cityData.getJSONArray("Cities").getJSONObject(i).getString("key");
                        } catch (Exception e) {
                        }
                        try {
                            URL obj = new URL(Constant.SERVER_URL + "/user/" + AccessToken.getCurrentAccessToken().getUserId() + "/" + city_id + "/subscribe");
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("GET");
                            int responseCode = con.getResponseCode();
                            Log.d("Login", responseCode + " is response code");
                        } catch (Exception e) {
                            Log.d("Login", e.toString() + " in toggle");
                        }

                    }
                }).start();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Cities");
    }

    private static String readAll(Reader rd) {
        StringBuilder sb = null;
        try {
            sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }
}
