package com.example.featuredetection;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Bitmap originalBitmap;
    public Bitmap currentBitmap;
    public ImageView imgView;
    public Mat originalMat;
    Button DoG, canny, sobel, corners;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(Intent.ACTION_PICK,
                Uri.parse("content://media/internal/images/media"));
        DoG = (Button) findViewById(R.id.gBlur);
        sobel = (Button) findViewById(R.id.sobel);
        sobel.setVisibility(View.INVISIBLE);
        sobel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sobel();
            }
        });
        DoG.setVisibility(View.INVISIBLE);
        canny = (Button) findViewById(R.id.canny);
        canny.setVisibility(View.INVISIBLE);
        DoG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DifferenceOfGaussian();
            }
        });
        canny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Canny();
            }
        });
        corners = (Button) findViewById(R.id.corner);
        corners.setVisibility(View.INVISIBLE);
        corners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HarrisCorner();
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.Settings){
            return true;
        }
        else if(id == R.id.OpenGallery){

            Intent intent =new Intent(Intent.ACTION_PICK, Uri.parse("content://media/internal/images/media"));

            startActivityForResult(intent, 0);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            String [] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Log.e("Path", filePathColumn[0]);
            Bitmap temp = BitmapFactory.decodeFile(picturePath, options);

            int orientation = 0;
            try
            {
                ExifInterface imgParams = new ExifInterface(picturePath);
                orientation = imgParams.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                Log.e("Orientation ", Integer.valueOf(orientation).toString());
            }
            catch (IOException e){
                e.printStackTrace();
            }
            Matrix rotate90 = new Matrix();
            rotate90.postRotate(orientation);
            originalBitmap = rotateBitmap(temp, orientation);
            Bitmap tempBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            originalMat = new Mat(tempBitmap.getHeight(), tempBitmap.getWidth(), CvType.CV_8U);
            Utils.bitmapToMat(tempBitmap, originalMat);

            currentBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, false);
            imgView = (ImageView) findViewById(R.id.image_view);
            imgView.setImageBitmap(currentBitmap);
            DoG.setVisibility(View.VISIBLE);
            canny.setVisibility(View.VISIBLE);
            sobel.setVisibility(View.VISIBLE);
            corners.setVisibility(View.VISIBLE);
        }
    }

    public void DifferenceOfGaussian()
    {
        Mat grayMat = new Mat();
        Mat blur1 = new Mat();
        Mat blur2 = new Mat();

        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayMat, blur1, new Size(15,15), 5);
        Imgproc.GaussianBlur(grayMat, blur2, new Size(21, 21), 5);

        Mat DoG = new Mat();
        Core.absdiff(blur1, blur2, DoG);

        Core.multiply(DoG, new Scalar(100), DoG);
        Imgproc.threshold(DoG, DoG, 50, 255, Imgproc.THRESH_BINARY_INV);

        Utils.matToBitmap(DoG, currentBitmap);
        imgView.setImageBitmap(currentBitmap);

    }

    public void Canny()
    {
        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(grayMat, cannyEdges, 10, 100);
        Utils.matToBitmap(cannyEdges, currentBitmap);
        imgView.setImageBitmap(currentBitmap);
    }

    public void Sobel(){
        Mat grayMat = new Mat();
        Mat sobel = new Mat();
        //Mat to store gradient and absolute gradient respectively
        Mat grad_x = new Mat();
        Mat abs_grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_y = new Mat();
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Sobel(grayMat, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0);
        Imgproc.Sobel(grayMat, grad_y, CvType.CV_16S, 0, 1, 3, 1, 0);
        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1, sobel);
        Utils.matToBitmap(sobel, currentBitmap);
        imgView.setImageBitmap(currentBitmap);
    }

    public void HarrisCorner(){
        Mat grayMat = new Mat();
        Mat corners = new Mat();
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        Mat tempDst = new Mat();
        Imgproc.cornerHarris(grayMat, tempDst, 2, 3, 0.04);
        Mat tempDstNorm = new Mat();
        Core.normalize(tempDst, tempDstNorm, 0, 255, Core.NORM_MINMAX);
        Core.convertScaleAbs(tempDstNorm, corners);
        Random r = new Random();
        for (int i = 0 ; i<tempDstNorm.cols(); i++){
            for (int j = 0; j < tempDstNorm.rows(); j++){
                double[] value = tempDstNorm.get(j,i);
                if (value[0] > 150)
                    Imgproc.circle(corners, new Point(i, j) , 5, new Scalar(r.nextInt(255)), 2);
            }
        }
        Utils.matToBitmap(corners, currentBitmap);
        imgView.setImageBitmap(currentBitmap);
    }

    public Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this,
                mOpenCVCallBack);
    }
}
