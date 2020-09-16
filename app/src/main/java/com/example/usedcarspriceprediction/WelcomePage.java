package com.example.usedcarspriceprediction;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WelcomePage extends AppCompatActivity {
    Spinner transmission;
    ArrayList<String> transmissionArray;
    Button predict;
    Dialog result;
    RequestQueue queue;
    final String url = "http://192.168.1.8:5000/api/";
    public EditText year, mileage, tax, mgp, engineSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        // Instantiation
        predict = findViewById(R.id.predict_btn);
        transmission = findViewById(R.id.fuelType);
        year = findViewById(R.id.year);
        tax = findViewById(R.id.tax);
        mgp = findViewById(R.id.mgp);
        engineSize = findViewById(R.id.engineSize);
        mileage = findViewById(R.id.mileage);
        queue = Volley.newRequestQueue(this);
        result = new Dialog(WelcomePage.this);

        // Populate the spinner
        transmissionArray = new ArrayList<>();
        transmissionArray.add("Transmission");
        transmissionArray.add("Manual");
        transmissionArray.add("Automatic");
        transmissionArray.add("Semi-Auto");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, transmissionArray);
        transmission.setAdapter(adapter);


        // Initiate the prediction
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    JSONObject postparams = null;
                    // Fill the jsonObject with user inputs
                    String json = "{" +
                            "year:{'0':" + year.getText().toString() +
                            "},transmission:{'0':" + transmission.getSelectedItemPosition() +
                            "},mileage:{'0':" + mileage.getText().toString() +
                            "},tax:{'0':" + tax.getText().toString() +
                            "},mpg:{'0':" + mgp.getText().toString() + "},engineSize:{'0':" + engineSize.getText().toString()
                            + "}}";
                    System.out.println(json);
                    try {
                        postparams = new JSONObject(json);
                        //System.out.println(postparams);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Send a post request and fetch the results from the flaskAPI
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postparams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray data = response.getJSONArray("data");
                                if (data.length() == 0) {
                                    Toast.makeText(getApplicationContext(), "No result !", Toast.LENGTH_LONG).show();
                                } else {
                                    JSONObject obj = data.getJSONObject(0);
                                    String prediction = "" + obj.getString("prediction").substring(1, obj.getString("prediction").lastIndexOf('.'));
                                    // Show a pop up window containing the information that the user provide and the predicted price.
                                    showpopup(prediction);
                                    //Toast.makeText(getApplicationContext(), prediction, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    queue.add(jsonObjectRequest);
                    //clear();
                    year.requestFocus();
                } else
                    Toast.makeText(getApplicationContext(), "All fields should be filled", Toast.LENGTH_SHORT).show();
            }

        });
    }
    // validate the form check if all fields are fulfilled
    public boolean validate() {
        return !year.getText().toString().equals("") && !mgp.getText().toString().equals("") &&
                !tax.getText().toString().equals("") && !engineSize.getText().toString().equals("") &&
                !mileage.getText().toString().equals("") && transmission.getSelectedItemPosition() != 0;
    }

    // clear the form
    public void clear() {
        year.setText("");
        mgp.setText("");
        tax.setText("");
        mileage.setText("");
        engineSize.setText("");
    }

    // show the pop up window
    public void showpopup(String data) {
        ImageView exit;
        TextView resultcontent;
        result.setContentView(R.layout.custompopup);
        exit = result.findViewById(R.id.exit);
        resultcontent = result.findViewById(R.id.result);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.dismiss();
            }
        });
        Spannable spannable;
        String content = "A car with the specifications you mentioned { year of production : " + year.getText().toString() +
                ", type of transmission : " + transmission.getSelectedItem().toString() + ", the mileage counter : " +
                mileage.getText().toString() + ", tax : " + tax.getText().toString() + ", that consumes : " + mgp.getText().toString() +
                " gallons per mile ,and an engine size of : " + engineSize.getText().toString() + " } it could be estimated by : "
                + data + "$.";
        resultcontent.setText(content);
        System.out.println(content);
        //result.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        clear();
        result.show();
    }
}
