package com.azeroth.webcontainer;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Set;
import java.util.function.Function;


public class MainActivity extends Activity {
    public Hashtable<Integer, Action<Integer,Intent>> dictHandlerWithActivityResult;
    public Hashtable<Integer, Action<String[],int[]>> dictHandlerWithPermissionsResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dictHandlerWithActivityResult=new java.util.Hashtable<Integer, Action<Integer,Intent>>();
        this.dictHandlerWithPermissionsResult=new Hashtable<Integer, Action<String[],int[]>>();
        setContentView(R.layout.activity_main);
        android.webkit.WebView webView= (android.webkit.WebView)this.findViewById(R.id.wv);
        HzWebClient hzWebClient=new HzWebClient(this);
        webView.setWebChromeClient(hzWebClient);
        android.webkit.WebSettings webSettings= webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.loadUrl("http://192.138.56.101:8080/index2.html");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
          Action<Integer,Intent> handler= this.dictHandlerWithActivityResult.get(requestCode);
          if(handler==null )
              return;
          handler.invoke(resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Action<String[],int[]> handler=this.dictHandlerWithPermissionsResult.get(requestCode);
        if(handler==null)
            return;
        handler.invoke(permissions,grantResults);

    }
}