package org.gilbre.app.gilbre;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;


import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;


//import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.client.methods.RequestBuilder;
//import cz.msebera.android.httpclient.entity.StringEntity;



public class LoginActivity extends AppCompatActivity {
     EditText emailText;
     EditText passwordText;
     Button loginButton;
     String email;
     String password;
     JSONObject jsonObject;
     StringEntity entity;
     TokenManager manager;
     AccessToken token;
    public String tokenType;
    public int expiresIn;
    public String accessToken;
    public String refreshToken;
    TextInputLayout inputLayoutPassword;
    TextInputLayout inputLayoutEmail;

    String url ="http://197.248.145.182/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        loginButton=(Button) findViewById(R.id.login_button);

        manager=TokenManager.getINSTANCE(getSharedPreferences("prefs",MODE_PRIVATE));


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(emailText.getText().length()==0){
                    Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_LONG).show();
                }
                else if(passwordText.getText().length()==0){
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
*/
                if(!validateEmail()){
                 return;
                }
                else if(!validatePassword()){
                    return;
               }
                else{

                    email=emailText.getText().toString();
                    password=passwordText.getText().toString();
                    //AsyncHttpClient client = new AsyncHttpClient();
                    //OkHttpClient client = new OkHttpClient();

                     jsonObject = new JSONObject();


                    try{
                        jsonObject.put("email", email);
                        jsonObject.put("username", email);
                        jsonObject.put("password",password);
                        entity = new StringEntity(jsonObject.toString());

                    }catch(Exception e){}

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(getBaseContext(),url,entity,"application/json",
                            new TextHttpResponseHandler(){

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {


                                        Toast.makeText(LoginActivity.this,throwable.getMessage(), Toast.LENGTH_LONG).show();


                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String response) {

                                    //Gson gson = new GsonBuilder().create();
                                    //token = gson.fromJson(response,AccessToken.class);

                                    try{

                                        JSONObject json = new JSONObject(response);
                                        token = new AccessToken();
                                        tokenType=json.getString("token_type");
                                        expiresIn=json.getInt("expires_in");
                                        accessToken=json.getString("access_token");
                                        refreshToken=json.getString("refresh_token");

                                        token.setRefresh_token(refreshToken);
                                        token.setAccess_token(accessToken);
                                        token.setExpires_in(expiresIn);
                                        token.setToken_type(tokenType);

                                        manager.saveToken(token);

                                    }catch(Exception e){


                                    }
                                    //Toast.makeText(LoginActivity.this,response.toString(), Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));


                                }
                            });

                }


            }
        });


    }
    private boolean validateEmail() {
        String email = emailText.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(emailText);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (passwordText.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(passwordText);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }



}
