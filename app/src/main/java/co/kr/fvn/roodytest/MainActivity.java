package co.kr.fvn.roodytest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button btnBauadSet, btnDoorStatus, btnSlideStatus, btnLockStatus, btnDoorOpen, btnSlideOpen, btnLockOpen, btnDoorClose, btnSlideClose, btnLockClose;
    private EditText etvBaudrate;
    private TextView txtAreaSend,txtAreaRead;
    private RdSerialPtTh rdSerialPtTh;
    private boolean serialYn = false;
    private Thread serverThread;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                final String cliantMSG = intent.getStringExtra("msg");
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(cliantMSG.indexOf("Send") > -1){
                            txtAreaSend.append(cliantMSG+"\n");
                            scrollBottom(txtAreaSend);
                        }else{
                            txtAreaRead.append(cliantMSG+"\n");
                            scrollBottom(txtAreaRead);
                        }
                    }
                });
            } catch (Exception e) {
                Log.d("==> ",e.getMessage());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBauadSet = findViewById(R.id.btn_baud);
        etvBaudrate = findViewById(R.id.etv_baudrate);

        btnDoorStatus = findViewById(R.id.btn_door_status);
        btnSlideStatus = findViewById(R.id.btn_slide_status);
        btnLockStatus = findViewById(R.id.btn_lock_status);

        btnDoorOpen = findViewById(R.id.btn_door_open);
        btnSlideOpen = findViewById(R.id.btn_slide_open);
        btnLockOpen = findViewById(R.id.btn_lock_open);

        btnDoorClose = findViewById(R.id.btn_door_close);
        btnSlideClose = findViewById(R.id.btn_slide_close);
        btnLockClose = findViewById(R.id.btn_lock_close);

        txtAreaRead = findViewById(R.id.txt_area_read);
        txtAreaSend = findViewById(R.id.txt_area_send);
        txtAreaRead.setMovementMethod(new ScrollingMovementMethod());
        txtAreaSend.setMovementMethod(new ScrollingMovementMethod());

        //팝업 청취자 설정
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter("diddata"));

        btnBauadSet.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdSerialPtTh = new RdSerialPtTh(Integer.parseInt(etvBaudrate.getText().toString()),MainActivity.this);
                serialYn = true;
                Toast.makeText(MainActivity.this, "Serial 연결이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnDoorStatus.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serialYn){
                    rdSerialPtTh.sendDate("D","S");
                }else{
                    Toast.makeText(MainActivity.this, "포트 연결을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSlideStatus.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serialYn){
                    rdSerialPtTh.sendDate("S","S");
                }else{
                    Toast.makeText(MainActivity.this, "포트 연결을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLockStatus.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serialYn){
                    rdSerialPtTh.sendDate("L","S");
                }else{
                    Toast.makeText(MainActivity.this, "포트 연결을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDoorOpen.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serialYn){
                    rdSerialPtTh.sendDate("D","O");
                }else{
                    Toast.makeText(MainActivity.this, "포트 연결을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSlideOpen.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serialYn){
                    rdSerialPtTh.sendDate("S","O");
                }else{
                    Toast.makeText(MainActivity.this, "포트 연결을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLockOpen.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serialYn){
                    rdSerialPtTh.sendDate("L","O");
                }else{
                    Toast.makeText(MainActivity.this, "포트 연결을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDoorClose.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serialYn){
                    rdSerialPtTh.sendDate("D","C");
                }else{
                    Toast.makeText(MainActivity.this, "포트 연결을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSlideClose.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serialYn){
                    rdSerialPtTh.sendDate("S","C");
                }else{
                    Toast.makeText(MainActivity.this, "포트 연결을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLockClose.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serialYn){
                    rdSerialPtTh.sendDate("L","C");
                }else{
                    Toast.makeText(MainActivity.this, "포트 연결을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void scrollBottom(TextView textView) {
        int lineTop =  textView.getLayout().getLineTop(textView.getLineCount()) ;
        int scrollY = lineTop - textView.getHeight();
        if (scrollY > 0) {
            textView.scrollTo(0, scrollY);
        } else {
            textView.scrollTo(0, 0);
        }
    }
}
