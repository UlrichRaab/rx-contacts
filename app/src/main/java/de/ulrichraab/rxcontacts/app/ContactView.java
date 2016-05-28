package de.ulrichraab.rxcontacts.app;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.ulrichraab.rxcontacts.model.Contact;
import de.ulrichraab.rxcontacts.model.PhoneNumber;


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
public class ContactView extends RelativeLayout {

   private Contact contact;
   private ImageView photoView;
   private TextView displayNameView;
   private TextView phoneNumberView;

   /**
    * Inflates a new {@link ContactView} widget.
    * @param parent The view to be the parent of the inflated contact view.
    * @param attachToParent Whether the inflated contact view should be attached to the parent view.
    */
   public static ContactView inflate (ViewGroup parent, boolean attachToParent) {
      if (parent == null) {
         return null;
      }
      Context context = parent.getContext();
      LayoutInflater inflater = LayoutInflater.from(context);
      return (ContactView) inflater.inflate(R.layout.list_item_contact, parent, attachToParent);
   }

   // region Constructors

   public ContactView (Context context) {
      this(context, null);
   }

   public ContactView (Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public ContactView (Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   // endregion

   @Override
   protected void onFinishInflate () {
      super.onFinishInflate();
      photoView = getView(R.id.imageView_photo);
      displayNameView = getView(R.id.textView_displayName);
      phoneNumberView = getView(R.id.textView_phoneNumber);
   }

   /**
    * Binds this view to the given contact.
    * @param contact The contact to bind.
    */
   public void bind (@NonNull Contact contact) {
      this.contact = contact;
      updateView();
   }

   public Contact getContact () {
      return contact;
   }

   /**
    * Updates this view.
    */
   private void updateView () {
      Contact contact = getContact();
      if (contact == null) {
         String msg = "contact must be set before updating this view";
         throw new IllegalStateException(msg);
      }
      updatePhotoView(contact);
      updateDisplayNameView(contact);
      updatePhoneNumberView(contact);
   }

   private void updatePhotoView (Contact contact) {
      Uri photoUri = contact.getThumbnail();
      if (photoUri == null) {
         photoView.setImageResource(android.R.drawable.ic_input_add);
      }
      else {
         // Context context = getContext();
         photoView.setImageURI(photoUri);
         // Picasso.with(context).load(photoUri).into(photoView);
      }
   }

   private void updateDisplayNameView (Contact contact) {
      String displayName = contact.getDisplayName();
      displayNameView.setText(displayName);
   }

   private void updatePhoneNumberView (Contact contact) {
      List<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
      if (phoneNumbers.isEmpty()) {
         phoneNumberView.setVisibility(GONE);
         setEnabled(false);
      }
      else {
         PhoneNumber phoneNumber = pickPhoneNumber(phoneNumbers);
         String text = PhoneNumberUtils.formatNumber(phoneNumber.getNumber());
         text += " (" + phoneNumber.getTypeLabel() + ")";
         phoneNumberView.setText(text);
         setEnabled(true);
      }
   }

   private PhoneNumber pickPhoneNumber (List<PhoneNumber> phoneNumbers) {
      PhoneNumber result = null;
      for (PhoneNumber phoneNumber : phoneNumbers) {
         return phoneNumber;
      }
      return result;
   }

   /**
    * Returns the view with the given id.
    * @param id The id of the view.
    * @return The view if found or null otherwise.
    */
   public <T extends View> T getView (@IdRes int id) {
      // noinspection unchecked
      return (T) findViewById(id);
   }
}
