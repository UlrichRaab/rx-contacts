package de.ulrichraab.rxcontacts.app;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import de.ulrichraab.rxcontacts.RxContacts;
import de.ulrichraab.rxcontacts.model.Contact;
import rx.Observer;


public class MainActivity extends AppCompatActivity {

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
      RxContacts.with(this).requestContacts().subscribe(new Observer<List<Contact>>() {
         @Override
         public void onCompleted () {}
         @Override
         public void onError (Throwable e) {
            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
         }
         @Override
         public void onNext (List<Contact> contacts) {
            ContactAdapter contactAdapter = getContactAdapter();
            contactAdapter.setContacts(contacts);
            contactAdapter.notifyDataSetChanged();
         }
      });
   }
}
