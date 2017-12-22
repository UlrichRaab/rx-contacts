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


import android.net.Uri;

import java.util.HashSet;
import java.util.Set;


/**
 * Contact entity.
 * @author Ulrich Raab
 */
public class Contact {

    /**
     * The unique id of this contact.
     */
    public final long id;

    /**
     * Flag indicating if this contact should be visible in any user interface.
     */
    public int inVisibleGroup;

    /**
     * The display name of this contact.
     */
    public String displayName;

    /**
     * Flag indicating if this contact is a favorite contact.
     */
    public boolean starred;

    /**
     * The URI of the full-size photo of this contact.
     */
    public Uri photo;

    /**
     * The URI of the thumbnail of the photo of this contact.
     */
    public Uri thumbnail;

    /**
     * The email addresses of this contact.
     */
    public Set<String> emails = new HashSet<>();

    /**
     * The phone numbers of this contact.
     */
    public Set<String> phoneNumbers = new HashSet<>();

    /**
     * The postal addresses of this contact.
     */
    public Set<String> addresses = new HashSet<>();

    /**
     * Creates a new contact with the specified id.
     * @param id The id of the contact.
     */
    Contact (long id) {
        this.id = id;
    }

    @Override
    public int hashCode () {
        return (int) (id ^ (id >>> 32));
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
        return id == contact.id;
    }
}
