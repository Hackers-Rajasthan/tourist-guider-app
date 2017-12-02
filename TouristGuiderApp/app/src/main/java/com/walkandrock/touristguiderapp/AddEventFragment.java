package com.walkandrock.touristguiderapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.facebook.AccessToken;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;


public class AddEventFragment extends Fragment {

    public static String username;
    final private int PICK_IMAGE_REQUEST = 1;
    private ImageView eventImage;
    public static JSONObject cityData;

    private EditText titleEditText;
    private EditText aboutEditText;
    private Spinner citySpinner;
    private static int year, month, day, hour, minute, second;
    Bitmap bitmap;

    final AddEventFragment curr = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //fetchData();
        ScrollView scroll = (ScrollView) inflater.inflate(R.layout.add_event, container, false);
        RelativeLayout layout = (RelativeLayout) scroll.findViewById(R.id.add_event_layout);
        titleEditText = (EditText) layout.findViewById(R.id.title);
        aboutEditText = (EditText) layout.findViewById(R.id.description);
        setSpinner(layout);
        setDatePicker(layout);
        setTimePicker(layout);
        setImageUploader(layout);
        setCitySpinner(layout);
        setFinalSubmit(layout);
        return scroll;
    }

    //Function to modify spinner
    private void setSpinner(RelativeLayout layout) {
        Spinner spinner = (Spinner)layout.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.event_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //Function to setup date picker
    private void setDatePicker(RelativeLayout layout) {
        Button dateButton = (Button) layout.findViewById(R.id.date_button);
        final EditText startDate = (EditText)layout.findViewById(R.id.start_date);
        startDate.setEnabled(false);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                AddEventFragment.year = year;
                                AddEventFragment.month = monthOfYear;
                                AddEventFragment.day = dayOfMonth;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();
            }
        });
    }

    //Function to setup time picker
    private void setTimePicker(RelativeLayout layout) {
        Button timeButton = (Button) layout.findViewById(R.id.time_button);
        final EditText startTime = (EditText) layout.findViewById(R.id.start_time);
        startTime.setEnabled(false);
        eventImage = (ImageView) layout.findViewById(R.id.event_image);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                startTime.setText(hourOfDay + ":" + minute);
                                AddEventFragment.hour = hourOfDay;
                                AddEventFragment.minute = minute;
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    //Function to setup image uploader
    private void setImageUploader(RelativeLayout layout) {
        Button imageButton = (Button) layout.findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    //Function to setup City Spinner
    private void setCitySpinner(RelativeLayout layout) {
        citySpinner = (Spinner) layout.findViewById(R.id.position);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        try {
            JSONArray cities = cityData.getJSONArray("Cities");
            for (int i = 0; i < cities.length(); i++)
                spinnerAdapter.add(cities.getJSONObject(i).getString("name"));
        } catch (Exception e) {}
        citySpinner.setAdapter(spinnerAdapter);
    }

    //Function to fetch cities list for given user using thread
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

    //Function to set selected image in image view
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri filePath = null;
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                eventImage.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Function to finally submit the form
    private void setFinalSubmit(RelativeLayout layout) {
        Button submitButton = (Button) layout.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONObject postData = constructPostData();
                //Post this json to server
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL obj = new URL(Constant.SERVER_URL + "/event/add");
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("POST");

                            // For POST only - START
                            con.setDoOutput(true);
                            OutputStream os = con.getOutputStream();
                            os.write(postData.toString().getBytes());
                            int responseCode = con.getResponseCode();
                            Log.d("Login", responseCode+"");
                        } catch (Exception e) { Log.d("Login", e.toString() );}
                    }
                });
                Toast.makeText(getActivity(), "Event Added Successfully", Toast.LENGTH_SHORT).show();
                Fragment f = new CityFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_layout, f);
                ft.remove(curr);
                ft.commit();
            }
        });
    }

    //Function to create json of post data
    private JSONObject constructPostData() {
        String title = titleEditText.getText().toString();
        String about = aboutEditText.getText().toString();
        String city_key = "";
        try {
            int city_index = citySpinner.getSelectedItemPosition();
            JSONArray cities = cityData.getJSONArray("Cities");
            city_key = cities.getJSONObject(city_index).getString("key");
        } catch (Exception e) {}
        String added_by = username;

        JSONObject postData = new JSONObject();
        try {
            postData.put("title", title);
            postData.put("about", about);
            postData.put("city_key", city_key);
            postData.put("added_by", added_by);
            postData.put("year", AddEventFragment.year);
            postData.put("month", AddEventFragment.month);
            postData.put("day", AddEventFragment.day);
            postData.put("hour", AddEventFragment.hour);
            postData.put("minute", AddEventFragment.minute);
            postData.put("main_photo", bitmap);
        } catch (Exception e) {}
        return  postData;
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
                        ft.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        getActivity().setTitle("Add Event");
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
}
