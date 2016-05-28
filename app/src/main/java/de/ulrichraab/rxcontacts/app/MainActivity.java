package de.ulrichraab.rxcontacts.app;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

import de.ulrichraab.rxcontacts.model.Contact;
import de.ulrichraab.rxcontacts.RxContacts;
import de.ulrichraab.rxcontacts.model.PhoneNumber;
import rx.Observer;


public class MainActivity extends AppCompatActivity {

   public static final String TAG = MainActivity.class.getName();

   private ContactAdapter contactAdapter;

   @Override
   protected void onCreate (Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      initializeRecyclerView();
      requestContacts();
   }

   private void initializeRecyclerView () {
      ContactAdapter contactAdapter = getContactAdapter();
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
      RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView_contacts);
      if (rv != null) {
         rv.setAdapter(contactAdapter);
         rv.setLayoutManager(linearLayoutManager);
      }
   }

   private ContactAdapter getContactAdapter () {
      if (contactAdapter != null) {
         return contactAdapter;
      }
      contactAdapter = new ContactAdapter();
      return contactAdapter;
   }

   private void requestContacts () {
      RxContacts.with(this).requestContacts().subscribe(new Observer<Collection<Contact>>() {
         @Override
         public void onCompleted () {}
         @Override
         public void onError (Throwable e) {
            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
         }
         @Override
         public void onNext (Collection<Contact> contacts) {
            ContactAdapter contactAdapter = getContactAdapter();
            contactAdapter.setContacts(new ArrayList<>(contacts));
            contactAdapter.notifyDataSetChanged();
         }
      });
   }
}
