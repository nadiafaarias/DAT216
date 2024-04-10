
package recipesearch;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import se.chalmers.ait.dat215.lab2.Recipe;
import se.chalmers.ait.dat215.lab2.RecipeDatabase;



public class RecipeSearchController implements Initializable {

    @FXML private FlowPane recipeFlowPane;
    @FXML private ComboBox mainIngredientComboBox;
    @FXML private ComboBox cuisineComboBox;
    @FXML private RadioButton allRadioButton;
    @FXML private RadioButton easyRadioButton;
    @FXML private RadioButton intermediateRadioButton;
    @FXML private RadioButton hardRadioButton;
    @FXML private Spinner maxPriceSpinner;
    @FXML private Slider maxTimeSlider;
    @FXML private Label timeLabel;
    @FXML private Label detailedName;
    @FXML private ImageView detailedImage;
    @FXML private AnchorPane detailedRecipe;
    @FXML private SplitPane searchView;

    private Map<String, RecipeListItem> recipeListItemMap = new HashMap<String, RecipeListItem>();
    private RecipeBackendController recipeBackendController = new RecipeBackendController();

    RecipeDatabase db = RecipeDatabase.getSharedInstance();

    private void updateRecipeList(){
        recipeFlowPane.getChildren().clear();
        List<Recipe> recipeList = recipeBackendController.getRecipes();
        for (Recipe recipe : recipeList){
            RecipeListItem recipeListItem = recipeListItemMap.get(recipe.getName());
            recipeFlowPane.getChildren().add(recipeListItem);
        }

    }

    private void populateRecipeDetailView(Recipe recipe){
        detailedName.setText(recipe.getName());
        detailedImage.setImage(recipe.getFXImage());
    }

    @FXML
    public void closeRecipeView(){
        searchView.toFront();
    }

    public void openRecipeView(Recipe recipe){
        populateRecipeDetailView(recipe);
        detailedRecipe.toFront();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (Recipe recipe: recipeBackendController.getRecipes()){
            RecipeListItem recipeListItem = new RecipeListItem(recipe, this);
            recipeListItemMap.put(recipe.getName(), recipeListItem);
        }
        updateRecipeList();
        mainIngredientComboBox.getItems().addAll("Visa alla", "Kött", "Fisk", "Kyckling", "Vegetarisk");
        mainIngredientComboBox.getSelectionModel().select("Visa alla");
        mainIngredientComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue <? extends String> observableValue, String oldValue, String newValue) {
                recipeBackendController.setMainIngredient(newValue);
                updateRecipeList();
            }
        });
        cuisineComboBox.getItems().addAll("Visa alla", "Sverige", "Grekland", "Indien", "Asien", "Afrika", "Frankrike");
        cuisineComboBox.getSelectionModel().select("Visa alla");
        cuisineComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue <? extends String> observableValue, String oldValue, String newValue) {
                recipeBackendController.setCuisine(newValue);
                updateRecipeList();
            }
        });
        ToggleGroup difficultyToggleGroup = new ToggleGroup();
        allRadioButton.setToggleGroup(difficultyToggleGroup);
        easyRadioButton.setToggleGroup(difficultyToggleGroup);
        intermediateRadioButton.setToggleGroup(difficultyToggleGroup);
        hardRadioButton.setToggleGroup(difficultyToggleGroup);
        allRadioButton.setSelected(true);
        difficultyToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle oldValue, Toggle newValue) {
                if(difficultyToggleGroup.getSelectedToggle() != null){
                    RadioButton selected = (RadioButton) difficultyToggleGroup.getSelectedToggle();
                    recipeBackendController.setDifficulty(selected.getText());
                    updateRecipeList();
                }
            }
        });
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0, 10);
        maxPriceSpinner.setValueFactory(valueFactory);
        maxPriceSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue <? extends Integer> observableValue, Integer oldValue, Integer newValue) {
                recipeBackendController.setMaxPrice(newValue);
                updateRecipeList();
            }
        });
        maxTimeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                newValue = (newValue.intValue()/10)*10;
                timeLabel.setText(newValue.toString() + " minuter");
                if(newValue != null && !newValue.equals(oldValue) && !maxTimeSlider.isValueChanging()){
                    recipeBackendController.setMaxTime((Integer) newValue);
                    updateRecipeList();
                }
            }
        });

    }

}