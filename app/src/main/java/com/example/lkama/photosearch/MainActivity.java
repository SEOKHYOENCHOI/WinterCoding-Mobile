package com.example.lkama.photosearch;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    String addr = "https://secure.flickr.com/services/rest/?method=flickr.photos.search" +
            "&api_key=7194ada0ac4c445d3cc29eecb884fd1e&safe_search=1&content_type=1&" +
            "sort=interestingness-desc&per_page=5&format=json&text=";
    ProgressDialog progressDialog;
    TextView textView;
    SimpleAdapter adapter;
    List<HashMap<String,Object>> photoinfoList;
    EditText keyword;
    Handler handler = new Handler(){
        public void handleMessage(Message message){
            progressDialog.dismiss();

            adapter.notifyDataSetChanged();
        }
    };
    public void getJSON(){
        Thread th = new Thread(){
            public void run(){
                String result = null;
                try{
                    String keywordText = URLEncoder.encode(keyword.getText().toString().trim(),"UTF-8");
                    addr = addr + keywordText;

                    URL url = new URL(addr);

                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setUseCaches(false);
                    con.setConnectTimeout(30000);

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    while(true) {
                        String line = br.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line);
                    }
                    result = sb.toString();
                    br.close();
                    con.disconnect();


                }catch(Exception e){
                    Log.e("다운로드 예외:",e.getMessage());
                }
                if(result != null){
                    result = result.replace("jsonFlickrApi(","");
                    result = result.replace(")","");
                    try{
                        JSONObject root = new JSONObject(result);
                        JSONObject photos = root.getJSONObject("photos");
                        JSONArray photo = photos.getJSONArray("photo");
                        photoinfoList.clear();
                        for(int i = 0; i<photo.length(); i=i+1){
                            JSONObject item = photo.getJSONObject(i);
                            HashMap<String,Object>map = new HashMap<>();
                            map.put("id",item.getString("id"));
                            map.put("secret",item.getString("secret"));
                            map.put("server",item.getString("server"));
                            map.put("farm",item.getString("farm"));
                            map.put("title",item.getString("title"));

                            photoinfoList.add(map);

                        }
                        Log.e("리스트", photoinfoList.toString());
                    }catch(Exception e){
                        Log.e("JSON 파싱 예외:", e.getMessage());
                    }
                    handler.sendEmptyMessage(0);
                }
            }
        };
        th.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyword = (EditText)findViewById(R.id.keyword);
        ListView listView = (ListView)findViewById(R.id.listView);

        photoinfoList = new ArrayList<>();
        String [] from = {"id","title","secret","server","farm"};
        int [] to ={R.id.id, R.id.title, R.id.secret, R.id.server, R.id.farm};
        adapter = new SimpleAdapter(this, photoinfoList,R.layout.listview_items,from,to);

        listView.setAdapter(adapter);
        Button button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Wait..");
                progressDialog.show();
                getJSON();
            }
        });
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,ShowImage.class);

                Bundle bundle = new Bundle();
                TextView idView = (TextView)findViewById(R.id.id);
                TextView titleView = (TextView)findViewById(R.id.title);
                TextView secretView = (TextView)findViewById(R.id.secret);
                TextView serverView = (TextView)findViewById(R.id.server);
                TextView farmView = (TextView)findViewById(R.id.farm);


                bundle.putString("id",idView.getText().toString());
                bundle.putString("title",titleView.getText().toString());
                bundle.putString("secret",secretView.getText().toString());
                bundle.putString("server",serverView.getText().toString());
                bundle.putString("farm",farmView.getText().toString());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

  }
}

