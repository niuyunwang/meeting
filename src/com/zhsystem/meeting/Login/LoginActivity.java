package com.zhsystem.meeting.Login;

import com.zhsystem.meeting.Main.MainActivity;
import com.zhsystem.meeting.Main.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{
	
	private EditText username_edit;
	private EditText password_edit;
	private Button ok_btn;
	private Button cancle_btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    public void initView(){
    	username_edit=(EditText) this.findViewById(R.id.username_edit);
    	password_edit=(EditText) this.findViewById(R.id.password_edit);
    	ok_btn=(Button) this.findViewById(R.id.login_ok_btn);
    	ok_btn.setOnClickListener(LoginActivity.this);
    	cancle_btn=(Button) this.findViewById(R.id.login_cancle_btn);
    	cancle_btn.setOnClickListener(this);
    }
    
    public void intiData(){
    	
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.login_ok_btn:
			 Intent intent=new Intent(this, MainActivity.class);
			 startActivity(intent);
			 this.finish();
			break;
		case R.id.login_cancle_btn:
			
			break;
		}
	}
}
