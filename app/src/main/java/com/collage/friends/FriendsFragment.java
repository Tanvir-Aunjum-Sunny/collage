package com.collage.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.collage.R;
import com.collage.base.BaseFragment;
import com.collage.base.HomeActivity;
import com.collage.interactors.FirebaseDatabaseInteractor;
import com.collage.util.FriendsAdapter;
import com.collage.util.model.Friend;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendsFragment extends BaseFragment implements FriendsResultListener {

    @BindView(R.id.friends_recycler_view)
    RecyclerView recyclerView;

    private FriendsPresenter friendsPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendsPresenter = new FriendsPresenter(new FirebaseDatabaseInteractor(this));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, view);

        List<Friend> friendList = new ArrayList<>();

        friendList.add(new Friend("Mateusz"));
        friendList.add(new Friend("Roman"));
        friendList.add(new Friend("Filip"));
        friendList.add(new Friend("Kuba"));
        friendList.add(new Friend("Kamil"));
        friendList.add(new Friend("Anon"));
        friendList.add(new Friend("Artur"));
        friendList.add(new Friend("Adrian"));
        friendList.add(new Friend("Michał"));
        friendList.add(new Friend("Robert"));
        friendList.add(new Friend("Tadek"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),
                        layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(new FriendsAdapter(friendList));

        return view;
    }

    @Override
    public void setMenuVisibility(boolean fragmentVisible) {
        super.setMenuVisibility(fragmentVisible);
        HomeActivity homeActivity = (HomeActivity) getActivity();
        if (homeActivity != null) {
            if (fragmentVisible) {
                showSystemUI();
                homeActivity.showHomeNavigation();
            }
        }
    }

    @OnClick(R.id.fab_add_friend)
    public void onAddFriendClicked() {
        friendsPresenter.searchForFriend("ttt@ow.po");
    }

    @Override
    public void onFriendFound() {
        Toast.makeText(getContext(), "Friend Found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFriendNotFound() {
        Toast.makeText(getContext(), "Friend NOT found", Toast.LENGTH_SHORT).show();
    }
}
