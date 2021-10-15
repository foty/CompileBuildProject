package com.example.compilebuildproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.annotation.FindView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @FindView(R.id.tvHl)
    TextView tv;

    @FindView(R.id.tvHl2)
    TextView tv2;

    @FindView(R.id.tvHl3)
    TextView tv3;

    @FindView(R.id.tvHl4)
    TextView tv4;

    @FindView(R.id.tvHl5)
    TextView tv5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        tv = findViewById(R.id.tvHl);
        bindView(this);
        tv.setText("xxxx");

    }

    public void bindView(Activity activity) {
        Class clazz = activity.getClass();
        try {
            Class bindViewClass = Class.forName(clazz.getName() + "_ViewFinding");
            Log.d("lxx", bindViewClass.getSimpleName());

            Method method = bindViewClass.getMethod("find", activity.getClass());
            method.invoke(bindViewClass.newInstance(), activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
