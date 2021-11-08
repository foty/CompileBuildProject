package com.example.aop;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFilter();
        TextView login = findViewById(R.id.tvLogin);
        login.setOnClickListener(v -> {
            extracted();
        });
    }

    @LoginFilter(loginStatue = -1)
    private void extracted() {
        Toast.makeText(this, "验证没有问题，执行点击事件", Toast.LENGTH_LONG).show();
    }

    private void initFilter() {
        ILoginFilter Filter = new ILoginFilter() {
            @Override
            public void login(int statue) {
                switch (statue) {
                    case 0:
                        Toast.makeText(MainActivity.this, "其他问题", Toast.LENGTH_SHORT).show();
                        break;
                    case -1:
                        Toast.makeText(MainActivity.this, "请先去登录！", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public boolean isLogin() { // 返回了false，不会执行click内的逻辑，而是回到到上面的login方法
                return true;
            }
        };
        LoginHelper.getInstance().setILoginFilter(Filter);
    }
}