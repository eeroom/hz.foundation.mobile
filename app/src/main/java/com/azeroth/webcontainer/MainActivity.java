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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
    WebView webView;
    String targetUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dictHandlerWithActivityResult=new java.util.Hashtable<Integer, Action<Integer,Intent>>();
        this.dictHandlerWithPermissionsResult=new Hashtable<Integer, Action<String[],int[]>>();
        setContentView(R.layout.activity_main);
        this.webView= (android.webkit.WebView)this.findViewById(R.id.wv);
        HzWebClient hzWebClient=new HzWebClient(this);
        this.webView.setWebChromeClient(hzWebClient);
        this.webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        android.webkit.WebSettings webSettings= this.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        this.webView.setOnKeyListener((x,y,z)->this.handlerWebViewKeyDown(x,y,z));
        this.targetUrl="http://192.138.56.101:8080";
        this.webView.loadUrl(targetUrl);
    }

    boolean handlerWebViewKeyDown(View v, int keyCode, KeyEvent event){
        if (event.getAction() != KeyEvent.ACTION_DOWN)
            return false;
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(this.webView.canGoBack())
                this.webView.goBack();
            return true;
        }
        return false;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,1,0,"设置地址");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent=new Intent(this,Setting.class);
        intent.putExtra("url",this.targetUrl);
        int reqcode=45;
        this.dictHandlerWithActivityResult.put(reqcode,(x,y)->{
            this.targetUrl= y.getStringExtra("url");


           this.webView.loadUrl(this.targetUrl);
            this.webView.clearHistory();
        });
        this.startActivityForResult(intent,reqcode);
        return true;
    }
}