package de.ulrichraab.rxcontacts.app;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Collection;

import de.ulrichraab.rxcontacts.model.Contact;
import de.ulrichraab.rxcontacts.RxContacts;
import de.ulrichraab.rxcontacts.model.PhoneNumber;
import rx.Observer;


public class MainActivity extends AppCompatActivity {

   public static final String TAG = MainActivity.class.getName();

   @Override
   protected void onCreate (Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      RxContacts.with(this).requestContacts().subscribe(new Observer<Collection<Contact>>() {
         @Override
         public void onCompleted () {
            Log.wtf(TAG, "Loading contacts completed");
         }

         @Override
         public void onError (Throwable e) {}

         @Override
         public void onNext (Collection<Contact> contacts) {
            Log.wtf(TAG, contacts.size() + " contacts loaded");
            for (Contact contact : contacts) {
               Log.wtf(TAG, "-----");
               String cs = convert(contact);
               Log.wtf(TAG, cs);
            }
            Log.wtf(TAG, "-----");
         }
      });
   }

   private String convert (Contact contact) {
      StringBuilder sb = new StringBuilder()
            .append("id: ").append(contact.getId()).append("\n")
            .append("name: ").append(contact.getDisplayName()).append("\n")
            .append("photo uri: ").append(contact.getPhotoUri()).append("\n");
      for (PhoneNumber phoneNumber : contact.getPhoneNumbers()) {
         sb.append("phone number (")
           .append(phoneNumber.getTypeLabel())
           .append("): ")
           .append(phoneNumber.getNumber()).append("\n");
      }
      return sb.toString();
   }
}
