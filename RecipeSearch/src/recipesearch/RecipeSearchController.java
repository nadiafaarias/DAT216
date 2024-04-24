
package recipesearch;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
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
    @FXML private ImageView closeImageView;
    @FXML private ImageView detailedFlagImage;
    @FXML private TextArea detailedIngredientList;
    @FXML private TextArea detailedInstructions;
    @FXML private TextArea detailedDescription;
    @FXML private ImageView detailedMainIngredientImage;
    @FXML private ImageView detailedDifficultyImage;
    @FXML private Label detailedTime;
    @FXML private Label detailedCost;


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

    @FXML
    public void mouseTrap(Event event){
        event.consume();
    }

    @FXML
    public void closeButtonMouseEntered(){
        closeImageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close_hover.png")));
    }

    @FXML
    public void closeButtonMousePressed(){
        closeImageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close_pressed.png")));
    }

    @FXML
    public void closeButtonMouseExited(){
        closeImageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close.png")));
    }

    public void openRecipeView(Recipe recipe){
        populateRecipeDetailView(recipe);
        detailedRecipe.toFront();
        detailedFlagImage.setImage(getCuisineImage(recipe.getCuisine()));
        detailedMainIngredientImage.setImage(getIngredientImage(recipe.getMainIngredient()));
        detailedDifficultyImage.setImage(getDifficultyImage(recipe.getDifficulty()));
        detailedTime.setText(recipe.getTime() + " minuter");
        detailedCost.setText(recipe.getPrice() + " kr");
        detailedDescription.setText(recipe.getDescription());
        detailedInstructions.setText(recipe.getInstruction());
        for(int i = 0; i < recipe.getIngredients().size(); i++){
            var currentText = detailedIngredientList.getText();
            currentText += recipe.getIngredients().get(i) + "\n";
            detailedIngredientList.setText(currentText);
        }
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

        populateMainIngredientComboBox();
        populateCuisineComboBox();
        Platform.runLater(()->mainIngredientComboBox.requestFocus());

    }

    private void populateMainIngredientComboBox(){
        Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>() {

            @Override public ListCell<String> call(ListView<String> p) {

                return new ListCell<String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        setText(item);

                        if (item == null || empty) {
                            setGraphic(null);
                        }

                        else {
                            Image icon = null;
                            String iconPath;
                            try {
                                switch (item){

                                    case "Kött":
                                        iconPath = "RecipeSearch/resources/icon_main_meat.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Fisk":
                                        iconPath = "RecipeSearch/resources/icon_main_fish.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Kyckling":
                                        iconPath = "RecipeSearch/resources/icon_main_chicken.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Vegetarisk":
                                        iconPath = "RecipeSearch/resources/icon_main_veg.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                }
                            } catch(NullPointerException ex) {
                                //This should never happen in this lab but could load a default image in case of a NullPointer
                            }
                            ImageView iconImageView = new ImageView(icon);
                            iconImageView.setFitHeight(12);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        };
        mainIngredientComboBox.setButtonCell(cellFactory.call(null));
        mainIngredientComboBox.setCellFactory(cellFactory);
    }

    private void populateCuisineComboBox(){
        Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>() {

            @Override public ListCell<String> call(ListView<String> p) {

                return new ListCell<String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        setText(item);

                        if (item == null || empty) {
                            setGraphic(null);
                        }

                        else {
                            Image icon = null;
                            String iconPath;
                            try {
                                switch (item){

                                    case "Frankrike":
                                        iconPath = "RecipeSearch/resources/icon_flag_france.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Sverige":
                                        iconPath = "RecipeSearch/resources/icon_flag_sweden.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Grekland":
                                        iconPath = "RecipeSearch/resources/icon_flag_greece.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Indien":
                                        iconPath = "RecipeSearch/resources/icon_flag_india.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Asien":
                                        iconPath = "RecipeSearch/resources/icon_flag_asia.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Afrika":
                                        iconPath = "RecipeSearch/resources/icon_flag_africa.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                }
                            } catch(NullPointerException ex) {
                                //This should never happen in this lab but could load a default image in case of a NullPointer
                            }
                            ImageView iconImageView = new ImageView(icon);
                            iconImageView.setFitHeight(12);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        };
        cuisineComboBox.setButtonCell(cellFactory.call(null));
        cuisineComboBox.setCellFactory(cellFactory);
    }

    public Image getCuisineImage(String cuisine) {
        String iconPath;
        return switch (cuisine) {
            case "Sverige" -> {
                iconPath = "RecipeSearch/resources/icon_flag_sweden.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            case "Indien" -> {
                iconPath = "RecipeSearch/resources/icon_flag_india.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            case "Asien" -> {
                iconPath = "RecipeSearch/resources/icon_flag_asia.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            case "Frankrike" -> {
                iconPath = "RecipeSearch/resources/icon_flag_france.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            case "Grekland" -> {
                iconPath = "RecipeSearch/resources/icon_flag_greece.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            case "Afrika" -> {
                iconPath = "RecipeSearch/resources/icon_flag_africa.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            default -> throw new IllegalStateException("Unexpected value: " + cuisine);
        };
    }

    public Image getIngredientImage(String cuisine) {
        String iconPath;
        return switch (cuisine) {
            case "Vegetarisk" -> {
                iconPath = "RecipeSearch/resources/icon_main_veg.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            case "Kött" -> {
                iconPath = "RecipeSearch/resources/icon_main_meat.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            case "Fisk" -> {
                iconPath = "RecipeSearch/resources/icon_main_fish.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            case "Kyckling" -> {
                iconPath = "RecipeSearch/resources/icon_main_chicken.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            default -> throw new IllegalStateException("Unexpected value: " + cuisine);
        };
    }

    public Image getDifficultyImage(String difficulty) {
        String iconPath;
        return switch (difficulty) {
            case "Lätt" -> {
                iconPath = "RecipeSearch/resources/icon_difficulty_easy.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            case "Mellan" -> {
                iconPath = "RecipeSearch/resources/icon_difficulty_medium.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            case "Svår" -> {
                iconPath = "RecipeSearch/resources/icon_difficulty_hard.png";
                yield new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            }
            default -> throw new IllegalStateException("Unexpected value: " + difficulty);
        };

    }

}