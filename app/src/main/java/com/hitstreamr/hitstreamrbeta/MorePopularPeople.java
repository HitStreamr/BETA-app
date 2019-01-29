package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hitstreamr.hitstreamrbeta.UserTypes.User;

import java.util.ArrayList;
import java.util.List;

public class MorePopularPeople extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_popular_people);

        morePopularPeople();
    }

    /**
     * Show a full list of popular people.
     */
    private void morePopularPeople() {
        RecyclerView recyclerView = findViewById(R.id.morePopularPeopleRCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("PopularPeople").orderBy("followers", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> userFirestoreList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    userFirestoreList.add(documentSnapshot.getId());
                }

                // Query to Firebase
                List<User> userList = new ArrayList<>();
                MorePopularPeopleAdapter morePopularPeopleAdapter =
                        new MorePopularPeopleAdapter(userList, getApplicationContext(), getIntent());

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BasicAccounts");
                for (String userID : userFirestoreList) {
                    databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User basic_user = dataSnapshot.getValue(User.class);
                                userList.add(basic_user);
                                morePopularPeopleAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(morePopularPeopleAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
