package com.azeroth.webcontainer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Setting extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        EditText txtbox=(EditText)this.findViewById(R.id.txtUrl);
        String targetUrl= this.getIntent().getStringExtra("url");
        txtbox.setText(targetUrl);
        Button btn=(Button) this.findViewById(R.id.btnOk);
        btn.setOnClickListener(x->{
            String url=txtbox.getText().toString();
            Intent it=new Intent();
            it.putExtra("url",url);
            this.setResult(RESULT_OK,it);
            this.finish();
        });
    }

    void txtvClick(View view){
        String url=((TextView)view).getText().toString();
        Intent it=new Intent();
        it.putExtra("url",url);
        this.setResult(RESULT_OK,it);
        this.finish();
    }
}
