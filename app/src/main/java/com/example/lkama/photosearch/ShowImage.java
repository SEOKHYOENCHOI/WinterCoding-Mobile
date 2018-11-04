package com.example.lkama.photosearch;

/**
 * Created by lkama on 2018-11-03.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowImage extends AppCompatActivity {
    ImageView bigImageView;
    ProgressDialog progressDialog;
    String imageName;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showimage);

        bigImageView = (ImageView)findViewById(R.id.bigImage);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("id");
        String title = bundle.getString("title");
        String secret = bundle.getString("secret");
        String server = bundle.getString("server");
        String farm = bundle.getString("farm");
        imageName=title;
        String smallImageURL = "http://farm"+farm+".staticflickr.com/"+server+"/"
                +id+"_"+secret+"_t.jpg";
        String bigImageURL = "http://farm"+farm+".staticflickr.com/"+server+"/"
                +id+"_"+secret+"_b.jpg";

        ShowimageTask task = new ShowimageTask();
        task.execute(smallImageURL, bigImageURL);

        Button download = (Button)findViewById(R.id.download);
        download.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                try {
                    savePicture();
                }
                catch(IOException e){
                    Toast.makeText(ShowImage.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button button = (Button)findViewById(R.id.back);
        button.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
    }
    void savePicture()throws FileNotFoundException{
        String imageFileName = imageName + ".jpg";

        try {
            File root = Environment.getExternalStorageDirectory();
            File cachePath = new File(root.getAbsolutePath() + imageFileName);
            cachePath.createNewFile();
            OutputStream fOut = new FileOutputStream(cachePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
            Log.d("void","save complete");

            fOut.flush();
            fOut.close();

            Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        }catch(Exception e) {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        }

}
    public Bitmap getImagefromURL(final String photoURL){
        if(photoURL==null)
            return null;
        try{
            URL url = new URL(photoURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setConnectTimeout(30000);
            con.setUseCaches(false);

            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
            Bitmap bitmap = BitmapFactory.decodeStream(bis);

            bis.close();
            con.getClass();

            return bitmap;

        }catch(Exception e){
            Log.e("비트맵 변환 예외", e.getMessage());
        }
        return null;
    }

    class ShowimageTask extends AsyncTask<String, Void, Bitmap[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ShowImage.this);
            progressDialog.setMessage("Please Wait");
            progressDialog.show();

        }

        @Override
        protected Bitmap[] doInBackground(String... strings) {
            String smallImage = strings[0];
            String bigImage = strings[1];

            Bitmap[] bitmaps = new Bitmap[2];
            bitmaps[0] = getImagefromURL(smallImage);
            bitmaps[1] = getImagefromURL(bigImage);

            return bitmaps;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap[] bitmaps) {
            super.onPostExecute(bitmaps);
            bitmap=bitmaps[1];
            bigImageView.setImageBitmap(bitmaps[1]);

            progressDialog.dismiss();
        }
    }
}



