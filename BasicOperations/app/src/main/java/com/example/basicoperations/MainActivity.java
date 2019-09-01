package com.example.basicoperations;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private final int SELECT_PHOTO = 1;
    private ImageView ivImage, ivImageProcessed;
    Mat src;
    static int ACTION_MODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImageProcessed = (ImageView) findViewById(R.id.ivImageProcessed);
        Intent intent = getIntent();
        if (intent.hasExtra("ACTION_MODE")){
            ACTION_MODE = intent.getIntExtra("ACTION_MODE", 0);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_load_image){
            Intent photoPickerIntent = new Intent(
                    Intent.ACTION_PICK
            );
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case LoaderCallbackInterface
                        .SUCCESS:
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent){
        switch (requestCode){
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try
                    {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectImage = BitmapFactory.decodeStream(imageStream);
                        src = new Mat(selectImage.getHeight(), selectImage.getWidth(), CvType.CV_8UC4);
                        Utils.bitmapToMat(selectImage, src);
                        switch(ACTION_MODE){
                            case HomeActivity.MEAN_BLUR:
                                Imgproc.blur(src, src, new Size(47,47));
                                break;
                            case HomeActivity.GAUSSIAN_BLUR:
                                Imgproc.GaussianBlur(src, src, new Size(31,31), 0);
                                break;
                            case HomeActivity.MEDIAN_BLUR:
                                Imgproc.medianBlur(src, src, 7);
                                break;
                            case HomeActivity.DILATION:
                                Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7,7));
                                Imgproc.dilate(src, src, kernelDilate);
                                break;
                            case HomeActivity.EROSION:
                                Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5));
                                Imgproc.erode(src, src, kernelErode);
                                break;
                            case HomeActivity.THRESHOLDING:
                                Imgproc.threshold(src, src, 100, 255,
                                        Imgproc.THRESH_TRUNC);
                                break;

                        }
                        Bitmap processedImage = Bitmap.createBitmap(src.cols(),
                                src.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(src, processedImage);
                        ivImage.setImageBitmap(selectImage);
                        ivImageProcessed.setImageBitmap(processedImage);
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this,
                mOpenCVCallBack);
    }
}














