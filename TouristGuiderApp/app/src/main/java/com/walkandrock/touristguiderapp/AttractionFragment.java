package com.walkandrock.touristguiderapp;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import org.w3c.dom.Attr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


public class AttractionFragment extends Fragment {

    public String city_id;
    private JSONObject attData;
    final AttractionFragment curr = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fetchData();
        ScrollView scroll = getScrollView();

        RelativeLayout layout = getRelativeLayout();
        scroll.addView(layout);

        for(int curr_item=0, curr_ctg=0 ; curr_ctg<3 ; ) {
            if(curr_item == 0) {
                if(getNoItemsCtg(curr_ctg) != 0) {
                    TextView title = getCategoryTitleTextView(curr_ctg);
                    layout.addView(title);
                }
            }
            if(getNoItemsCtg(curr_ctg) == curr_item) {
                Log.d("Login", "here:"+getNoItemsCtg(curr_ctg) );
                curr_item = 0;
                curr_ctg++;
                continue;
            }

            RelativeLayout item = getItemLayout(curr_item, curr_ctg);
            layout.addView(item);

            ImageView image = getItemImage(curr_ctg, curr_item);
            item.addView(image);

            TextView name = getItemName(curr_ctg, curr_item);
            item.addView(name);

            RatingBar rating = getItemRating(curr_ctg, curr_item);
            item.addView(rating);

            TextView desc = getItemDesc(curr_ctg, curr_item);
            item.addView(desc);

            TextView time = getItemTiming(curr_ctg, curr_item);
            item.addView(time);

            TextView addedBy = getAddedBy(curr_ctg, curr_item);
            item.addView(addedBy);

            curr_item++;
        }
        return scroll;
    }

    //Function to fetch data of the city from server
    private void fetchData() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL obj = new URL(Constant.SERVER_URL + "/user/" + city_id + "/events");
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    attData = new JSONObject(readAll(in));
                    Log.d("Login", responseCode + " is response code");
                } catch (Exception e) {
                    Log.d("Login", e.toString() + " in City List");
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {}
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

    //Function to get text view of title of category
    private TextView getCategoryTitleTextView(int curr_ctg) {
        TextView title = new TextView(getActivity());
        String ctg_title = getCategoryTitle(curr_ctg);
        title.setText(ctg_title.substring(0, 1).toUpperCase() + ctg_title.substring(1));
        title.setTextColor(Color.rgb(100, 100, 100));
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        title.setId(1000*(curr_ctg+1));
        if(curr_ctg != 0)
            titleParams.addRule(RelativeLayout.BELOW, getNoItemsCtg(curr_ctg-1)+1000*curr_ctg);
        title.setLayoutParams(titleParams);
        title.setTextSize(18);
        titleParams.setMargins(20, 20, 0, 0);
        return title;
    }

    //Function to get title of category from index
    private String getCategoryTitle(int ctg) {
        if(ctg == 0)
            return "arts";
        if(ctg == 1)
            return "events";
        return "exhibitions";
    }

    //Function to get total no of items in a category
    private int getNoItemsCtg(int ctg) {
        int nItems = 0;
        try {
            JSONObject ctgObj = attData.getJSONObject(getCategoryTitle(ctg));
            nItems = ctgObj.getJSONArray("live").length() + ctgObj.getJSONArray("upcoming").length();
        } catch (Exception e) { Log.d("Login", "getNitems: " + e.toString());}
        return nItems;
    }

    //Function to get Layout for a single item
    private RelativeLayout getItemLayout(int curr_item, int curr_ctg) {
        RelativeLayout item = new RelativeLayout(getActivity());
        item.setPadding(0, 0, 0, 0);
        item.setElevation(6);
        item.setBackgroundColor(Color.WHITE);
        item.setId(curr_item + 1000*(curr_ctg+1) + 1);
        RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        itemParams.addRule(RelativeLayout.BELOW, curr_item + 1000*(curr_ctg+1));
        item.setLayoutParams(itemParams);
        itemParams.setMargins(20, 10, 20, 10);
        return item;
    }

    //Function to get the Item image
    private ImageView getItemImage(int ctg, int item) {
        ImageView image = new ImageView(getActivity());
        image.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        image.setLayoutParams(imageParams);
        String url=""; try {url = getJSONObjectOfItem(ctg, item).getString("main_photo_url"); } catch(Exception e) {}
        Log.d("Login", "url: " + url);
        Picasso.with(getActivity())
                .load(url)
                .resize(200, 200)
                .into(image);
        imageParams.setMargins(0, 0, 20, 0);
        image.setId(1);
        return image;
    }

    //Function to get JSONObject of Item
    private JSONObject getJSONObjectOfItem(int ctg, int item) {
        try {
            JSONObject category = attData.getJSONObject(getCategoryTitle(ctg));
            if (item < category.getJSONArray("live").length())
                return category.getJSONArray("live").getJSONObject(item);
            else
                return category.getJSONArray("upcoming").getJSONObject(item - category.getJSONArray("live").length());
        } catch (Exception e) {}
        return null;
    }

    //Function to get the text view of item name
    private TextView getItemName(int ctg, int item) {
        TextView name = new TextView(getActivity());
        String item_name=""; try { item_name = getJSONObjectOfItem(ctg, item).getString("title"); } catch (Exception e) {}
        Log.d("Login", "name: " + name);
        name.setText(item_name);
        name.setTextColor(Color.BLACK);
        RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        name.setLayoutParams(nameParams);
        name.setTextSize(15);
        name.setId(2);
        nameParams.setMargins(0, 12, 0, 12);
        nameParams.addRule(RelativeLayout.RIGHT_OF, 1);
        return name;
    }

    //Function to get rating bar of item
    private RatingBar getItemRating(int ctg, int item) {
        RatingBar rating = new RatingBar(getActivity(), null, android.R.attr.ratingBarStyleSmall);
        rating.setId(3);
        double rate=0; try {rate = getJSONObjectOfItem(ctg, item).getDouble("rating");} catch (Exception e) {}
        rating.setRating((float)rate);
        rating.setNumStars(5);
        rating.setStepSize(1);
        RelativeLayout.LayoutParams ratingParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        ratingParams.setMargins(0, 12, 20, 0);
        ratingParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        ratingParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rating.setLayoutParams(ratingParams);
        return rating;
    }

    //Function to get Description text view of item
    private TextView getItemDesc(int ctg, int item) {
        TextView desc = new TextView(getActivity());
        desc.setId(4);
        String desc_text=""; try { desc_text = getJSONObjectOfItem(ctg, item).getString("about");} catch (Exception e) {}
        if(desc.length() > 59)
            desc.setText(desc_text.substring(0, 60) + "...");
        else desc.setText(desc_text);
        RelativeLayout.LayoutParams descParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        descParams.addRule(RelativeLayout.RIGHT_OF, 1);
        descParams.addRule(RelativeLayout.BELOW, 2);
        desc.setLayoutParams(descParams);
        return desc;
    }

    //Function to get the timing of item
    private TextView getItemTiming(int ctg, int item) {
        TextView timing = new TextView(getActivity());
        timing.setId(5);
        RelativeLayout.LayoutParams timingParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        timing.setLayoutParams(timingParams);
        timingParams.addRule(RelativeLayout.RIGHT_OF, 1);
        timingParams.addRule(RelativeLayout.BELOW, 4);
        timingParams.setMargins(0, 18, 0, 0);
        try {
            JSONObject category = attData.getJSONObject(getCategoryTitle(ctg));
            if (item < category.getJSONArray("live").length()) {
                timing.setText("LIVE");
                timing.setTextColor(Color.RED);
                timing.setTypeface(null, Typeface.BOLD);
            }
            else {
                String st_time = getJSONObjectOfItem(ctg, item).getString("start_time");
                timing.setText(st_time);
            }
        } catch (Exception e) {}
        timing.setTextSize(10);
        timing.setTypeface(null, Typeface.ITALIC);
        return timing;
    }

    //Function to get addedBy textView
    private TextView getAddedBy(int ctg, int item) {
        TextView addedBy = new TextView(getActivity());
        addedBy.setId(6);
        addedBy.setTextColor(Color.rgb(100, 100, 100));
        String value=""; try { value = getJSONObjectOfItem(ctg, item).getString("added_by");} catch (Exception e) {}
        if(value.length() > 10)
            addedBy.setText("Added By: " + value.substring(0, 9));
        else
            addedBy.setText("Added By: " + value);
        RelativeLayout.LayoutParams addedbyParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        addedBy.setLayoutParams(addedbyParams);
        addedbyParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addedbyParams.addRule(RelativeLayout.BELOW, 4);
        addedbyParams.setMargins(0, 18, 20, 0);
        addedBy.setTextSize(10);
        addedBy.setTypeface(null, Typeface.ITALIC);
        return addedBy;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Fragment f = new CityFragment();
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.main_layout, f);
                        ft.remove(curr);
                        ft.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        getActivity().setTitle("Magical Attractions");
        super.onViewCreated(view, savedInstanceState);
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

        private void setDummyData() {
            try {
                attData = new JSONObject("{\n" +
                        "\t\"user_id\":\"\",\n" +
                        "\t\"arts\":\n" +
                        "\t{\n" +
                        "\t\t\"live\":\n" +
                        "\t\t[\n" +
                        "\t\t\t{\n" +
                        "\t\t\t\t\"id\":\"id1\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4 //rating of event\n" +
                        "\t\t\t},\n" +
                        "\t\t\t{\n" +
                        "\t\t\t\t\"id\":\"id2\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4 //rating of event\n" +
                        "\t\t\t}\n" +
                        "\t\t],\n" +
                        "\t\t\"upcoming\":\n" +
                        "\t\t[\n" +
                        "\t\t\t{\n" +
                        "\t\t\t\t\"id\":\"id1\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4, //rating of event\n" +
                        "                \"start_time\": \"24 Dec 8:80PM\",\n" +
                        "                \"end_time\": \"24 Dec 9:00PM\"\n" +
                        "\t\t\t},\n" +
                        "            {\n" +
                        "\t\t\t\t\"id\":\"id1\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4, //rating of event\n" +
                        "                \"start_time\": \"24 Dec 8:80PM\",\n" +
                        "                \"end_time\": \"24 Dec 9:00PM\"\n" +
                        "\t\t\t}\n" +
                        "\t\t]\n" +
                        "\t},\n" +
                        "\t\"events\":\n" +
                        "\t{\n" +
                        "\t\t\"live\":[{\n" +
                        "\t\t\t\t\"id\":\"id1\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4 //rating of event\n" +
                        "\t\t\t},\n" +
                        "\t\t\t{\n" +
                        "\t\t\t\t\"id\":\"id2\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4 //rating of event\n" +
                        "\t\t\t}],\n" +
                        "\t\t\"upcoming\":[\n" +
                        "            {\n" +
                        "\t\t\t\t\"id\":\"id1\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4, //rating of event\n" +
                        "                \"start_time\": \"24 Dec 8:80PM\",\n" +
                        "                \"end_time\": \"24 Dec 9:00PM\"\n" +
                        "\t\t\t},\n" +
                        "            {\n" +
                        "\t\t\t\t\"id\":\"id1\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4, //rating of event\n" +
                        "                \"start_time\": \"24 Dec 8:80PM\",\n" +
                        "                \"end_time\": \"24 Dec 9:00PM\"\n" +
                        "\t\t\t}]\n" +
                        "\t},\n" +
                        "\t\"exhibitions\":\n" +
                        "\t{\n" +
                        "\t\t\"live\":[{\n" +
                        "\t\t\t\t\"id\":\"id1\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4 //rating of event\n" +
                        "\t\t\t},\n" +
                        "\t\t\t{\n" +
                        "\t\t\t\t\"id\":\"id2\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4 //rating of event\n" +
                        "\t\t\t}],\n" +
                        "\t\t\"upcoming\":[{\n" +
                        "\t\t\t\t\"id\":\"id1\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4, //rating of event\n" +
                        "                \"start_time\": \"24 Dec 8:80PM\",\n" +
                        "                \"end_time\": \"24 Dec 9:00PM\"\n" +
                        "\t\t\t},\n" +
                        "            {\n" +
                        "\t\t\t\t\"id\":\"id1\", //use system generated//so that you can use it for redirection_to main_page_of event\n" +
                        "\t\t\t\t\"main_photo_url\":\"https://953dbb3e023d8d2081dc-a6ac47d7e9972b6bed5824eadfd0b772.ssl.cf3.rackcdn.com/wp-content/uploads/2017/10/93431-004-910B716E.jpg\",\n" +
                        "\t\t\t\t\"title\":\"Check\",\n" +
                        "\t\t\t\t\"about\":\"This is the most occupying event ever organized in jaoipur. Go and attend this event.\",\n" +
                        "\t\t\t\t\"locationid\":\"\", //will use locationid provided by google map\n" +
                        "\t\t\t\t\"city\":\"\",\n" +
                        "\t\t\t\t\"distance\":0.2, //this will change according to user(in central system it will be 0)\n" +
                        "\t\t\t\t\"seen_by\":200, //count\n" +
                        "\t\t\t\t\"posted_on\":\"24 Nov 17\",\n" +
                        "\t\t\t\t\"added_by\":\"Gupta ji\",\n" +
                        "\t\t\t\t\"rating\":3.4, //rating of event\n" +
                        "                \"start_time\": \"24 Dec 8:80PM\",\n" +
                        "                \"end_time\": \"24 Dec 9:00PM\"\n" +
                        "\t\t\t}]\n" +
                        "\t}\n" +
                        "}");
                Log.d("Login", attData.getJSONObject("arts").getJSONArray("live").length()+"");
            } catch (Exception e) {
                Log.d("Login", "occupy");}
    }
}

