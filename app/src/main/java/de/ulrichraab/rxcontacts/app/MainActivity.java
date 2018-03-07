package de.ulrichraab.rxcontacts.app;


import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import de.ulrichraab.rxcontacts.RxContacts;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int RC_PERMISSION_CONTACTS = 1254;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeRecyclerView();
        callPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_PERMISSION_CONTACTS)
    private void callPermission() {
        String[] perms = {Manifest.permission.READ_CONTACTS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            requestContacts();
        } else {

            EasyPermissions.requestPermissions(this, "Grant all permissions", RC_PERMISSION_CONTACTS, perms);
        }
    }

    private void initializeRecyclerView() {
        ContactAdapter contactAdapter = getContactAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView rv = findViewById(R.id.recyclerView_contacts);
        if (rv != null) {
            rv.setAdapter(contactAdapter);
            rv.setLayoutManager(linearLayoutManager);
        }
    }

    private ContactAdapter getContactAdapter() {
        if (contactAdapter != null) {
            return contactAdapter;
        }
        contactAdapter = new ContactAdapter();
        return contactAdapter;
    }

    private void requestContacts() {

        RxContacts.fetch(this)
                .filter(contact -> contact.inVisibleGroup == 1)
                .toSortedList((lhs, rhs) -> lhs.displayName.compareTo(rhs.displayName))
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contacts -> {
                    ContactAdapter adapter = getContactAdapter();
                    adapter.setContacts(contacts);
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        requestContacts();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
