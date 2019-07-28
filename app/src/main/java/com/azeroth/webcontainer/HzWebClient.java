package com.azeroth.webcontainer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HzWebClient extends android.webkit.WebChromeClient {
    MainActivity mainActivity;
    ValueCallback<Uri[]> filePathCallback;
    public HzWebClient(MainActivity mainActivity){
        this.mainActivity=mainActivity;
    }
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if(this.isGranted(Manifest.permission.CAMERA)
                &&this.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
                &&this.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
           return this.onShowFileChooserInternal(webView,filePathCallback,fileChooserParams);
        }
        this.mainActivity.dictHandlerWithPermissionsResult.put(11,(x,y)->{
            if(this.isGranted(y[0])&&this.isGranted(y[1])&&this.isGranted(y[2])){
                this.onShowFileChooserInternal(webView,filePathCallback,fileChooserParams);
                return;
            }
            filePathCallback.onReceiveValue(null);
            Toast.makeText(this.mainActivity,"权限不足，请在系统设置中允许程序使用相机和文件存储",Toast.LENGTH_LONG).show();
        });
        this.mainActivity.requestPermissions(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},11);
        return true;
    }

    public boolean onShowFileChooserInternal(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        this.filePathCallback=filePathCallback;
        String[] acceptTypes= fileChooserParams.getAcceptTypes();
        boolean isCapture= fileChooserParams.isCaptureEnabled();
        String cameraFolderPath = Environment.getExternalStorageDirectory() +
                File.separator + Environment.DIRECTORY_DCIM + File.separator;
        File cameraFolder = new File(cameraFolderPath);
        if (!cameraFolder.exists()) {
            cameraFolder.mkdir();
        }
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = "IMG_" + dateFormat.format(date) + ".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri cameraFile = Uri.fromFile(new File(cameraFolderPath + fileName));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFile);
        int reqCode=1;
        if(this.mainActivity.dictHandlerWithActivityResult.get(reqCode)==null)
            this.mainActivity.dictHandlerWithActivityResult.put(reqCode,(x,y)->this.handlerResultWithShowFileChooser(x,y,cameraFile));
        this.mainActivity.startActivityForResult(intent,reqCode);
        return  true;
    }

    void handlerResultWithShowFileChooser(Integer resultCode,Intent data,Uri cameraFile){
        if(resultCode!= Activity.RESULT_OK){
            this.filePathCallback.onReceiveValue(null);
            return;
        }
        this.filePathCallback.onReceiveValue(new Uri[]{cameraFile});

    }

    boolean isGranted(int code){
        return code==PackageManager.PERMISSION_GRANTED;
    }

    boolean isGranted(String permissionName){
        return this.mainActivity.checkSelfPermission(permissionName)==PackageManager.PERMISSION_GRANTED;
    }

}
