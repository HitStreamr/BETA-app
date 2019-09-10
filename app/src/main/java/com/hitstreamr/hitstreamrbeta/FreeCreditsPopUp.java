package com.hitstreamr.hitstreamrbeta;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FreeCreditsPopUp extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String userID;
    private String newCreditvalue;
    private int newCreditVal =150;
    private String currentCredit;

    private Button close, confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_cedits_pop_up);

        newCreditvalue = getIntent().getStringExtra("CREDIT");

        close = findViewById(R.id.closeCPop);
        confirm = findViewById(R.id.okayC);

        close.setOnClickListener(this);
        confirm.setOnClickListener(this);


        // Define the dimension
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
//        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        getWindow().setLayout((int) (width), (int) (height));
        getWindow().setBackgroundDrawable(new ColorDrawable(0x4b000000));

        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //Checking for User credits
        FirebaseDatabase.getInstance().getReference("Credits")
                .child(userID).child("creditvalue")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentCredit = dataSnapshot.getValue(String.class);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v == confirm){
           updateCredits();
           finish();
        }
        else if(v == close){
            updateCredits();
            finish();
        }
    }


    private void updateCredits(){

        if ("currentCredit" == String.valueOf(0)){
            FirebaseDatabase.getInstance()
                    .getReference("Credits")
                    .child(userID)
                    .child("creditvalue")
                    .setValue("50")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(FreeCreditsPopUp.this, "Enjoy your free credits!", Toast.LENGTH_SHORT).show();
                        }
                    });
            finish();

        } else {
            finish();
        }

    }

}
