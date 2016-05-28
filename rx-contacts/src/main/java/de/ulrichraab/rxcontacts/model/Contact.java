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
package de.ulrichraab.rxcontacts.model;


import android.net.Uri;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
public class Contact {

   private long id;
   private String displayName;
   private List<PhoneNumber> phoneNumbers;
   private Uri photo;
   private Uri thumbnail;

   public Contact (Builder builder) {
      id = builder.id;
      displayName = builder.displayName;
      phoneNumbers = builder.phoneNumbers;
      photo = builder.photo;
      thumbnail = builder.thumbnail;
   }

   /**
    * Returns the unique id of this contact.
    * @return The unique id of this contact.
    */
   public long getId () {
      return id;
   }

   /**
    * Returns the display name of this contact.
    * @return The display name of this contact.
    */
   public String getDisplayName () {
      return displayName;
   }

   /**
    * Returns the phone numbers of this contact.
    * @return The phone numbers of this contact.
    */
   public List<PhoneNumber> getPhoneNumbers () {
      if (phoneNumbers == null) {
         phoneNumbers = new ArrayList<>(0);
      }
      return new ArrayList<>(phoneNumbers);
   }

   /**
    * Returns a URI that can be used to retrieve the contact's full-size photo.
    * @return The URI of the full-size photo of this contact.
    */
   public Uri getPhoto () {
      return photo;
   }

   /**
    * Returns a URI that can be used to retrieve a thumbnail of the contact's photo.
    * @return The URI of the thumbnail of this contact's photo.
    */
   public Uri getThumbnail () {
      return thumbnail;
   }

   @Override
   public boolean equals (Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      Contact contact = (Contact) o;
      return getId() == contact.getId();
   }

   @Override
   public int hashCode () {
      return (int) (id ^ (id >>> 32));
   }

   /**
    * Builder for {@link Contact} instances.
    */
   public final static class Builder {

      private long id;
      private String displayName;
      private List<PhoneNumber> phoneNumbers;
      private Uri photo;
      private Uri thumbnail;

      public Builder () {
         phoneNumbers = new ArrayList<>();
      }

      public Builder id (long id) {
         this.id = id;
         return this;
      }

      public Builder displayName (String displayName) {
         this.displayName = displayName;
         return this;
      }

      public Builder phoneNumber (PhoneNumber phoneNumber) {
         phoneNumbers.add(phoneNumber);
         return this;
      }

      public Builder photo (Uri photo) {
         this.photo = photo;
         return this;
      }

      public Builder thumbnail (Uri thumbnail) {
         this.thumbnail = thumbnail;
         return this;
      }

      public Contact build () {
         return new Contact(this);
      }
   }
}
