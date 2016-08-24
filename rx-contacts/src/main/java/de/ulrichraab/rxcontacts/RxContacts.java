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


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
public class RxContacts {

    private static final String[] PROJECTION = {
        ContactsContract.Data.CONTACT_ID,
        ContactsContract.Data.DISPLAY_NAME_PRIMARY,
        ContactsContract.Data.STARRED,
        ContactsContract.Data.PHOTO_URI,
        ContactsContract.Data.PHOTO_THUMBNAIL_URI,
        // DATA1 is email or phone. Type can be distinguished by MIMETYPE
        ContactsContract.Data.DATA1,
        ContactsContract.Data.MIMETYPE
    };

    private ContentResolver resolver;

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
        int idxDisplayNamePrimary = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY);
        int idxStarred = cursor.getColumnIndex(ContactsContract.Data.STARRED);
        int idxPhoto = cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI);
        int idxThumbnail = cursor.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI);
        int idxMimetype = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
        int idxData1 = cursor.getColumnIndex(ContactsContract.Data.DATA1);
        // Map the columns to the fields of the contact
        while (!cursor.isAfterLast()) {
            // Get the id and the contact for this id. The contact may be a new contact.
            long id = cursor.getLong(idxId);
            Contact contact = contacts.get(id, null);
            if (contact == null) {
                contact = new Contact(id);
                ColumnMapper.mapDisplayName(cursor, contact, idxDisplayNamePrimary);
                ColumnMapper.mapStarred(cursor, contact, idxStarred);
                ColumnMapper.mapPhoto(cursor, contact, idxPhoto);
                ColumnMapper.mapThumbnail(cursor, contact, idxThumbnail);
                // Add the contact to the collection
                contacts.put(id, contact);
            } else {
                String mimetype = cursor.getString(idxMimetype);
                switch (mimetype) {
                    case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE: {
                        ColumnMapper.mapEmail(cursor, contact, idxData1);
                        break;
                    }
                    case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE: {
                        ColumnMapper.mapPhoneNumber(cursor, contact, idxData1);
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
