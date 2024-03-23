package com.hcmute.instagram.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.hcmute.instagram.Login;
import com.hcmute.instagram.R;
import com.hcmute.instagram.models.Users;
import com.hcmute.instagram.models.privatedetails;

public class EditProfile extends AppCompatActivity {

    ImageView mProfilePhoto;
    TextInputEditText name, username, bio, website;
    String Name, Username, Bio, Website, profile;
    DatabaseReference databaseReference, data;
    StorageReference storageReference, reff;
    CollectionReference usersCollectionRef;
    FirebaseFirestore db;
    TextView Email, Phonenumber, Gender, Birth;
    ImageView submit;
    String useridd;
    int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mProfilePhoto = (ImageView) findViewById(R.id.user_img);
        name = (TextInputEditText) findViewById(R.id.Namee);
        username = (TextInputEditText) findViewById(R.id.Usernamee);
        bio = (TextInputEditText) findViewById(R.id.Bioo);
        website = (TextInputEditText) findViewById(R.id.Websitee);
        submit = (ImageView) findViewById(R.id.rightt);
        Email = (TextView) findViewById(R.id.email);
        Phonenumber = (TextView) findViewById(R.id.phonenumber);
        Gender = (TextView) findViewById(R.id.gender);
        Birth = (TextView) findViewById(R.id.birth);


        storageReference = FirebaseStorage.getInstance().getReference();


//******************************RETRIEVING DATA***************************
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

// Retrieving user data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("Users").document(userId);
        userDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Users user = documentSnapshot.toObject(Users.class);
                            name.setText(user.getFullName());
                            username.setText(user.getUsername());
                            bio.setText(user.getDescription());
                            website.setText(user.getWebsite());
                            Glide.with(EditProfile.this)
                                    .load(user.getProfilePhoto())
                                    .into(mProfilePhoto);
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error getting user document: " + e);
                    }
                });

        CollectionReference privateDetailsCollectionRef = db.collection("Users").document(userId).collection("PrivateDetails");

        privateDetailsCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    privatedetails privateDetails = documentSnapshot.toObject(privatedetails.class);
                    // Assuming you have only one document in "PrivateDetails" subcollection
                    Email.setText(privateDetails.getEmail());
                    Phonenumber.setText(privateDetails.getPhoneNumber());
                    Gender.setText(privateDetails.getGender());
                    Birth.setText(privateDetails.getBirthdate());
                }
            }
        });

//************************************************************************


        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = name.getText().toString().trim();
                Username = username.getText().toString().trim();
                Bio = bio.getText().toString().trim();
                Website = website.getText().toString().trim();
                DocumentReference userRefDoc = db.collection("Users").document(userId);

                // Check if the username already exists
                final ProgressDialog mDialog = new ProgressDialog(EditProfile.this);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setCancelable(false);
                mDialog.setMessage("Updating...");
                mDialog.show();

                userRefDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String userNameGet = documentSnapshot.getString("username");
                            db.collection("Users")
                                    .whereEqualTo("username", Username)
                                    .whereNotEqualTo("user_id", userId)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                                String uidGet = queryDocumentSnapshots
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                // Username already exists
                                                Toast.makeText(getApplicationContext(), "Username already exists. Please try another username.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Username is available, update user information
                                                userRefDoc.update("fullName", Name,
                                                                "username", Username,
                                                                "description", Bio)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.i("SUBMIT", "onSuccess: " + Name + Username);
                                                                mDialog.dismiss();

                                                                Toast.makeText(getApplicationContext(), "User information updated successfully!", Toast.LENGTH_SHORT).show();
                                                                // You can perform further actions after updating user information here
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                mDialog.dismiss();

                                                                Log.e("Error", "Error updating user information: " + e.getMessage());
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mDialog.dismiss();
                                            
                                            Log.e("Error", "Error checking username existence: " + e.getMessage());
                                        }
                                    });

                        }
                    }
                });

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
//                userRef.orderByChild("username").equalTo(Username).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            Toast.makeText(EditProfile.this, "Username already exists. Please try another username.", Toast.LENGTH_SHORT).show();
//                        } else {
//                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                            DatabaseReference currentUserRef = userRef.child(userId);
//
//                            // Update user profile
//                            currentUserRef.child("fullName").setValue(Name);
//                            currentUserRef.child("username").setValue(Username);
//                            currentUserRef.child("description").setValue(Bio);
//                            currentUserRef.child("website").setValue(Website);
//
//                            // Set profile photo if imageUri is not null
//                            if (imageUri != null) {
//                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//                                StorageReference profilePhotoRef = storageRef.child("photos/users/" + userId + "/profilephoto");
//
//                                profilePhotoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                    @Override
//                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                        profilePhotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                            @Override
//                                            public void onSuccess(Uri uri) {
//                                                currentUserRef.child("profilePhoto").setValue(uri.toString());
//                                            }
//                                        });
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        // Handle failure
//                                    }
//                                });
//                            }
//
//                            // Show success message and navigate to Account Settings activity
//                            Toast.makeText(EditProfile.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(EditProfile.this, Account_Settings.class));
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle onCancelled
//                    }
//                });
            }

        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            mProfilePhoto.setImageURI(imageUri);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


//        https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.vexels.com%2Fpng-svg%2Fpreview%2F147102%2Finstagram-profile-icon&psig=AOvVaw0Liq2WBgqkhzMz_UQkcP5T&ust=1600009441788000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCIiNu-nx4-sCFQAAAAAdAAAAABAD

}