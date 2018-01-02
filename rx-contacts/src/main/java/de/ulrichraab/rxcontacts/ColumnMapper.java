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


import android.database.Cursor;
import android.net.Uri;


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
class ColumnMapper {

    // Utility class -> No instances allowed
    private ColumnMapper () {}

    static void mapInVisibleGroup (Cursor cursor, Contact contact, int columnIndex) {
        contact.inVisibleGroup = cursor.getInt(columnIndex);
    }

    static void mapDisplayName (Cursor cursor, Contact contact, int columnIndex) {
        String displayName = cursor.getString(columnIndex);
        if (displayName != null && !displayName.isEmpty()) {
            contact.displayName = displayName;
        }
    }

    static void mapFirstName (Cursor cursor, Contact contact, int columnIndex) {
        String firstName = cursor.getString(columnIndex);
        if (firstName != null && !firstName.isEmpty()) {
            contact.firstName = firstName;
        }
    }

    static void mapLastName (Cursor cursor, Contact contact, int columnIndex) {
        String lastName = cursor.getString(columnIndex);
        if (lastName != null && !lastName.isEmpty()) {
            contact.lastName = lastName;
        }
    }

    static void mapEmail (Cursor cursor, Contact contact, int columnIndex) {
        String email = cursor.getString(columnIndex);
        if (email != null && !email.isEmpty()) {
            contact.emails.add(email);
        }
    }

    static void mapPhoneNumber (Cursor cursor, Contact contact, int columnIndex) {
        String phoneNumber = cursor.getString(columnIndex);
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            // Remove all whitespaces
            phoneNumber = phoneNumber.replaceAll("\\s+","");
            contact.phoneNumbers.add(phoneNumber);
        }
    }

    static void mapPhoto (Cursor cursor, Contact contact, int columnIndex) {
        String uri = cursor.getString(columnIndex);
        if (uri != null && !uri.isEmpty()) {
            contact.photo = Uri.parse(uri);
        }
    }

    static void mapStarred (Cursor cursor, Contact contact, int columnIndex) {
        contact.starred = cursor.getInt(columnIndex) != 0;
    }

    static void mapThumbnail (Cursor cursor, Contact contact, int columnIndex) {
        String uri = cursor.getString(columnIndex);
        if (uri != null && !uri.isEmpty()) {
            contact.thumbnail = Uri.parse(uri);
        }
    }

    static void mapAddress (Cursor cursor, Contact contact, int columnIndex) {
        String address = cursor.getString(columnIndex);
        if (address != null && !address.isEmpty()) {
            contact.addresses.add(address);
        }
    }
}
