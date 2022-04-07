package pl.mvasio.gameApp;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import pl.mvasio.colorsPalete.MyArc;
import pl.mvasio.colorsPalete.Palette;
import pl.mvasio.game.ColorsHolder;
import pl.mvasio.game.NewGame;
import pl.mvasio.roundsView.RoundView;
import pl.mvasio.selectedColorsBar.SelectedColorsBar;

import java.io.IOException;
import java.util.List;

public class GameSceneController extends AppController{

    @FXML
    private BorderPane mainPane;
    @FXML
    private VBox leftPane;
    @FXML
    private VBox rightPane;
    @FXML
    private BorderPane selectionPane;
    @FXML
    private Pane palettePane;
    @FXML
    private HBox buttonsPane;
    @FXML
    private BorderPane textPane;
    @FXML
    private Button acceptButton;
    @FXML
    private Button undoButton;
    @FXML
    private Button backToMenuButton;
    @FXML
    private Text roundToEndLabel;
    @FXML
    private Text labelText;

    private SelectedColorsBar bar ;
    private Palette colorsPalette ;
    public static double selectionPaneWidth;
    public static final double GAME_PALETTE_RADIUS = 110;


    public GameSceneController(){
        super(AppController.colorsPoolSize, AppController.expectedQuantity, AppController.roundsQuantity, ColorGeneratorController.colors, GAME_PALETTE_RADIUS);
    }

    @FXML
    private void initialize(){
        this.initializeView();

        this.palette.setEventHandler(getPaletteEventHandler(this.game.getColorsHolder(), this.bar));
        this.undoButton.addEventHandler(ActionEvent.ACTION, getUndoButtonEventHandler(super.game.getColorsHolder(), this.bar));
        this.acceptButton.addEventHandler(ActionEvent.ACTION, getAcceptButtonEventHandler(super.game.getColorsHolder(), this.bar));
    }

    private void initializeView(){
        double radius = 15;
        double spacing = (selectionPane.getWidth() - game.COLORS_TO_GUESS_QUANTITY * radius) / game.COLORS_TO_GUESS_QUANTITY - 1;
        bar = new SelectedColorsBar(game.COLORS_TO_GUESS_QUANTITY, radius, spacing);

        selectionPane.setCenter(super.palette);
        selectionPane.setBottom(bar);

        this.setRoundToEndLabel();

        selectionPaneWidth = this.selectionPane.getPrefWidth();
        super.palette.setLayout(selectionPaneWidth / 2 , GAME_PALETTE_RADIUS);
        super.palette.setRadius(GAME_PALETTE_RADIUS);
    }

    public EventHandler<MouseEvent> getPaletteEventHandler(ColorsHolder holder, SelectedColorsBar bar ){
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                holder.addColor(((MyArc)event.getSource()).getColor());
                bar.setColors(holder.getColors());
            }
        };
    }

    public EventHandler<ActionEvent> getUndoButtonEventHandler (ColorsHolder holder, SelectedColorsBar bar ){
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                holder.removeColor(holder.getColors().size() - 1);
                bar.setColors(holder.getColors());
            }
        };
    }

    public EventHandler<ActionEvent> getAcceptButtonEventHandler ( ColorsHolder holder, SelectedColorsBar bar ){
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (holder.getColors().size() == game.COLORS_TO_GUESS_QUANTITY) {
                    RoundView round = new RoundView(game.EXPECTED_COLORS, holder.getColors());
                    bar.unselectAll();
                    holder.removeAllColors();
                    leftPane.getChildren().add(round);
                    setRoundToEndLabel();
                    if ( round.isGuessed()) gameWon();
                    else if ( RoundView.getRoundsToEndGame() <= 0){
                        gameOver();
                    }
                }
            }
        };
    }

    private void setRoundToEndLabel (){
        String text = "Szanse: " + RoundView.getRoundsToEndGame();
        this.roundToEndLabel.setText(text);
    }

    public void setColorsOfPalette(List<Color> colors){
        super.palette.setColors(colors.toArray(new Color[0]));
    }

    private void gameOver(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Niestety nie udało się...");
        alert.getDialogPane().setContentText("Nacisnij \'OK\' żeby rozpocząć od nowa.");
        labelText.setText("Nie udało się :(");
        alert.showAndWait();
        labelText.setText("");
        while ( !leftPane.getChildren().isEmpty() ){
            leftPane.getChildren().remove(0);
        }
        this.game = new NewGame(roundsQuantity, colorsToGuess.size(), colorsPool );

        setRoundToEndLabel();
    }

    public void gameWon(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Brawo mistrzu. Wygrałeś!!!");
        alert.getDialogPane().setContentText("Nacisnij \'OK\' żeby rozpocząć od nowa.");
        labelText.setText("Wygrankooo!!!");
        alert.showAndWait();
        labelText.setText("");

        while ( !leftPane.getChildren().isEmpty() ){
            leftPane.getChildren().remove(0);
        }
        this.game = new NewGame(roundsQuantity, colorsToGuess.size(), colorsPool );
        setRoundToEndLabel();
    }

    @FXML
    public void backToMenu(ActionEvent event) {
        try {
            this.switchToStartMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}