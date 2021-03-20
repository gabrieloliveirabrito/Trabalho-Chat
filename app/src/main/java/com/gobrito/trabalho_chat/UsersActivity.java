package com.gobrito.trabalho_chat;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gobrito.trabalho_chat.Adapters.UserListAdapter;
import com.gobrito.trabalho_chat.Models.Users;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerUsers;
    private UserListAdapter recyclerAdapter;
    private List<Users> usuarios;
    private Resources resources;
    private int userCount = 0;
    private boolean loaded = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.changeLocale(this);
        setContentView(R.layout.activity_users);
        usuarios = new LinkedList<>();
        resources = getResources();

        toolbar = findViewById(R.id.users_appbar);
        toolbar.setSubtitle(R.string.loading);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerUsers = findViewById(R.id.recycler_users);
        recyclerAdapter = new UserListAdapter(this, usuarios);
        recyclerUsers.setAdapter(recyclerAdapter);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsers.setHasFixedSize(true);
        recyclerUsers.setItemViewCacheSize(20);
        recyclerUsers.setDrawingCacheEnabled(true);
        recyclerUsers.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        syncUsers();
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.users_context_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem syncItem = menu.findItem(R.id.menu_users_sync);
        syncItem.setEnabled(loaded);
        syncItem.getIcon().setAlpha(loaded ? 255 : 130);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_users_sync && item.isEnabled() && loaded)
            syncUsers();
        return super.onOptionsItemSelected(item);
    }

    private void syncUsers() {
        loaded = false;
        invalidateOptionsMenu();

        AppController.sendUsersRequest(
                users -> {
                    userCount = usuarios.size();
                    if (userCount != users.length) {
                        List toAdd = Arrays.asList(users).subList(userCount == 0 ? 0 : userCount, users.length);
                        usuarios.addAll(toAdd);
                    }

                    runOnUiThread(() -> {
                        if (userCount != usuarios.size()) {
                            boolean canScroll = recyclerUsers.canScrollVertically(1);
                            recyclerAdapter.notifyItemRangeChanged(userCount - 1, users.length - userCount);

                            if (!canScroll)
                                recyclerUsers.scrollToPosition(usuarios.size() - 1);
                        }

                        userCount = usuarios.size();
                        Toast.makeText(this, resources.getString(R.string.users_loaded), Toast.LENGTH_SHORT).show();

                        toolbar.setSubtitle(resources.getString(R.string.users_subtitle, userCount));
                        loaded = true;
                        invalidateOptionsMenu();
                    });
                },
                error -> {
                    Toast.makeText(this, R.string.users_load_failed, Toast.LENGTH_SHORT).show();
                    loaded = true;
                });
    }
}