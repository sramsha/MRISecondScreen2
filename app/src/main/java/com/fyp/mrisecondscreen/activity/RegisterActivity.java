package com.fyp.mrisecondscreen.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.fyp.mrisecondscreen.R;
import com.fyp.mrisecondscreen.network.RegisterRequest;
import com.fyp.mrisecondscreen.utils.SessionManagement;
import com.fyp.mrisecondscreen.utils.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    SessionManagement session;
    User user;

    /* Birthday picker */
    private EditText register_age;
    private int mYear;
    private int mMonth;
    private int mDay;
    static final int DATE_DIALOG_ID = 0;
    /* Birthday picker */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final EditText register_name = findViewById(R.id.register_name);
        final EditText register_username = findViewById(R.id.register_username);
        register_age = findViewById(R.id.register_age);
        final EditText register_email = findViewById(R.id.register_email);
        final EditText register_password = findViewById(R.id.register_password);
        final RadioButton register_male = findViewById(R.id.register_male);
        final RadioButton register_female = findViewById(R.id.register_female);
        final Button register_button = findViewById(R.id.register_button);
        user = new User(getApplicationContext());
        session = new SessionManagement(getApplicationContext());

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = register_name.getText().toString();
                final String username = register_username.getText().toString();
                final String age = register_age.getText().toString();
                final String email = register_email.getText().toString();
                final String password = register_password.getText().toString();
                final String gender = register_male.isChecked() ? "male" : "female";
                final String mac = getMacAddress();
                //final String imei = getIMEINumber();

                Response.Listener<String> responseListener = new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonResponse;

                        String serverReply = null;
                        try {
                            jsonResponse = new JSONObject(response);
                            serverReply = jsonResponse.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (Objects.equals(serverReply, "USER_REGISTERED")) {
                            Toast.makeText(getApplicationContext(), "Registered Successfully!", Toast.LENGTH_LONG).show();

                            user.updateSession();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            RegisterActivity.this.startActivity(intent);
                            RegisterActivity.this.finish();
                        } else if (Objects.equals(serverReply, "USERNAME_NOT_AVAILABLE")) {
                            Toast.makeText(getApplicationContext(), "Please choose a different username as it is not available", Toast.LENGTH_LONG).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setMessage("Registration failed due to unknown reasons")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                            Toast.makeText(getApplicationContext(), "Server Response: " + serverReply, Toast.LENGTH_LONG).show();
                        }

                    }
                };

                if (Objects.equals(name, "")) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Name!", Toast.LENGTH_SHORT).show();
                    register_name.setFocusable(true);
                    register_name.requestFocus();
                } else if (Objects.equals(username, "")) {
                    Toast.makeText(RegisterActivity.this, "Please enter your desirable Username!", Toast.LENGTH_SHORT).show();
                    register_username.setFocusable(true);
                    register_username.requestFocus();

                } else if (Objects.equals(age, "")) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Birthday!", Toast.LENGTH_SHORT).show();
                    register_age.setFocusable(true);
                    register_age.requestFocus();
                } else if (Objects.equals(email, "") || !isEmailValid(email)) {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid Email Address!", Toast.LENGTH_SHORT).show();
                    register_email.setFocusable(true);
                    register_email.requestFocus();
                } else if (Objects.equals(password, "")) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Password!", Toast.LENGTH_SHORT).show();
                    register_password.setFocusable(true);
                    register_password.requestFocus();
                } else if (!(register_male.isChecked() || register_female.isChecked())) {
                    Toast.makeText(RegisterActivity.this, "Please select your Gender!", Toast.LENGTH_SHORT).show();
                    register_male.setFocusable(true);
                    register_male.requestFocus();
                    register_female.setFocusable(true);
                    register_female.requestFocus();
                }
                /*else
                {
                    int ageValidated = validateAge(age);

                    if (ageValidated == 1)
                    {
                        Toast.makeText(RegisterActivity.this, "Incomplete Birth date (required: MM/DD/YYYY)", Toast.LENGTH_LONG).show();
                        register_age.setFocusable(true);
                        register_age.requestFocus();
                    }
                    else if (ageValidated == 2)
                    {
                        Toast.makeText(RegisterActivity.this, "Invalid birthday format (use MM/DD/YYYY)", Toast.LENGTH_LONG).show();
                        register_age.setFocusable(true);
                        register_age.requestFocus();
                    }
                    else if (ageValidated == 3)
                    {
                        Toast.makeText(RegisterActivity.this, "Incorrect value of the Month (MM/DD/YYYY)", Toast.LENGTH_LONG).show();
                        register_age.setFocusable(true);
                        register_age.requestFocus();
                    }
                    else if (ageValidated == 4)
                    {
                        Toast.makeText(RegisterActivity.this, "Incorrect value of the Day (MM/DD/YYYY)", Toast.LENGTH_LONG).show();
                        register_age.setFocusable(true);
                        register_age.requestFocus();
                    }
                    else if (ageValidated == 5)
                    {
                        Toast.makeText(RegisterActivity.this, "Incorrect value of the Year (MM/DD/YYYY)", Toast.LENGTH_LONG).show();
                        register_age.setFocusable(true);
                        register_age.requestFocus();
                    } */
                    //else if (ageValidated == 6)
                    else
                    {

                        if (checkActiveInternetConnection())
                        {
                            user.setName(name);
                            user.setID(username);
                            user.setBirthday(age);
                            user.setEmail(email);
                            user.setPassword(password);
                            user.setGender(gender);
                            user.setMAC(mac);
                            buttonAnimation(v, 3500);
                            RegisterRequest registerRequest = new RegisterRequest(user, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                            queue.add(registerRequest);
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.coordinatorLayout), "No internet connection!", Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                        }
                                    });

                            // Changing message text color
                            snackbar.setActionTextColor(Color.RED);

                            // Changing action button text color
                            View sbView = snackbar.getView();
                            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.YELLOW);
                            snackbar.show();
                        }
                    }

            }
        });

        register_male.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (register_female.isChecked())
                    register_female.setChecked(false);
            }
        });

        register_female.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (register_male.isChecked())
                    register_male.setChecked(false);
            }
        });

        register_age.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showDialog(DATE_DIALOG_ID); }
        });

                /* DATE PICKER */
        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
                /* DATE PICKER */

    }

    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF)).append(":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    /*private String getIMEINumber() {
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return (tm.getImei());
    }*/

    public int validateAge(String age) {
        if (age.length() < 8)
            return 1;

        if ((age.charAt(2) == '/') && (age.charAt(5) == '/'))
        {
            String month = age.substring(0,2);
            String day = age.substring(3,5);
            String year = age.substring(6,10);

            if (Integer.parseInt(month) > 12 || Integer.parseInt(month) < 1)
                return 3;

            if (Integer.parseInt(day) > 31 || Integer.parseInt(day) < 1)
                return 4;

            if (Integer.parseInt(year) > Calendar.getInstance().get(Calendar.YEAR) || Integer.parseInt(year) < 1900)
                return 5;

                // Date is correct and according to the format MM/DD/YYYY
            else
                return 6;
        }

        else
            return 2;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    public boolean checkActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("Internet Error", "Error: ", e);
            }
        } else {
            Log.d("Internet Error", "No network present");
        }
        return false;
    }

    private void buttonAnimation(View v, int i) {
        AlphaAnimation buttonClick = new AlphaAnimation(1.0F, 0.1F);
        buttonClick.setDuration(i);
        v.startAnimation(buttonClick);
    }

    //return date picker dialog
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        }
        return null;
    }

    //update month day year
    private void updateDisplay() {
        register_age.setText(//this is the edit text where you want to show the selected date
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mMonth + 1).append("/")
                        .append(mDay).append("/")
                        .append(mYear).append(""));


        //.append(mMonth + 1).append("-")
        //.append(mDay).append("-")
        //.append(mYear).append(" "));
    }

    // the call back received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };
}