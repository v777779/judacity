Notes:
1. Content Provider
	problem :  delete() method contains wrong strings
   // reset _ID
   db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + RecipeEntry.TABLE_NAME + "'");
   solution: remove them all
   
