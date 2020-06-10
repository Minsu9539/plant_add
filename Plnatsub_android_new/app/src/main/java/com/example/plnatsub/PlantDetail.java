package com.example.plnatsub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantDetail extends AppCompatActivity {

    private final String BASE_URL = "http://85b809a8712a.ngrok.io";

    private MyAPI mMyAPI;
    Button add_galarry,go_galarry;
    private final  String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_plant);

        TextView name = (TextView)findViewById(R.id.name);
        TextView flower_flower = (TextView)findViewById(R.id.flower_flower);
        ImageView flower_img = (ImageView)findViewById(R.id.flower_img);
        TextView flower_content = (TextView)findViewById(R.id.flower_content);
         add_galarry = findViewById(R.id.addGallery);
         go_galarry = findViewById(R.id.goGallery);

        Intent intent = getIntent();

        String plant_name = intent.getExtras().getString("name_txt");
        String plant_flower = intent.getExtras().getString("flower_txt");
        String img_txt = intent.getExtras().getString("img_txt");
        String plant_content = intent.getExtras().getString("content_txt");

        name.setText(plant_name);
        Picasso.get().load(img_txt).into(flower_img);

        flower_flower.setText(plant_flower);
        flower_content.setText(plant_content);


        add_galarry.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"도감에 추가 완료", Toast.LENGTH_SHORT).show();
            }
        });

        go_galarry.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });

        initMyAPI(BASE_URL);

    }

    public void initMyAPI(String baseUrl){

        Log.d(TAG,"initMyAPI : " + baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mMyAPI = retrofit.create(MyAPI.class);
    }


}
