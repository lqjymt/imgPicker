package com.hmmy.courtyard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.ymt.imgpicker.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.pick).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
          ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
          //没有 权限
          Toast.makeText(MainActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
        }else{
          //拥有 文件 权限
          Matisse.from(MainActivity.this)
              .choose(MimeType.ofImage())
              .countable(true)
              .capture(false)
              .captureStrategy(
                  new CaptureStrategy(true, "com.hmmy.courtyard.fileprovider", "智慧庭院"))
              .maxSelectable(9)
              .isCrop(false)
              //是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
              .showSingleMediaType(true)
              .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
              .thumbnailScale(0.85f)
              .imageEngine(new GlideEngine())
              .originalEnable(true)
              .maxOriginalSize(10)
              .autoHideToolbarOnSingleTap(true)
              .fotBothResult(99);
        }

      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (RESULT_OK == resultCode) {
      if (requestCode == 99 && data != null) {
        List<String> arr = Matisse.obtainPathResult(data);
        for (String s:arr) {
          Log.d("ymt",s);
        }

      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}