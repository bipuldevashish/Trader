package com.example.bipuldevashish.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bipuldevashish.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    //variable declaration
    TextView usernameTextView, userEmailTextView, userPhoneNumberTextView;
    EditText etextName, etextEmail;
    Button saveButton, editProfileButton;
    ImageView imageViewproFilepic;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    DatabaseReference reference;
    final String TAG = "Profile";
    public static final int PICK_IMAGE = 1;
    private int requestCode;
    private int resultCode;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //EditProfile Button Linked
        editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout linearLayoutProfileDetails = findViewById(R.id.profileDetailForm);
                LinearLayout linearLayoutUpdateProfile = findViewById(R.id.updateProfileForm);

                linearLayoutProfileDetails.setVisibility(View.GONE);
                linearLayoutUpdateProfile.setVisibility(View.VISIBLE);
                imageViewproFilepic.setClickable(true);
            }
        });

        //profile pic button clickable enabled
        imageViewproFilepic = findViewById(R.id.userProfile_image);
        imageViewproFilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

            }
        });

        // Save profile Button Linked
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
                LinearLayout linearLayoutProfileDetails = findViewById(R.id.profileDetailForm);
                LinearLayout linearLayoutUpdateProfile = findViewById(R.id.updateProfileForm);
                linearLayoutProfileDetails.setVisibility(View.VISIBLE);
                linearLayoutUpdateProfile.setVisibility(View.GONE);
                imageViewproFilepic.setClickable(false);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading ");
        progressDialog.setMessage("Retrieving profile data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Linking the variables to the UI
        usernameTextView = findViewById(R.id.userName);
        userEmailTextView = findViewById(R.id.userEmail);
        userPhoneNumberTextView = findViewById(R.id.userPhoneNumber);
        etextName = findViewById(R.id.etNameUpdateProfile);
        etextEmail = findViewById(R.id.etEmailUpdateProfile);
        imageViewproFilepic = findViewById(R.id.userProfile_image);

        //Firebase reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = mDatabase.child("Users").child(userId);
        updateDatabase();
    }

    private void updateDatabase() {
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // [START_EXCLUDE]
                        if (dataSnapshot.exists()) {

                            // fetching data from firebase
                            String fullName = dataSnapshot.child("Name").getValue(String.class);
                            String Email = dataSnapshot.child("Email").getValue(String.class);
                            String Mobile = dataSnapshot.child("Mobile").getValue(String.class);

                            setProfile(fullName, Email, Mobile);

                        } else {

                            Log.d(TAG, "User " + " is unexpectedly null");
                            Toast.makeText(Profile.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Log.w(TAG, "getUser:onCancelled", error.toException());
                    }
                });
    }

    private void updateProfile() {
        String NewName = etextName.getText().toString();
        String NewEmail = etextEmail.getText().toString();
        reference.child("Name").setValue(NewName);
        reference.child("Email").setValue(NewEmail);
        Log.d(TAG, "New name and email = " + NewName + " " + NewEmail);
        updateDatabase();
    }

    public void setProfile(String name, String email, String mobile) {
        usernameTextView.setText(name);
        userEmailTextView.setText(email);
        userPhoneNumberTextView.setText(mobile);
        etextName.setText(name);
        etextEmail.setText(email);
        progressDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            //TODO: action

        }
    }
}