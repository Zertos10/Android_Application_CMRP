package com.example.ligneactivite.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ligneactivite.Class.ArticleItem;
import com.example.ligneactivite.Class.ArticleItemAdapter;
import com.example.ligneactivite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int jsonlenght = 0;
     List<ArticleItem> articleItems;
    private RequestQueue mQueue;
    Context context;
   BottomNavigationView bottomNavigationView;
   Contact contact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(this);
        articleItems = new ArrayList<>();
        context = this.getApplicationContext();

        extractJson();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_main_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_android:
                        Intent otherActivity = new Intent(getApplicationContext(), Contact.class);
                        startActivity(otherActivity);
                        finish();
                        break;
                    case R.id.action_logo:
                        clicked_button("https://www.google.fr/maps");
                        break;
                    case R.id.action_landscape:
                        clicked_button("http://www.google.fr");
                        break;
                    case R.id.action_inscription:
                       Intent formulaire = new Intent(getApplicationContext(), Formulaire.class);
                       startActivity(formulaire);
                        finish();
                        break;

                }
                return true;
            }
        });
    }


    public void clicked_button(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }



    //Extraction du contenue json
    private void extractJson() {
        String JSON_URL ="https://app.cmrp.net/reverso/list-actu.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                jsonlenght = response.length();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject articleObject = response.getJSONObject(i);

                        ArticleItem articleItem = new ArticleItem("","","","");

                        articleItem.setTitle(articleObject.getString("title").toString());
                        articleItem.setDescription( articleObject.getString("decription").toString());
                        articleItem.setImageUrl( articleObject.getString("urlImage"));
                        articleItem.setUrl( articleObject.getString("url"));
                        articleItems.add(articleItem);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ListView articleListView = findViewById(R.id.articlelist);

                    articleListView.setAdapter(new ArticleItemAdapter(context, articleItems));



               // ListView articleListView = findViewById(R.id.articlelist);
               // articleListView.setAdapter(new ArticleItemAdapter(this, articleItems));


        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("tag", "onErrorResponse: " + error.getMessage());
        }
    });

        queue.add(jsonArrayRequest);
}}