package ru.vpcb.btplay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import static ru.vpcb.btplay.utils.Constants.*;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 16-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */
public class RecipeItem {
    private int id;
    private String name;
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private int servings;
    private String imageURL;
    private JSONObject jsonObject;
    private boolean valid;


    public RecipeItem(JSONObject jsonObject) {
        valid = parseJSON(jsonObject);
    }

// methods
    private boolean parseJSON(JSONObject jsonObject) {
        if (jsonObject == null || !jsonObject.has(KEY_ID) ||
                !jsonObject.has(KEY_INGREDIENTS) && !jsonObject.has(KEY_STEPS)) {
            return false;
        }
        try {
            this.id = jsonObject.getInt(KEY_ID);
            this.name = jsonObject.getString(KEY_NAME);
            this.ingredients = getIngredients(jsonObject.getJSONArray(KEY_INGREDIENTS));
            this.steps = getSteps(jsonObject.getJSONArray(KEY_STEPS));
            this.servings = jsonObject.getInt(KEY_SERVINGS);
            this.imageURL = jsonObject.getString(KEY_IMAGE_URL);
            this.jsonObject = jsonObject;
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return jsonObject.toString();
    }

    private List<Ingredient> getIngredients(JSONArray jsonArray) throws JSONException {
        List<Ingredient> list = new ArrayList<>();
        if (jsonArray == null) return list;
        for (int i = 0; i < jsonArray.length(); i++) {
            Ingredient ingredient = new Ingredient(jsonArray.getJSONObject(i));
            if (ingredient.valid) {
                list.add(ingredient);
            }
        }
        return list;
    }

    private List<Step> getSteps(JSONArray jsonArray) throws JSONException {
        List<Step> list = new ArrayList<>();
        if (jsonArray == null) return list;
        for (int i = 0; i < jsonArray.length(); i++) {
            Step step = new Step(jsonArray.getJSONObject(i));
            if (step.valid) {
                list.add(step);
            }
        }
        return list;
    }


    public static List<RecipeItem> getRecipeList(String jsonString) {
        List<RecipeItem> list = new ArrayList<>();
        if (jsonString == null || jsonString.isEmpty()) {
            return list;
        }
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            if (jsonArray == null && jsonArray.length() == 0) return null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);

                RecipeItem recipeItem = new RecipeItem(json);
                if (recipeItem.valid) {
                    list.add(recipeItem);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

// classes
    private class Ingredient {
        private int quantity;
        private String measure;
        private String ingredient;
        private boolean valid;


        public Ingredient(JSONObject jsonObject) {
            valid = false;
            if (jsonObject == null) {
                return;
            }
            try {
                this.quantity = jsonObject.getInt(KEY_QUANTITY);
                this.measure = jsonObject.getString(KEY_MEASURE);
                this.ingredient = jsonObject.getString(KEY_INGREDIENT);
                valid = true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class Step {
        private int id;
        private String shortDescription;
        private String description;
        private String videoURL;
        private String thumbnailURL;
        private boolean valid;

        public Step(JSONObject jsonObject) {
            valid = false;
            if (jsonObject == null) {
                return;
            }
            try {
                this.id = jsonObject.getInt(KEY_ID);
                this.shortDescription = jsonObject.getString(KEY_SHORT_DESCRIPTION);
                this.description = jsonObject.getString(KEY_DESCRIPTION);
                this.videoURL = jsonObject.getString(KEY_VIDEO_URL);
                this.thumbnailURL = jsonObject.getString(KEY_THUMBNAIL_URL);
                this.valid = true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }



}
