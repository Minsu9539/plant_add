package com.example.plnatsub;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.mbms.FileInfo;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PlnatCarmer extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_IMAGE_CROP = 2;
    private static final int PICK_FROM_ALBUM = 3;
    private static final int CROP_FROM_ALBUM = 4;
    private MyAPI mMyAPI;
    private final  String TAG = getClass().getSimpleName();
    // server의 url을 적어준다
    private final String BASE_URL = "http://6fce80ee.ngrok.io";
//    private final String BASE_URL = "http://127.0.0.1:8000";

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    //private Uri photoUri;
    Uri photoURI,albumURI = null;
    Boolean album = false;
    Button btn_capture, btn_album, btn_search;
    ImageView image_result;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carmer);
        //mListTv = findViewById(R.id.result1);

        btn_capture = findViewById(R.id.btn_capture);
        image_result = findViewById(R.id.image_result);
        btn_album = findViewById(R.id.btn_album);
        btn_search = findViewById(R.id.btn_search);
        //edit = findViewById(R.id.edit);





        initMyAPI(BASE_URL);

        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                //Intent intent = new Intent(PlnatCarmer.this, ImageProcessing.class);  //이미지 촬영화면 새로 만들었을때 연결
                //startActivity(intent);
            }
        });

        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAlbum();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlnatCarmer.this, SearchResult.class);
                startActivity(intent);
            }
        });



       /* btn_test.setOnClickListener(new View.OnClickListener() { //검색결과화면 연결
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlnatCarmer.this, SearchResult.class);
                startActivity(intent);
            }
        });
        */

    }


    private void dispatchTakePictureIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {

            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void callAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    private File createImageFile() throws IOException {
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

    /*

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;
            galleryAddPic();
//            getaccount();
//            PostData();
            ImageUpdate();

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegress(exifOrientation);


            } else {
                exifDegree = 0;

            }

            image_result.setImageBitmap(rotate(bitmap,exifDegree));


        }
    }

     */


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
                    cropImageAlbum();

                case CROP_FROM_ALBUM:
                    Bitmap photo = BitmapFactory.decodeFile(albumURI.getPath());
                    image_result.setImageBitmap(photo);
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

                case REQUEST_IMAGE_CAPTURE:
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                    ExifInterface exif = null;
                    galleryAddPic();
                    ImageUpdate();

                    try {
                        exif = new ExifInterface(imageFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int exifOrientation;
                    int exifDegree;

                    if (exif != null) {
                        exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        exifDegree = exifOrientationToDegress(exifOrientation);


                    } else {
                        exifDegree = 0;

                    }

                    //cropImagePhoto();
                    image_result.setImageBitmap(rotate(bitmap,exifDegree));


            }
        }
    }


    private int exifOrientationToDegress(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private void galleryAddPic(){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imageFilePath);
        Uri uri = Uri.fromFile(f);
        intent.setData(uri);
        this.sendBroadcast(intent);

//        Log.i( "사진이 앨범에 저장되었습니다.",imageFilePath+"ㅇㅇ"+intent);
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

    private void cropImagePhoto(){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(photoURI,"image/*");
        cropIntent.putExtra("outputX",1080);
        cropIntent.putExtra("outputY",1080);
        cropIntent.putExtra("aspectX",1);
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("scale",true);

            cropIntent.putExtra("output",photoURI);
            Log.d(TAG,"ㅇㅇㅇㅅㅂㅈㄱㅇ : " + photoURI);

        startActivityForResult(cropIntent, CROP_FROM_ALBUM);
        //startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    private void cropImageAlbum() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("outputX", 1080);
        cropIntent.putExtra("outputY", 1080);
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        if (album == false) {
            cropIntent.putExtra("output", photoURI);
            Log.d(TAG, "ㅇㅇㅇㅅㅂㅈㄱㅇ : " + photoURI);
        } else if (album == true) {
            cropIntent.putExtra("output", albumURI);
            Log.d(TAG, "ㅇ12521ㄱㅂㅈㅁㄴㅇㄱㅇ : " + albumURI);
        }
        startActivityForResult(cropIntent, CROP_FROM_ALBUM);
    }
/*
    public void getaccount(){
        Log.d(TAG,"GET");
        Call<List<AccountItem>> getCall = mMyAPI.get_accounts();
        getCall.enqueue(new Callback<List<AccountItem>>() {
            @Override
            public void onResponse(Call<List<AccountItem>> call, Response<List<AccountItem>> response) {
                if( response.isSuccessful()){
                    List<AccountItem> mList = response.body();
                    String result ="";
                    for( AccountItem item : mList){
                        result += "title : " + item.getContent() + "\n";
                    }
                    mListTv.setText(result);
                }else {
                    Log.d(TAG,"Status Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AccountItem>> call, Throwable t) {
                Log.d(TAG,"Fail msg : " + t.getMessage());
            }
        });
    }



*/


/*    public void PostData(){
        content = edit.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mMyAPI = retrofit.create(MyAPI.class);
        AccountItem accountItem = new AccountItem(content);
        Call<AccountItem> postCall = mMyAPI.post_accounts(accountItem);
        postCall.enqueue(new Callback<AccountItem>() {
            @Override
            public void onResponse(Call<AccountItem> call, Response<AccountItem> response) {
                if(response.isSuccessful()){
//                    ArrayList<List> list =new ArrayList<List>();
                    Log.d(TAG,"등록 완료");
//                    mListTv.setText(response.body().getJson().getVersion());
                    mListTv.setText(response.body().getContent());
                    Log.i( "사진이 앨범에 저장되었습니다.3121",content);
                }else {
                    Log.d(TAG,"Status Code : " + response.code());
                    Log.d(TAG,response.errorBody().toString());
                    Log.d(TAG,call.request().body().toString());
                }
            }
            @Override
            public void onFailure(Call<AccountItem> call, Throwable t) {
                Log.d(TAG,"Fail msg : " + t.getMessage());
            }
        });
    }

 */


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

    private void initMyAPI(String baseUrl){

        Log.d(TAG,"initMyAPI : " + baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mMyAPI = retrofit.create(MyAPI.class);
    }

}