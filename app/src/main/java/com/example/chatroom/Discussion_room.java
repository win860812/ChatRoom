package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.Edits;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Discussion_room extends AppCompatActivity {
    private Button sendbtn;
    private EditText inputmsg;
    private ListView msgview;
    private ArrayList<String> conversationlist = new ArrayList<String>();
    private ArrayAdapter arrayAdapter;
    private DatabaseReference dbr;
    private DatabaseReference dbr2;
    private String userName,Topic,user_msg_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_room);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap<String,Object>();
                user_msg_key = dbr.push().getKey();
                dbr.updateChildren(map);

                dbr2 =dbr.child(user_msg_key);
                Map<String,Object> map2 = new HashMap<String,Object>();
                map2.put("msg",inputmsg.getText().toString());
                map2.put("user",userName);
                dbr2.updateChildren(map2);
                inputmsg.setText("");
            }
        });

        dbr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init(){
        sendbtn = findViewById(R.id.Send_btn);
        inputmsg = findViewById(R.id.inputmsg_edittext);
        msgview = findViewById(R.id.msglistview);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,conversationlist);
        msgview.setAdapter(arrayAdapter);
        userName = getIntent().getStringExtra("username");
        Topic = getIntent().getStringExtra("select_topic");
        dbr = FirebaseDatabase.getInstance().getReference().child(Topic);
        setTitle(Topic);
    }

    private void updateConversation(DataSnapshot dataSnapshot){
        String user,msg;
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            msg = (String) ((DataSnapshot)i.next()).getValue();
            user = (String) ((DataSnapshot)i.next()).getValue();
            arrayAdapter.insert(user+":"+msg,conversationlist.size());
            arrayAdapter.notifyDataSetChanged();
        }
    }
}

