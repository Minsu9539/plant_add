package com.example.plnatsub;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plnatsub.util.ImageResizeUtils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button main_btn1,main_btn2;
    ImageView gallery_img;
    Boolean album = false;
    private final  String TAG = getClass().getSimpleName();
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_ALBUM = 2;
    Uri photoURI,albumURI = null;
    private MyAPI mMyAPI;
    String imageFilePath;
    String mCurrentPhotoPath;
    // server의 url을 적어준다
    private final String BASE_URL = "http://6fce80ee.ngrok.io";
//    private final String BASE_URL = "http://127.0.0.1:8000";

    Runserver runserver = new Runserver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_btn1 = findViewById(R.id.main_btn1);
        main_btn2 = findViewById(R.id.main_btn2);
        //gallery_img = findViewById(R.id.gallery_img);

        // 권한 체크
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        main_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PlnatCarmer.class);
                startActivity(intent);
            }
        });

        main_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyPlantBook.class);
                startActivity(intent);
            }
        });
        initMyAPI(BASE_URL);
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
        }else{
            switch (requestCode){
                case PICK_FROM_ALBUM:
                    album = true;
                    File albumFile = null;
                    try{
                        albumFile = createImage();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    if(albumFile != null){
                        albumURI = Uri.fromFile(albumFile);
                    }
                    photoURI = data.getData();
                    Log.d(TAG,"ㅇㅇㅇㅇ : " + photoURI);
                    cropImage();
                case CROP_FROM_ALBUM:
                    Bitmap photo = BitmapFactory.decodeFile(albumURI.getPath());
                    gallery_img.setImageBitmap(photo);
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    ImageUpdate();
                    if(album == false){
                        mediaScanIntent.setData(photoURI);
                    }else if(album == true){
                        album = false;
                        mediaScanIntent.setData(albumURI);
                    }
                    Log.d(TAG,"ㅇㅇㅇㅅㅂㅈㄱㅇ : " + mediaScanIntent.setData(photoURI));
                    this.sendBroadcast(mediaScanIntent);
                    break;
            }
        }
    }

    private File createImage() throws IOException{
//        String imageFileName ="tmp_" + String.valueOf(System.currentTimeMillis())+".jpg";
//        File storageDir = new File(Environment.getExternalStorageDirectory(),imageFileName);
//        mCurrentPhotoPath = storageDir.getAbsolutePath();
//        return storageDir;


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // 보안에걸려서 못끄냄
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();

        return image;
    }
    private void cropImage(){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(photoURI,"image/*");
        cropIntent.putExtra("outputX",1080);
        cropIntent.putExtra("outputY",1080);
        cropIntent.putExtra("aspectX",1);
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("scale",true);
        if(album == false){
            cropIntent.putExtra("output",photoURI);
            Log.d(TAG,"ㅇㅇㅇㅅㅂㅈㄱㅇ : " + photoURI);
        }
        else if(album == true){
            cropIntent.putExtra("output",albumURI);
            Log.d(TAG,"ㅇ12521ㄱㅂㅈㅁㄴㅇㄱㅇ : " + albumURI);
        }
        startActivityForResult(cropIntent, CROP_FROM_ALBUM);
    }
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        return imgName;
    }

    public void ImageUpdate(){
        File file = new File(imageFilePath);

        Log.d(TAG, "Filename " + file.getName());
        Log.i( "사진이 앨범에 저장되었습니다.3121",file.getName());

        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("images", file.getName(), mFile);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mMyAPI = retrofit.create(MyAPI.class);

//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/data"),file);
//        MultipartBody.Part multiPartBody = MultipartBody.Part
//                .createFormData("images", file.getName(),requestBody);

        Call<AccountItem> call = mMyAPI.upload(fileToUpload);
        call.enqueue(new Callback<AccountItem>() {
            @Override
            public void onResponse(Call<AccountItem> call, Response<AccountItem> response) {
                Log.i("good", "good");
            }

            @Override
            public void onFailure(Call<AccountItem> call, Throwable t) {
                Log.i(TAG,"Fail msg : " + t.getMessage());
            }
        });
//        call.enqueue(new Callback<AccountItem>() {
//            @Override
//            public void onResponse(Call<AccountItem> call, Response<ResponseBody> response) {
//                Log.i("good", "good");
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.i(TAG,"Fail msg : " + t.getMessage());
//
//            }
//        });
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
