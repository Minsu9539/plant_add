package com.example.plnatsub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageProcessing extends AppCompatActivity {

    private String imageFilePath;
    Uri photoURI = null;
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private static final int CROP_FROM_ALBUM = 1;

    ImageView image_1;
    Button btn_accept, btn_crop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        dispatchTakePictureIntent();

        image_1 = findViewById(R.id.image_1);
        btn_accept = findViewById(R.id.btn_accept);


        Bitmap photo = BitmapFactory.decodeFile(imageFilePath);
        image_1.setImageBitmap(photo);


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

    private void cropImagePhoto() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("outputX", 1080);
        cropIntent.putExtra("outputY", 1080);
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);

        cropIntent.putExtra("output", photoURI);

        startActivityForResult(cropIntent, CROP_FROM_ALBUM);
        //startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            switch (requestCode) {

                case CROP_FROM_ALBUM:
                    Bitmap photo = BitmapFactory.decodeFile(imageFilePath);
                    image_1.setImageBitmap(photo);

                    break;

                case REQUEST_IMAGE_CAPTURE:
                    cropImagePhoto();

            }
        }

    }
}
