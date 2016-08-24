package de.ulrichraab.rxcontacts;


import android.net.Uri;

import java.util.HashSet;
import java.util.Set;


/**
 * Contact entity.
 * @author Ulrich Raab
 */
public class Contact {

    public final long id;
    public String displayName;
    public boolean starred;
    public Uri photo;
    public Uri thumbnail;
    public Set<String> emails = new HashSet<>();
    public Set<String> phoneNumbers = new HashSet<>();

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
