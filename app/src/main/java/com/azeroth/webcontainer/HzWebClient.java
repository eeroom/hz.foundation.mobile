package com.azeroth.webcontainer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HzWebClient extends android.webkit.WebChromeClient {
    MainActivity mainActivity;
    ValueCallback<Uri[]> filePathCallback;
    Uri cameraFile;
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
        int mode= fileChooserParams.getMode();
        String cameraFolderPath = Environment.getExternalStorageDirectory() +
                File.separator + Environment.DIRECTORY_DCIM + File.separator;
        File cameraFolder = new File(cameraFolderPath);
        if (!cameraFolder.exists()) {
            cameraFolder.mkdir();
        }
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = "IMG_" + dateFormat.format(date) + ".jpg";
        Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.cameraFile = Uri.fromFile(new File(cameraFolderPath + fileName));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// android7.0注意uri的获取方式改变
            this.cameraFile = FileProvider.getUriForFile(this.mainActivity,BuildConfig.APPLICATION_ID + ".fileProvider", new File(cameraFolderPath + fileName));
        }
        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, cameraFile);
        int reqCode=1;
        this.mainActivity.dictHandlerWithActivityResult.put(reqCode,(x,y)->this.handlerResultWithShowFileChooser(x,y));
        if(isCapture){
            this.mainActivity.startActivityForResult(intentCapture,reqCode);
            return  true;
        }
        Intent intentFile = new Intent(Intent.ACTION_GET_CONTENT);
        intentFile.addCategory(Intent.CATEGORY_OPENABLE);
        intentFile.putExtra(Intent.EXTRA_MIME_TYPES, acceptTypes);
        intentFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,mode==FileChooserParams.MODE_OPEN_MULTIPLE);
        intentFile.setType("*/*");
        Intent intentChooser = new Intent(Intent.ACTION_CHOOSER);
        intentChooser.putExtra(Intent.EXTRA_TITLE, "选择文件");
        intentChooser.putExtra(Intent.EXTRA_INTENT, intentCapture);
        intentChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentFile});
         this.mainActivity.startActivityForResult(intentChooser, reqCode);
         return true;
    }

    void handlerResultWithShowFileChooser(Integer resultCode,Intent data){
        if(resultCode!= Activity.RESULT_OK){
            this.filePathCallback.onReceiveValue(null);
            return;
        }
        if(data==null){//直接拍照的
            this.filePathCallback.onReceiveValue(new Uri[]{this.cameraFile});
            return;
        }
        Uri tmp=data.getData();
        if(tmp!=null){//单选
            this.filePathCallback.onReceiveValue(new Uri[]{tmp});
            return;
        }
        android.content.ClipData clipData=data.getClipData();
        if(clipData!=null){//多选
            Uri[] lstUri=new Uri[clipData.getItemCount()];
            for(int i=0;i<clipData.getItemCount();i++){
                lstUri[i]=clipData.getItemAt(i).getUri();
            }
            this.filePathCallback.onReceiveValue(lstUri);
        }
    }

    boolean isGranted(int code){
        return code==PackageManager.PERMISSION_GRANTED;
    }

    boolean isGranted(String permissionName){
        return this.mainActivity.checkSelfPermission(permissionName)==PackageManager.PERMISSION_GRANTED;
    }



}
