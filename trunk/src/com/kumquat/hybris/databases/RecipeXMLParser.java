package com.kumquat.hybris.databases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;

import com.kumquat.hybris.Ingredients;
import com.kumquat.hybris.R;
import com.kumquat.hybris.Recipe;


public class RecipeXMLParser {
	
	public RecipeXMLParser(Context context, int file_id, SQLiteDatabase db) {
		//file_id = R.raw.recipes;
		//XmlResourceParser parser = context.getResources().getXml(file_id);
		//readRecipeXMLFile(parser, db);
	}
	
	
	public static Recipe[] readRecipeXMLFile(Context context, int file_id, SQLiteDatabase db) {
		file_id = R.raw.recipes;
		XmlResourceParser parser = context.getResources().getXml(file_id);
		Recipe[] recipe_list = new Recipe[0];
		try {
		    int eventType = parser.getEventType();
		    
		    while (eventType != XmlPullParser.END_DOCUMENT) {
		        String name = null;
		        String recipe_name = null;
		        int qty = -1;
		        String qty_metric = null;
		        String ing_name = null;
	        	String[] directions = new String[0];
	        	String prep = null;
	        	String cook = null;
	        	String size = null;
	        	String type = null;
	        	
	        	Ingredients[] ingredient_list = new Ingredients[0];
		        int counter = 0;
	        	
		        switch (eventType){
		            case XmlPullParser.START_TAG:
		                name = parser.getName().toLowerCase();
		                if (name.equals("name")) {
		                	recipe_name = parser.getText();
		                } else if (name.equals("qty")) {
		                	qty = Integer.parseInt(parser.getText());
		                } else if (name.equals("qty_metric")) {
		                	qty_metric = parser.getText();
		                } else if (name.equals("ing_name")) {
		                	ing_name = parser.getText();
		                } else if (name.equals("step")) {
		                	resizeArray(directions, counter+1);
		                	directions[counter] = parser.getText();
		                } else if (name.equals("prep")) {
		                	prep = parser.getText();
		                } else if (name.equals("cook")) {
		                	cook = parser.getText();
		                } else if (name.equals("size")) {
		                	size = parser.getText();
		                } else if (name.equals("type")) {
		                	type = parser.getText();
		                } 

		                break;
		            case XmlPullParser.END_TAG:
		                name = parser.getName();
		                if (name.equals("ing_name")) {
		                	Ingredients ing_obj = new Ingredients(ing_name, qty, qty_metric, db);
		                	resizeArray(ingredient_list, counter+1);
		                	ingredient_list[counter] = ing_obj;
		                	counter += 1;
		                } else if (name.equals("ingredients")) {
		                	counter = 0;
		                } else if (name.equals("step")) {
		                	counter += 1;
		                } else if (name.equals("recipe")) {
		                	Recipe rec_obj = new Recipe(ingredient_list, directions, prep, cook, size, type);
		                	resizeArray(recipe_list, counter+1);
		                	recipe_list[counter] = rec_obj;
		                	ingredient_list = null;
		                }
		               
		                break;
		        }

		        eventType = parser.next();
		    }
		} catch (XmlPullParserException e) {
		    throw new RuntimeException("Cannot parse XML");
		} catch (IOException e) {
		    throw new RuntimeException("Cannot parse XML");
		}
		finally {
		    parser.close();
		}
		
		return recipe_list;
	}
	
	
	private static Object[] resizeArray(Object array[], int newSize){
		Object newArray[] = new Object[newSize];
		for(int i = 0 ; i < newSize ; i++){
		 newArray[i] = array[i];
		 }
		return newArray;
	} 
	
}


