package com.example.aryan.hack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by UTSAV JAIN on 1/26/2019.
 */

public class RequestListForAdmin extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private DatabaseReference databaseReference;
    private ArrayList<validationRequest> list;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.request_fragment,container,false);

        mRecyclerView = rootView.findViewById(R.id.items_list);

        refresh();
        return rootView;

    }
    public void refresh(){

        list = new ArrayList<>();
        // database fetch
        databaseReference = FirebaseDatabase.getInstance().getReference("gatherings").child("kumbhmela").child("requests");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    validationRequest val = snapshot.getValue(validationRequest.class);
                    list.add(val);
                }
                // Toast.makeText(RequestListForAdmin.this,""+list.size(),Toast.LENGTH_SHORT).show();
                mAdapter = new RequestListAdapter(getActivity().getApplicationContext(), list, getActivity());
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
