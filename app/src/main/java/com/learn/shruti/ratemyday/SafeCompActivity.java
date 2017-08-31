package com.learn.shruti.ratemyday;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.learn.shruti.ratemyday.Model.AnonymousComplaint;
import com.learn.shruti.ratemyday.Model.Employee;
import com.learn.shruti.ratemyday.Model.Review;

import java.nio.BufferUnderflowException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SafeCompActivity extends AppCompatActivity {
    EditText desc;
    Spinner issueSpin;
    Button compsavebutton;
    private FirebaseAuth auth;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_comp);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        desc =(EditText)findViewById(R.id.compdesctext);
        issueSpin = (Spinner)findViewById(R.id.issuespinner);
        compsavebutton = (Button)findViewById(R.id.addcompbutton);

        compsavebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descstr ="";
                String selectedIssuestr;
                if(!TextUtils.isEmpty(desc.getText().toString()))
                {

                    descstr = desc.getText().toString();
                }

                if(issueSpin.getSelectedItem() == null)
                {
                    Toast.makeText(SafeCompActivity.this, "Select an issue from the menu",Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                else
                    selectedIssuestr =  issueSpin.getSelectedItem().toString();

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                //method call to store complaint data in cloud db
                writeCompToFirebase(new AnonymousComplaint(selectedIssuestr, descstr, dateFormat.format(new Date())));

                startActivity(new Intent(SafeCompActivity.this, ShowDataActivity.class));

            }
        });


    }

    //method to store complaint data in cloud db
    private void writeCompToFirebase(AnonymousComplaint a)
    {


        try {


            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("anonym");
            mDatabase.child(mDatabase.push().getKey()).setValue(a);

            }
            catch (Exception e)
            {
                Toast.makeText(SafeCompActivity.this, "Failed to submit! try again",Toast.LENGTH_SHORT)
                        .show();
                return;
            }

    }
}
