package com.collage.util.interactors;

import android.net.Uri;

import com.collage.base.BaseListener;
import com.collage.friendsearch.FriendSearchListener;
import com.collage.gallery.GalleryListener;
import com.collage.util.models.Photo;
import com.collage.util.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class FirebaseDatabaseInteractor {

    private final static String USERS = "users";
    private final static String EMAIL = "email";
    private final static String PENDING_FRIENDS = "pendingFriends";
    private final static String ACCEPTED_FRIENDS = "acceptedFriends";
    private static final String IMAGE_URLS = "imageUrls";

    private DatabaseReference databaseReference =
            FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance()
            .getCurrentUser();

    public void createUserDatabaseEntry(String fullName, String email) {
        firebaseUser = FirebaseAuth.getInstance()
                .getCurrentUser();
        if (firebaseUser != null) {
            databaseReference
                    .child(USERS)
                    .child(firebaseUser.getUid())
                    .setValue(new User(fullName, email, firebaseUser.getUid()));
        }
    }

    public void searchForFriend(String email, final FriendSearchListener friendSearchListener) {
        Query friendQuery = databaseReference
                .child(USERS)
                .orderByChild(EMAIL)
                .equalTo(email);
        friendQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    friendSearchListener.onFriendFound();

                    final String friendUid = dataSnapshot
                            .getChildren()
                            .iterator()
                            .next()
                            .getKey();

                    databaseReference
                            .child(USERS)
                            .child(firebaseUser.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User collageUser = dataSnapshot.getValue(User.class);
                                    databaseReference
                                            .child(USERS)
                                            .child(friendUid)
                                            .child(PENDING_FRIENDS)
                                            .child(collageUser.uid)
                                            .setValue(collageUser);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Timber.e(databaseError.getMessage());
                                }
                            });
                } else {
                    friendSearchListener.onFriendNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e(databaseError.getMessage());
            }
        });
    }

    public void fetchPendingList(final BaseListener<User> baseListener) {
        baseListener.onListFetchingStarted();
        databaseReference
                .child(USERS)
                .child(firebaseUser.getUid())
                .child(PENDING_FRIENDS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<User> pendingList = new ArrayList<>();
                        for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                            pendingList.add(dataItem.getValue(User.class));
                        }
                        baseListener.onListFetched(pendingList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e(databaseError.getMessage());
                    }
                });
    }

    public void addFriend(final User friend) {
        final String albumStorageId = UUID.randomUUID().toString();
        friend.albumStorageId = albumStorageId;

        databaseReference
                .child(USERS)
                .child(firebaseUser.getUid())
                .child(ACCEPTED_FRIENDS)
                .child(friend.uid)
                .setValue(friend);

        databaseReference
                .child(USERS)
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User collageUser = dataSnapshot.getValue(User.class);
                        collageUser.albumStorageId = albumStorageId;
                        databaseReference
                                .child(USERS)
                                .child(friend.uid)
                                .child(ACCEPTED_FRIENDS)
                                .child(firebaseUser.getUid())
                                .setValue(collageUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e(databaseError.getMessage());
                    }
                });

        databaseReference
                .child(USERS)
                .child(firebaseUser.getUid())
                .child(PENDING_FRIENDS)
                .child(friend.uid)
                .removeValue();
    }

    public void fetchFriendsList(final BaseListener<User> baseListener) {
        baseListener.onListFetchingStarted();
        databaseReference
                .child(USERS)
                .child(firebaseUser.getUid())
                .child(ACCEPTED_FRIENDS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<User> friendsList = new ArrayList<>();
                        for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                            friendsList.add(dataItem.getValue(User.class));
                        }
                        baseListener.onListFetched(friendsList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e(databaseError.getMessage());
                    }
                });
    }

    public void addImageUrl(Uri downloadUrl, User friend) {
        databaseReference
                .child(USERS)
                .child(firebaseUser.getUid())
                .child(ACCEPTED_FRIENDS)
                .child(friend.uid)
                .child(IMAGE_URLS)
                .push()
                .setValue(downloadUrl.toString());

        databaseReference
                .child(USERS)
                .child(friend.uid)
                .child(ACCEPTED_FRIENDS)
                .child(firebaseUser.getUid())
                .child(IMAGE_URLS)
                .push()
                .setValue(downloadUrl.toString());
    }

    public void fetchPhotos(User friend, final GalleryListener galleryListener) {
        galleryListener.onListFetchingStarted();
        databaseReference
                .child(USERS)
                .child(firebaseUser.getUid())
                .child(ACCEPTED_FRIENDS)
                .child(friend.uid)
                .child(IMAGE_URLS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Photo> photosList = new ArrayList<>();
                        for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                            photosList.add(new Photo(dataItem.getValue(String.class)));
                        }
                        galleryListener.onListFetched(photosList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.e(databaseError.getMessage());
                    }
                });
    }
}
