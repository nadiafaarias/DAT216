package recipesearch;

import se.chalmers.ait.dat215.lab2.*;

import java.util.List;

public class RecipeBackendController {

    RecipeDatabase db = RecipeDatabase.getSharedInstance();
    private String cuisine = null;
    private String mainIngredient = null;
    private String difficulty = null;
    private int maxPrice = 0;
    private int maxTime = 0;

    public List<Recipe> getRecipes(){
        return db.search(new SearchFilter(difficulty, maxTime, cuisine, maxPrice, mainIngredient));
    }
    public void setCuisine(String cuisine){
        switch (cuisine){
            case "Sverige", "Grekland", "Indien", "Asien", "Afrika", "Frankrike":
                this.cuisine = cuisine;
                break;
            default:
                this.cuisine = null;
                break;
        }
    }

    public void setMainIngredient(String mainIngredient){
        switch (mainIngredient){
            case "Kött", "Fisk", "Kyckling", "Vegetarisk":
                this.mainIngredient = mainIngredient;
                break;
            default:
                this.mainIngredient = null;
                break;
        }
    }

    public void setDifficulty(String difficulty){
        switch (difficulty){
            case "Lätt", "Mellan", "Svår":
                this.difficulty = difficulty;
                break;
            default:
                this.difficulty = null;
                break;
        }
    }
    public void setMaxPrice(int maxPrice){
        if(maxPrice > 0){
            this.maxPrice = maxPrice;
        }
        else{
            this.maxPrice = 0;
        }
    }

    public void setMaxTime(int maxTime){
        switch (maxTime){
            case 10, 20, 30, 40, 50, 60, 70, 90, 100, 110, 120, 130,140, 150:
                this.maxTime = maxTime;
                break;
            default:
                this.maxTime = 0;
                break;
        }
    }

}
