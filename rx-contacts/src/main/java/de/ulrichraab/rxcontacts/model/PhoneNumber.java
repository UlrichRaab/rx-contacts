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


import android.support.annotation.Nullable;


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
public class PhoneNumber {

   private String number;
   private int type;
   private String typeLabel;

   public PhoneNumber (Builder builder) {
      number = builder.number;
      type = builder.type;
      typeLabel = builder.typeLabel;
   }

   public String getNumber () {
      return number;
   }

   public int getType () {
      return type;
   }

   public String getTypeLabel () {
      return typeLabel;
   }

   /**
    * Builder for {@link PhoneNumber} instances.
    */
   public final static class Builder {

      private String number;
      private int type;
      private String typeLabel;

      public Builder number (String number) {
         this.number = number;
         return this;
      }

      public Builder type (int type) {
         this.type = type;
         return this;
      }

      public Builder typeLabel (String typeLabel) {
         this.typeLabel = typeLabel;
         return this;
      }

      @Nullable
      public PhoneNumber build () {
         if (number == null || number.isEmpty()) {
            return null;
         }
         return new PhoneNumber(this);
      }
   }
}
