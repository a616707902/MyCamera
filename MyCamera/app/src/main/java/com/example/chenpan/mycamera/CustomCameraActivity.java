package com.example.chenpan.mycamera;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CustomCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private Camera mCamera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private  String mFilePath= Environment.getExternalStorageDirectory().getPath()+"/"+"temp.png";
    /**
     * 拍照时获得数据的回调对象
     */
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        //data:照片的数据
            File tempfile=new File(mFilePath);//将图片存到mFilePath对应的路径文件下
            try {
                FileOutputStream fileOutputStream=new FileOutputStream(tempfile);
                fileOutputStream.write(data);//将照片以流的方式写入文件
                fileOutputStream.close();
                //拍照结束，跳转到显示照片的界面
                Intent intent=new Intent(CustomCameraActivity.this,ResultActivity.class);
                intent.putExtra("picture",mFilePath);
                startActivity(intent);
                finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        surfaceView = (SurfaceView) findViewById(R.id.preview);
        surfaceHolder = surfaceView.getHolder();//得到surfaceHolder对象
        surfaceHolder.addCallback(this);//关联surface
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(null);//点击进行对焦功能
            }
        });

    }

    //在与用户交互是打开相机
    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera();
            if (surfaceHolder != null) {
                setStartPreView(mCamera, surfaceHolder);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        relseaseCamera();
    }

    public void Capture(View view) {
        Camera.Parameters parameters = mCamera.getParameters();//得到相机参数对象，可通过这个对象设置相机基本参数
        parameters.setPictureFormat(ImageFormat.JPEG);//设置保存的照片为JPEG格式
        parameters.setPreviewSize(800, 400);//设置预览大小，根据需求设置
        parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_AUTO);//设置自动对焦
        mCamera.autoFocus(new Camera.AutoFocusCallback() {//当焦距最清晰时拍照
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    mCamera.takePicture(null, null, pictureCallback);//拍照,拍照数据通过回调的方法得到
                }
            }
        });

    }

    /**
     * 获取camera对象
     *
     * @return
     */
    public Camera getCamera() {
        Camera camera;//这里的camera是系统硬件的camera，包不能导错
        try {
            camera = Camera.open();//获取camera对象
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;

    }

    /**
     * 开始预览相机内容
     */
    private void setStartPreView(Camera camera, SurfaceHolder sHolder) {

        try {
            camera.setPreviewDisplay(sHolder);//将camera与surfaceview绑定
            camera.setDisplayOrientation(90);//camera默认是横屏的，这里我们旋转90度
            camera.startPreview();//开始预览
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机资源
     */
    private void relseaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);//将camera与surfceview解绑
            mCamera.stopPreview();//停止预览
            mCamera.release();//释放资源
            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreView(mCamera, surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        setStartPreView(mCamera, surfaceHolder);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        relseaseCamera();
    }
}
