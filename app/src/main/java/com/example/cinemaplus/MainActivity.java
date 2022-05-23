package com.example.cinemaplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String URL_MainActivity = "https://protocoderspoint.com/php/MainActivity.php";
    String luser,lpass;
    EditText username,password;
    Button MainActivityButton;
    String is_signed_in="";
    SharedPreferences mPreferences;
    String sharedprofFile="com.protocoderspoint.registration_MainActivity";
    SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences=getSharedPreferences(sharedprofFile,MODE_PRIVATE);
        preferencesEditor = mPreferences.edit();

        is_signed_in = mPreferences.getString("issignedin","false");

        if(is_signed_in.equals("true"))
        {
//            Intent i = new Intent(MainActivity.this,MainActivity.class);
//            startActivity(i);

            finish();
        }

//        Registernow =(TextView)findViewById(R.id.registernow);
        pdDialog= new ProgressDialog(MainActivity.this);
        pdDialog.setTitle("MainActivity please wait...");
        pdDialog.setCancelable(false);

        mPreferences=getSharedPreferences(sharedprofFile,MODE_PRIVATE);
        preferencesEditor = mPreferences.edit();

        username = findViewById(R.id.EmailAddress);
        password = findViewById(R.id.Password);
        MainActivityButton=(Button) findViewById(R.id.SignIn);
//        Registernow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent register=new Intent(MainActivity.this,activity_.class);
//                startActivity(register);
//            }
//        });

        MainActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                luser=username.getText().toString().trim();
                lpass=password.getText().toString().trim();
                if(luser.isEmpty()||lpass.isEmpty())
                {
                    Toast.makeText(MainActivity.this,"please enter valid data",Toast.LENGTH_SHORT).show();
                }else {
                    MainActivity();
                }
            }
        });
    }

    private void MainActivity()
    {
        pdDialog.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MainActivity,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("anyText",response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            String id= jsonObject.getString("id");
                            String name = jsonObject.getString("name");
                            String username = jsonObject.getString("username");

                            if(success.equals("1")){
                                Toast.makeText(getApplicationContext(),"Logged In  Success",Toast.LENGTH_LONG).show();
                                pdDialog.dismiss();

                                preferencesEditor.putString("issignedin","true");
                                preferencesEditor.putString("SignedInUserID",id);
                                preferencesEditor.putString("SignedInName",name);
                                preferencesEditor.putString("SignedInusername",username);
                                preferencesEditor.apply();

                                Intent i = new Intent(MainActivity.this,DashboardActivity.class);
                                startActivity(i);
                                finish();

                            }
                            if(success.equals("0")){
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                                pdDialog.dismiss();
                            }
                            if(success.equals("3")){
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                                pdDialog.dismiss();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Registration Error !1"+e,Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Registration Error !2"+error,Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                params.put("username",luser);
                params.put("password",lpass);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}