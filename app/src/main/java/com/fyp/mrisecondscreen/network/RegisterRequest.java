package com.fyp.mrisecondscreen.network;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.fyp.mrisecondscreen.utils.User;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "http://lb-89089438.us-east-2.elb.amazonaws.com/api/users/add";
    private Map<String, String> params;

    public RegisterRequest(User user, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", user.getName() + "");
        params.put("email", user.getEmail() + "");
        params.put("ID", user.getID() + "");
        params.put("password", user.getPassword() + "");
        params.put("gender", user.getGender() + "");
        params.put("relationshipStatus", user.getRelationshipStatus() + "");
        params.put("birthday", user.getBirthday() + "");
        params.put("location", user.getLocation() + "");
        params.put("MAC", user.getMAC() + "");
        params.put("mobileNumber", user.getMobileNumber() + "");
        params.put("isProfileComplete", user.isProfileComplete() + "");
        params.put("city", user.getCity() + "");
        params.put("country", user.getCountry() + "");
        //params.put("imei", imei);
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }

}
