package com.hitstreamr.hitstreamrbeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class ChangePassword extends AppCompatActivity {

    private static final String TAG = "ChangePassword";

    //EditText
    private EditText EditTextCurrentpassword;
    private EditText EditTextNewpassword1;
    private EditText EditTextNewpassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        EditTextCurrentpassword = findViewById(R.id.currentPassword);
        EditTextNewpassword1 = findViewById(R.id.NewPassword1);
        EditTextNewpassword2 = findViewById(R.id.NewPassword2);


        }
}
