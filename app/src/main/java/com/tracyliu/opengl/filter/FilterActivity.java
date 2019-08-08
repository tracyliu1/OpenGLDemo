package com.tracyliu.opengl.filter;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tracyliu.opengl.R;

public class FilterActivity extends Activity {

    /**
     * 使用OpenGL作为滤镜
     * https://www.cnblogs.com/zhuyp1015/p/4513355.html
     */


    private GLSurfaceView mEffectView;
    private TextureRenderer rander;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


        rander = new TextureRenderer();
        rander.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.puppy));//传入图片给OpenGL
        rander.setCurrentEffect(R.id.none);//默认不设置效果


        mEffectView = findViewById(R.id.effectsview);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(rander); // 设置rander
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//只有在创建和调用requestRender()时才会刷新。
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        rander.setCurrentEffect(item.getItemId());
        mEffectView.requestRender();
        return true;
    }
}
