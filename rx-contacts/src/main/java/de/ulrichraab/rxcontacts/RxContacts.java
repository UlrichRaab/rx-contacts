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


import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.Collection;

import de.ulrichraab.rxcontacts.model.Contact;
import rx.subjects.PublishSubject;


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
public class RxContacts {

   private static RxContacts instance;

   private Context context;
   private PublishSubject<Collection<Contact>> subject;

   public static synchronized RxContacts with (Context context) {
      if (instance == null) {
         instance = new RxContacts(context.getApplicationContext());
      }
      return instance;
   }

   private RxContacts (Context context) {
      this.context = context;
   }

   public PublishSubject<Collection<Contact>> requestContacts () {
      subject = PublishSubject.create();
      startHiddenActivity();
      return subject;
   }

   public void onContactsLoaded (@Nullable Collection<Contact> contacts) {
      if (contacts != null) {
         subject.onNext(contacts);
      }
      subject.onCompleted();
   }

   void onDestroy () {
      if (subject != null) {
         subject.onCompleted();
      }
   }

   private void startHiddenActivity () {
      Intent intent = new Intent(context, HiddenActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }
}
