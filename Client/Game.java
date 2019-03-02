package Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;

/**
 *
 * 948602 Xinwei Luo
 * 927096 Lixuan Ding
 * 950214 Lei REN
 * 897082 Min XUE
 */

public class Game {

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Socket socket;


    /**
     *
     * Initialize socket
     */

    public void init(Socket socket, BufferedWriter writer, BufferedReader reader) {
        this.socket = socket;
        this.bufferedReader = reader;
        this.bufferedWriter = writer;
    }


    /**
     *
     * Write message to the server
     */

    public synchronized void write(String msg){
        try {
            bufferedWriter.write(msg);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch(SocketException e){
            // Alert window
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText("Connection Error!");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Game game;

    public static Game getInstance() {
        if (game == null) {
            game = new Game();
        }
        return game;
    }


    /**
     *
     * Clear the game
     */

    public void clear(){
        if (game !=null){
            game = null;
        }
    }

    private static int position_x;
    private static int position_y;
    private static int HsentPosition_xL;
    private static int HsentPosition_xR;
    private static int HsentPosition_yL;
    private static int HsentPosition_yR;
    private static int VsentPosition_xL;
    private static int VsentPosition_xR;
    private static int VsentPosition_yL;
    private static int VsentPosition_yR;

    private static String Hword;
    private static String Vword;
    private static String finalA;
    private static int round =1;
    private boolean success = false;

    private GridPane gridPane = new GridPane();
    private GridPane subGridPane = new GridPane();
    private GridPane firstRowDisplay = new GridPane();
    private GridPane secondRowDisplay = new GridPane();
    public TextArea textArea = new TextArea();
    public Stage stage = new Stage();
    public ObservableList<String> observableList;
    public ListView<String> listView = new ListView<String>();

    public Label[][] labels = new Label[20][20];
    public Label[] alphabetS = new Label[13];
    public Label[] alphabetF = new Label[13];
    public Label display = new Label();
    public Button confirm = new Button();
    public Button undo = new Button();
    public Button submit = new Button();
    public Button pass = new Button();
    public Button quit = new Button();
    public Label label = new Label();


    /**
     *
     * Initialize StartAGame Stage
     */

    public void startAGame() {
        stage.setTitle("Scrabble Game");
        gridPane.setAlignment(Pos.CENTER);

        label.setFont(Font.font("Times", FontWeight.BOLD, 50));
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.DARKBLUE);

        display.setFont(Font.font("Times", FontWeight.SEMI_BOLD, 23));
        pass.setText("Pass");
        pass.setFont(Font.font("Times",FontWeight.NORMAL,16));

        HBox hBox = new HBox();
        hBox.setSpacing(35);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(display);
        hBox.getChildren().add(pass);

        VBox play = new VBox();
        play.setSpacing(20);
        play.setAlignment(Pos.CENTER_LEFT);
        play.getChildren().add(hBox);
        play.getChildren().add(subGridPane);

        Label choose = new Label();
        choose.setText("Choose A Word");
        choose.setFont(Font.font("Times", FontWeight.SEMI_BOLD, 20));

        // Initialize the chosen word area
        observableList= FXCollections.observableArrayList("","");
        listView.setItems(observableList);
        listView.setPrefHeight(50);

        submit.setText("Submit");
        submit.setFont(Font.font("Times",FontWeight.NORMAL,16));

        HBox wordDisplay = new HBox();
        wordDisplay.setSpacing(20);
        wordDisplay.setAlignment(Pos.CENTER);
        wordDisplay.getChildren().add(listView);
        wordDisplay.getChildren().add(submit);

        VBox chooseAWord = new VBox();
        chooseAWord.setSpacing(10);
        chooseAWord.setAlignment(Pos.CENTER_LEFT);
        chooseAWord.getChildren().add(choose);
        chooseAWord.getChildren().add(wordDisplay);

        confirm.setText("Confirm");
        confirm.setDisable(true);
        confirm.setFont(Font.font("Times",FontWeight.NORMAL,16));

        undo.setText("Undo");
        undo.setDisable(true);
        undo.setFont(Font.font("Times",FontWeight.NORMAL,16));

        quit.setText("Quit");
        quit.setFont(Font.font("Times",FontWeight.NORMAL,16));

        HBox buttons = new HBox();
        buttons.setSpacing(50);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().add(confirm);
        buttons.getChildren().add(undo);
        buttons.getChildren().add(quit);
        gridPane.add(buttons,3,13,3,1);

        Label score = new Label();
        score.setText("Players' Score");
        gridPane.add(score,3,8,1,1);
        score.setFont(Font.font("Times", FontWeight.SEMI_BOLD, 20));

        textArea.setPrefSize(250,400);

        VBox scoreView = new VBox();
        scoreView.setSpacing(10);
        scoreView.setAlignment(Pos.CENTER_LEFT);
        scoreView.getChildren().add(score);
        scoreView.getChildren().add(textArea);
        gridPane.add(scoreView,3,9,1,1);

        // 26 single-character letters
        char[] letterF = new char[26];
        for (int i = 0; i < letterF.length; i++) {
            letterF[i]=(char)('A' + i);
        }

        // First 13 single-character letters placed into the grid
        for(int i = 0; i < alphabetF.length; i++){
            Label firstRow = new Label();
            firstRow.setText(String.valueOf(letterF[i]));
            alphabetF[i] = firstRow;
            alphabetF[i].setFont(Font.font("Times", FontPosture.REGULAR, 17));
            alphabetF[i].setMinSize(22.2,22.2);
            alphabetF[i].setAlignment(Pos.CENTER);
            alphabetF[i].setStyle("-fx-background-color: white;" +
                    "-fx-border-width:  0.2;" +
                    "-fx-border-insets: 1;" +
                    "-fx-border-radius: 5;" +
                    "-fx-border-color: black;");
            firstRowDisplay.add(alphabetF[i],i+1,0,1,1);

            int finalI = i;

            firstRow.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override

                public void handle(MouseEvent event) {
                    Dragboard dragboard = firstRow.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(firstRow.getText());
                    dragboard.setContent(content);
                    event.consume();
                }
            });

            firstRow.setOnDragDone(new EventHandler<DragEvent>() {
                @Override

                public void handle(DragEvent event) {
                    if(event.getTransferMode() == TransferMode.MOVE){
                        firstRow.setText(String.valueOf(letterF[finalI]));
                    }
                    event.consume();
                }
            });
        }


        // Second 13 single-character letters placed into the grid
        for(int i = 0; i < alphabetS.length; i++){
            Label secondRow = new Label();
            secondRow.setText(String.valueOf(letterF[i + 13]));
            alphabetS[i] = secondRow;
            alphabetS[i].setFont(Font.font("Times", FontPosture.REGULAR, 17));
            alphabetS[i].setMinSize(22.2,22.2);
            alphabetS[i].setAlignment(Pos.CENTER);
            alphabetS[i].setStyle("-fx-background-color: white;" +
                    "-fx-border-width:  0.2;" +
                    "-fx-border-insets: 1;" +
                    "-fx-border-radius: 5;" +
                    "-fx-border-color: black;");
            secondRowDisplay.add(alphabetS[i],i+1,0,1,1);

            int finalI = i;

            secondRow.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override

                public void handle(MouseEvent event) {
                    Dragboard dragboard = secondRow.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(secondRow.getText());
                    dragboard.setContent(content);
                    event.consume();
                }
            });

            secondRow.setOnDragDone(new EventHandler<DragEvent>() {
                @Override

                public void handle(DragEvent event) {
                    if(event.getTransferMode() == TransferMode.MOVE){
                        secondRow.setText(String.valueOf(letterF[finalI + 13]));
                    }
                    event.consume();
                }
            });
        }

        VBox displayArea = new VBox();
        displayArea.setSpacing(15);
        displayArea.setAlignment(Pos.CENTER_RIGHT);
        displayArea.getChildren().add(chooseAWord);
        displayArea.getChildren().add(scoreView);
        displayArea.getChildren().add(firstRowDisplay);
        displayArea.getChildren().add(secondRowDisplay);
        displayArea.getChildren().add(buttons);

        HBox scrabble = new HBox();
        scrabble.setSpacing(60);
        scrabble.setAlignment(Pos.CENTER);
        scrabble.getChildren().add(play);
        scrabble.getChildren().add(displayArea);

        VBox wholePage = new VBox();
        wholePage.setSpacing(30);
        wholePage.setAlignment(Pos.CENTER);
        wholePage.getChildren().add(label);
        wholePage.getChildren().add(scrabble);
        gridPane.add(wholePage,1,0,5,20);


        // 20 * 20 Grid
        for(int i = 0; i < labels.length; i++){
            for (int j = 0; j < labels[i].length; j++){
                Label grid = new Label();
                labels[i][j] = grid;
                labels[i][j].setFont(Font.font("Times", FontPosture.REGULAR, 20));
                labels[i][j].setMinSize(30,30);
                labels[i][j].setAlignment(Pos.CENTER);

                // Bonus grid style
                if((i == 0 || i == 19)&&(j == 0 || j ==19)){
                    labels[i][j].setStyle("-fx-background-color: #47d6ff;" +
                            "-fx-border-width:  0.2;" +
                            "-fx-border-insets: 1;" +
                            "-fx-border-radius: 5;" +
                            "-fx-border-color: black;");
                }
                else {
                    labels[i][j].setStyle("-fx-background-color: white;" +
                            "-fx-border-width:  0.2;" +
                            "-fx-border-insets: 1;" +
                            "-fx-border-radius: 5;" +
                            "-fx-border-color: black;");
                }
                subGridPane.add(labels[i][j],j+1,i+1,1,1);
            }
        }

        for(int i = 0; i < labels.length; i++){
            for (int j = 0; j < labels[i].length; j++){
                int finalJ = j;
                int finalI = i;
                if (labels[i][j].getText() == ""){
                    labels[i][j].setOnDragOver(new EventHandler<DragEvent>() {
                        @Override

                        public void handle(DragEvent event) {
                            if(event.getGestureSource() != labels[finalI][finalJ] && event.getDragboard().hasString()){
                                event.acceptTransferModes(TransferMode.MOVE);
                            }
                            event.consume();
                        }
                    });

                    labels[i][j].setOnDragEntered(new EventHandler<DragEvent>() {
                        @Override

                        public void handle(DragEvent event) {
                            if(event.getGestureSource() != labels[finalI][finalJ] && event.getDragboard().hasString()){
                                labels[finalI][finalJ].setTextFill(Color.GREEN);
                            }
                            event.consume();
                        }
                    });

                    labels[i][j].setOnDragExited(new EventHandler<DragEvent>() {
                        @Override

                        public void handle(DragEvent event) {
                            labels[finalI][finalJ].setTextFill(Color.BLACK);
                            event.consume();
                        }
                    });

                    labels[i][j].setOnDragDropped(new EventHandler<DragEvent>() {
                        @Override

                        public void handle(DragEvent event) {
                            Dragboard dragboard = event.getDragboard();

                            if(dragboard.hasString()){
                                // if round is 1, then choose position randomly
                                if (round == 1){
                                    labels[finalI][finalJ].setText(dragboard.getString());
                                    success=true;
                                    position_x = finalI;
                                    position_y = finalJ;
                                    confirm.setDisable(false);
                                    undo.setDisable(false);
                                    for(int i = 0; i< 13; i++){
                                        alphabetF[i].setDisable(true);
                                        alphabetS[i].setDisable(true);
                                    }
                                }else if(labels[finalI][finalJ].getText().equals("")){
                                    labels[finalI][finalJ].setText(dragboard.getString());
                                    success=true;
                                    position_x = finalI;
                                    position_y = finalJ;
                                    confirm.setDisable(false);
                                    undo.setDisable(false);
                                    for(int i = 0; i< 13; i++){
                                        alphabetF[i].setDisable(true);
                                        alphabetS[i].setDisable(true);

                                    }
                                }


                                /**
                                 *
                                 * The action after clicking button Confirm
                                 */

                                confirm.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                                    @Override

                                    public void handle(MouseEvent event) {
                                        if (success){
                                            String content = labels[position_x][position_y].getText();
                                            System.out.println("this is the position that should be updated" + content);
                                            write("position|"+ position_x + "|"+ position_y + "|" + content);

                                            finalA = dragboard.getString();
                                            success = true;
                                            position_x = finalI;
                                            position_y = finalJ;
                                            System.out.println("this label "+labels[finalI][finalJ].getText());

                                            Hword = labels[position_x][position_y].getText();
                                            Vword =labels[position_x][position_y].getText();
                                            for (int a =1; position_x + a <=20; a++) {
                                                if ((position_x + a ==20)||(labels[position_x + a][position_y].getText() == "")) {
                                                    HsentPosition_xR = position_x + a - 1;
                                                    HsentPosition_yR =  position_y;
                                                    System.out.println("sent positionHR");
                                                    System.out.println(HsentPosition_xR);
                                                    System.out.println(HsentPosition_yR);
                                                    break;
                                                }
                                                Hword = Hword + labels[position_x + a][position_y].getText();
                                            }
                                            for (int a =1; position_y + a<= 20; a++) {
                                                if ((position_y + a ==20)||(labels[position_x][position_y + a].getText() == "")) {
                                                    VsentPosition_yR = position_y + a - 1;
                                                    VsentPosition_xR = position_x;
                                                    System.out.println("sent positionVR");
                                                    System.out.println(VsentPosition_xR);
                                                    System.out.println(VsentPosition_yR);
                                                    break;
                                                }
                                                Vword = Vword + labels[position_x][position_y + a].getText();
                                            }
                                            for (int a = 1; position_x - a >=-1; a++) {
                                                if ((position_x - a ==-1)||(labels[position_x - a][position_y].getText() == "")) {
                                                    HsentPosition_xL = position_x - a + 1;
                                                    HsentPosition_yL = position_y;
                                                    System.out.println("sent positionHL");
                                                    System.out.println(HsentPosition_xL);
                                                    System.out.println(HsentPosition_yL);
                                                    break;
                                                }
                                                Hword = labels[position_x - a][position_y].getText() + Hword;
                                            }
                                            for (int a = 1; position_y - a >= -1; a++) {
                                                if ((position_y - a ==-1)||(labels[position_x][position_y - a].getText() == "")) {
                                                    VsentPosition_yL = position_y - a + 1;
                                                    VsentPosition_xL = position_x;
                                                    System.out.println("sent positionVL");
                                                    System.out.println(VsentPosition_xL);
                                                    System.out.println(VsentPosition_yL);
                                                    break;
                                                }
                                                Vword = labels[position_x][position_y - a].getText() + Vword;
                                            }
                                            if (Hword == Vword){
                                                Hword ="";
                                            }

                                            ObservableList<String> observableList = FXCollections.observableArrayList(Vword,Hword);
                                            listView.setItems(observableList);
                                            System.out.println(Hword);
                                            System.out.println(Vword);

                                            round++;
                                            confirm.setDisable(true);
                                            undo.setDisable(true);
                                        }
                                    }
                                });
                            }
                            event.setDropCompleted(success);
                            event.consume();
                        }
                    });
                }
                else {
                    System.out.println("this operation is illegal"); //Error message
                }
            }
        }


        /**
         *
         * The action after clicking button Submit
         */

        submit.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String position= "";
                String chooseWords = "";
                ObservableList<String> selectItems = listView.getSelectionModel().getSelectedItems();

                int multi = 1;
                for(Object o : selectItems){
                    chooseWords = chooseWords + o+ "|";
                }

                if(!chooseWords.equals("")) {
                    if(chooseWords.equals("|")){
                        // Alert window
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Warning!");
                        alert.setHeaderText("You do not put a character!");

                        ButtonType confirm = new ButtonType("OK");
                        alert.getButtonTypes().setAll(confirm);
                        alert.showAndWait();
                    }
                    else {
                        if(chooseWords.equals(Hword + "|")){
                            position = HsentPosition_xL + "|" + HsentPosition_xR + "|" + HsentPosition_yL + "|" + HsentPosition_yR;
                        }
                        else if(chooseWords.equals(Vword+ "|")){
                            position = VsentPosition_xL + "|" + VsentPosition_xR + "|" + VsentPosition_yL + "|" + VsentPosition_yR;
                        }
                        if((position_x == 0 || position_x == 19)&&(position_y == 0 || position_y ==19)){
                            multi= 2;
                        }

                        // choose a word first, then write this word to the server
                        String msg1 = "submmit|" + chooseWords +multi + "|" + position;
                        System.out.println(msg1);
                        write(msg1);
                    }
                }else{
                    // Alert window
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Warning!");
                    alert.setHeaderText("You do not choose word!");

                    ButtonType confirm = new ButtonType("OK");
                    alert.getButtonTypes().setAll(confirm);
                    alert.showAndWait();
                }
            }
        });


        /**
         *
         * The action after clicking button Pass
         */

        pass.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override

            public void handle(MouseEvent event) {
                write("pass|");
            }
        });


        /**
         *
         * The action after clicking button Undo
         */

        undo.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                confirm.setDisable(true);
                undo.setDisable(true);
                success = false;
                labels[position_x][position_y].setText("");
                for(int i = 0; i < 13; i++){
                    alphabetF[i].setDisable(false);
                    alphabetS[i].setDisable(false);
                }
            }
        });


        /**
         *
         * The action after clicking button Undo
         */

        quit.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override

            public void handle(MouseEvent event) {
                // Alert window
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Quit");
                alert.setHeaderText("Are you sure to quit the game?");
                alert.setContentText(null);

                ButtonType accept = new ButtonType("Yes");
                ButtonType deny = new ButtonType("Cancel");
                alert.getButtonTypes().setAll(accept, deny);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == accept){
                    System.out.println("watch quit");
                    write("quit|");
                } else if (result.get() == deny) {

                }
            }
        });

        Scene scene = new Scene(gridPane,1500,800);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("background.css").toExternalForm());
        stage.show();
    }
}