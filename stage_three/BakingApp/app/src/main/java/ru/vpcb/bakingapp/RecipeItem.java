package ru.vpcb.bakingapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.util.List;



/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 16-Nov-17
 * Email: vadim.v.voronov@gmail.com
 */
public class RecipeItem {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("ingredients")
    @Expose
    private List<Ingredient> ingredients;
    @SerializedName("steps")
    @Expose
    private List<Step> steps;
    @SerializedName("servings")
    @Expose
    private int servings;
    @SerializedName("image")
    @Expose
    private String image;


    // classes
    public class Ingredient {
        @SerializedName("quantity")
        @Expose
        private double quantity;
        @SerializedName("measure")
        @Expose
        private String measure;
        @SerializedName("ingredient")
        @Expose
        private String ingredient;


        @Override
        public String toString() {
            String stringQuantity = new DecimalFormat("0.#").format(quantity);
            return ingredient + ", " + stringQuantity + " " + measure;
        }
    }

    public class Step {
        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("shortDescription")
        @Expose
        private String shortDescription;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("videoURL")
        @Expose
        private String videoURL;
        @SerializedName("thumbnailURL")
        @Expose
        private String thumbnailURL;


        public int getId() {
            return id;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public String getDescription() {
            return description;
        }

        public String getVideoURL() {
            return videoURL;
        }

        public String getThumbnailURL() {
            return thumbnailURL;
        }

        public void setThumbnailURL(String thumbnailURL) {
            this.thumbnailURL = thumbnailURL;
        }
    }

    // getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    //setters
    public void setImage(String image) {
        this.image = image;
    }


}
