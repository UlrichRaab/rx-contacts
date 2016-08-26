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


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.LongSparseArray;

import rx.Observable;
import rx.Subscriber;

import static de.ulrichraab.rxcontacts.ColumnMapper.mapDisplayName;
import static de.ulrichraab.rxcontacts.ColumnMapper.mapEmail;
import static de.ulrichraab.rxcontacts.ColumnMapper.mapInVisibleGroup;
import static de.ulrichraab.rxcontacts.ColumnMapper.mapPhoneNumber;
import static de.ulrichraab.rxcontacts.ColumnMapper.mapPhoto;
import static de.ulrichraab.rxcontacts.ColumnMapper.mapStarred;
import static de.ulrichraab.rxcontacts.ColumnMapper.mapThumbnail;


/**
 * Android contacts as rx observable.
 * @author Ulrich Raab
 */
public class RxContacts {

    private static final String[] PROJECTION = {
        ContactsContract.Data.CONTACT_ID,
        ContactsContract.Data.DISPLAY_NAME_PRIMARY,
        ContactsContract.Data.STARRED,
        ContactsContract.Data.PHOTO_URI,
        ContactsContract.Data.PHOTO_THUMBNAIL_URI,
        ContactsContract.Data.DATA1,
        ContactsContract.Data.MIMETYPE,
        ContactsContract.Data.IN_VISIBLE_GROUP
    };

    private ContentResolver resolver;

    /**
     * Fetches all contacts from the contacts apps and social networking apps.
     * @param context The context.
     * @return Observable that emits contacts.
     */
    public static Observable<Contact> fetch (@NonNull final Context context) {
        return Observable.create(new Observable.OnSubscribe<Contact>() {
            @Override
            public void call (Subscriber<? super Contact> subscriber) {
                new RxContacts(context).fetch(subscriber);
            }
        });
    }

    private RxContacts (@NonNull Context context) {
        resolver = context.getContentResolver();
    }

    private void fetch (Subscriber<? super Contact> subscriber) {
        LongSparseArray<Contact> contacts = new LongSparseArray<>();
        // Create a new cursor and go to the first position
        Cursor cursor = createCursor();
        cursor.moveToFirst();
        // Get the column indexes
        int idxId = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        int idxInVisibleGroup = cursor.getColumnIndex(ContactsContract.Data.IN_VISIBLE_GROUP);
        int idxDisplayNamePrimary = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY);
        int idxStarred = cursor.getColumnIndex(ContactsContract.Data.STARRED);
        int idxPhoto = cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI);
        int idxThumbnail = cursor.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI);
        int idxMimetype = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
        int idxData1 = cursor.getColumnIndex(ContactsContract.Data.DATA1);
        // Map the columns to the fields of the contact
        while (!cursor.isAfterLast()) {
            // Get the id and the contact for this id. The contact may be a null.
            long id = cursor.getLong(idxId);
            Contact contact = contacts.get(id, null);
            if (contact == null) {
                // Create a new contact
                contact = new Contact(id);
                // Map the non collection attributes
                mapInVisibleGroup(cursor, contact, idxInVisibleGroup);
                mapDisplayName(cursor, contact, idxDisplayNamePrimary);
                mapStarred(cursor, contact, idxStarred);
                mapPhoto(cursor, contact, idxPhoto);
                mapThumbnail(cursor, contact, idxThumbnail);
                // Add the contact to the collection
                contacts.put(id, contact);
            } else {
                String mimetype = cursor.getString(idxMimetype);
                switch (mimetype) {
                    case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE: {
                        mapEmail(cursor, contact, idxData1);
                        break;
                    }
                    case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE: {
                        mapPhoneNumber(cursor, contact, idxData1);
                        break;
                    }
                }
            }
            cursor.moveToNext();
        }
        // Emit the contacts
        for (int i = 0; i < contacts.size(); i++) {
            subscriber.onNext(contacts.valueAt(i));
        }
        subscriber.onCompleted();
    }

    private Cursor createCursor () {
        return resolver.query(
            ContactsContract.Data.CONTENT_URI,
            PROJECTION,
            null,
            null,
            ContactsContract.Data.CONTACT_ID
        );
    }
}
