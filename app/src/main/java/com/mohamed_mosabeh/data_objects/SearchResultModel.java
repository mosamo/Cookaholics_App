package com.mohamed_mosabeh.data_objects;

import android.widget.ImageView;

public class SearchResultModel
{
   String srvRecipeTitle, srvRecipeDescription, srvRecipeDisplayName;

   public SearchResultModel(ImageView srvImageIcon, String srvRecipeTitle, String srvRecipeDescription, String srvRecipeDisplayName)
   {
      this.srvRecipeTitle = srvRecipeTitle;
      this.srvRecipeDescription = srvRecipeDescription;
      this.srvRecipeDisplayName = srvRecipeDisplayName;
   }

   public String getSrvRecipeTitle()
   {
      return srvRecipeTitle;
   }

   public void setSrvRecipeTitle(String srvRecipeTitle)
   {
      this.srvRecipeTitle = srvRecipeTitle;
   }

   public String getSrvRecipeDescription()
   {
      return srvRecipeDescription;
   }

   public void setSrvRecipeDescription(String srvRecipeDescription)
   {
      this.srvRecipeDescription = srvRecipeDescription;
   }

   public String getSrvRecipeDisplayName()
   {
      return srvRecipeDisplayName;
   }

   public void setSrvRecipeDisplayName(String srvRecipeDisplayName)
   {
      this.srvRecipeDisplayName = srvRecipeDisplayName;
   }
}
