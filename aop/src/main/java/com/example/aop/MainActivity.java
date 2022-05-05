package com.example.aop;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText tvEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFilter();
        TextView login = findViewById(R.id.tvLogin);
        tvEdit = findViewById(R.id.tvEdit);
        login.setOnClickListener(v -> {
            extracted();
        });
    }

    @LoginFilter(loginStatue = -1)
    private void extracted() {
        Toast.makeText(this, "状态验证成功，继续执行点击事件", Toast.LENGTH_LONG).show();
    }

    private void initFilter() {
        ILoginFilter Filter = new ILoginFilter() {
            @Override
            public void login(int statue) { //拦截回调，登录失败回调
                switch (statue) {
                    case 0:
                        Toast.makeText(MainActivity.this, "其他问题", Toast.LENGTH_SHORT).show();
                        break;
                    case -1:
                        Toast.makeText(MainActivity.this, "状态验证失败，请先去登录！", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public boolean isLogin() { // 返回了false，不会执行click内的逻辑，而是回到到上面的login方法
                String s = tvEdit.getText().toString().trim();
                int num;
                if (TextUtils.isEmpty(s)) num = 1;
                try {
                    num = Integer.parseInt(s);
                } catch (Exception e) {
                    num = 1;
                }
                return num % 2 == 0;
            }
        };
        LoginHelper.getInstance().setILoginFilter(Filter);
    }
}