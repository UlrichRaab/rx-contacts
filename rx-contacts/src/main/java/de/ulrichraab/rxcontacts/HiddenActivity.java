/*
 * Copyright (C) 2016 Ulrich Raab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ulrichraab.rxcontacts;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ulrichraab.rxcontacts.model.Contact;
import de.ulrichraab.rxcontacts.model.PhoneNumber;


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
public class HiddenActivity extends AppCompatActivity {

   public static final String[] PROJECTION = {
         ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
         ContactsContract.CommonDataKinds.Phone.NUMBER,
         ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
         ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
         ContactsContract.CommonDataKinds.Phone.TYPE
   };

   @Override
   protected void onCreate (Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (checkPermission()) {
         load();
      }
   }

   @Override
   protected void onDestroy () {
      super.onDestroy();
      RxContacts.with(this).onDestroy();
   }

   @Override
   public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
         load();
      }
      else {
         finish();
      }
   }

   private boolean checkPermission () {
      boolean permissionGranted = isPermissionGranted(Manifest.permission.READ_CONTACTS);
      if (!permissionGranted) {
         String[] permissions = {Manifest.permission.READ_CONTACTS};
         ActivityCompat.requestPermissions(HiddenActivity.this, permissions, 0);
         return false;
      }
      else {
         return true;
      }
   }

   private boolean isPermissionGranted (String permission) {
      return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
   }

   private void load () {
      Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
      Cursor cursor = getContentResolver().query(
            uri,
            PROJECTION,
            null,
            null,
            null
      );
      if (cursor == null) {
         onContactsLoaded(null);
         return;
      }
      // Create the contact builders using the cursor
      Collection<Contact.Builder> builders = mapCursor(cursor);
      // Create the contacts using the builders
      List<Contact> contacts = new ArrayList<>(builders.size());
      for (Contact.Builder builder : builders) {
         Contact contact = builder.build();
         contacts.add(contact);
      }
      onContactsLoaded(contacts);
   }

   private Collection<Contact.Builder> mapCursor (Cursor cursor) {
      Map<Long, Contact.Builder> builders = new HashMap<>();
      int columnIndex;
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
         long id = cursor.getLong(columnIndex);
         // Get the builder from the cache or create a new one if necessary
         Contact.Builder builder = builders.get(id);
         if (builder == null) {
            // If this is a new builder, add the data that can only occur once for a contact
            builder = new Contact.Builder();
            builders.put(id, builder);
            // Set the id
            builder.id(id);
            // Set the display name
            columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            builder.displayName(cursor.getString(columnIndex));
            // Set the photo uri
            columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
            builder.photoUri(cursor.getString(columnIndex));
         }
         // Add the phone number
         PhoneNumber phoneNumber = getPhoneNumber(cursor);
         builder.phoneNumber(phoneNumber);
         // Move to the next position
         cursor.moveToNext();
      }
      cursor.close();
      return builders.values();
   }

   private PhoneNumber getPhoneNumber (Cursor cursor) {
      PhoneNumber.Builder builder = new PhoneNumber.Builder();
      int columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
      builder.number(cursor.getString(columnIndex));
      columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
      int type = cursor.getInt(columnIndex);
      builder.type(type);
      String typeLabel = getPhoneTypeLabel(type);
      builder.typeLabel(typeLabel);
      return builder.build();
   }

   private String getPhoneTypeLabel (int type) {
      return getResources().getString(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(type));
   }

   private void onContactsLoaded (@Nullable Collection<Contact> contacts) {
      RxContacts.with(this).onContactsLoaded(contacts);
   }
}
