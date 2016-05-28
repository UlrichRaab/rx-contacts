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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
         ContactsContract.CommonDataKinds.Phone.TYPE,
         ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
         ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
         ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
   };

   @Override
   protected void onCreate (Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (savedInstanceState == null) {
         handleIntent(getIntent());
      }
   }

   @Override
   protected void onNewIntent (Intent intent) {
      handleIntent(intent);
   }

   @Override
   protected void onDestroy () {
      super.onDestroy();
      RxContacts.with(this).onDestroy();
   }

   @Override
   public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
         handleIntent(getIntent());
      }
      else {
         finish();
      }
   }

   @SuppressWarnings("UnusedParameters")
   private void handleIntent (Intent intent) {
      if (!checkPermission()) {
         return;
      }
      requestContacts();
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

   private void requestContacts () {
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
      Collection<Contact.Builder> builders = createContactBuilders(cursor);
      // Create the contacts using the builders
      List<Contact> contacts = new ArrayList<>(builders.size());
      for (Contact.Builder builder : builders) {
         Contact contact = builder.build();
         contacts.add(contact);
      }
      onContactsLoaded(contacts);
   }

   private Collection<Contact.Builder> createContactBuilders (Cursor cursor) {
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
            // Set the display name, photo and thumbnail
            mapDisplayName(cursor, builder);
            mapPhoto(cursor, builder);
            mapThumbnail(cursor, builder);
         }
         mapPhoneNumber(cursor, builder);
         // Move to the next position
         cursor.moveToNext();
      }
      cursor.close();
      return builders.values();
   }

   private void mapDisplayName (Cursor cursor, Contact.Builder contactBuilder) {
      int columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
      String displayName = cursor.getString(columnIndex);
      if (displayName != null && !displayName.isEmpty()) {
         contactBuilder.displayName(displayName);
      }
   }

   private void mapPhoto (Cursor cursor, Contact.Builder contactBuilder) {
      int columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
      String uri = cursor.getString(columnIndex);
      if (uri != null && !uri.isEmpty()) {
         contactBuilder.photo(Uri.parse(uri));
      }
   }

   private void mapThumbnail (Cursor cursor, Contact.Builder contactBuilder) {
      int columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
      String uri = cursor.getString(columnIndex);
      if (uri != null && !uri.isEmpty()) {
         contactBuilder.thumbnail(Uri.parse(uri));
      }
   }

   private void mapPhoneNumber (Cursor cursor, Contact.Builder contactBuilder) {
      PhoneNumber.Builder phoneNumberBuilder = new PhoneNumber.Builder();
      mapNumber(cursor, phoneNumberBuilder);
      mapNumberType(cursor, phoneNumberBuilder);
      PhoneNumber phoneNumber = phoneNumberBuilder.build();
      if (phoneNumber != null) {
         contactBuilder.phoneNumber(phoneNumber);
      }
   }

   private void mapNumber (Cursor cursor, PhoneNumber.Builder phoneNumberBuilder) {
      int columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
      String number = cursor.getString(columnIndex);
      if (number != null && !number.isEmpty()) {
         phoneNumberBuilder.number(number);
      }
   }

   private void mapNumberType (Cursor cursor, PhoneNumber.Builder phoneNumberBuilder) {
      int columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
      int type = cursor.getInt(columnIndex);
      phoneNumberBuilder.type(type);
      // Set the type label
      String typeLabel = getString(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(type));
      if (!typeLabel.isEmpty()) {
         phoneNumberBuilder.typeLabel(typeLabel);
      }
   }

   private void onContactsLoaded (@Nullable List<Contact> contacts) {
      RxContacts.with(this).onContactsLoaded(contacts);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         finishAndRemoveTask();
      }
      else {
         finish();
      }
   }
}
