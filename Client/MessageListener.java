package Client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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

public class MessageListener extends Thread {
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private static Socket socket;


    /**
     *
     * Initialize socket
     */

    public void init(Socket socket, BufferedWriter writer, BufferedReader reader){
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MessageListener messageListener;

    public static MessageListener getInstance(){
        if(messageListener == null){
            messageListener = new MessageListener();
        }
        return messageListener;
    }


    /**
     *
     * Read message from the server
     */

    @Override

    public void run() {
        while (true) {
            try {
                String message = null;
                while ((message = bufferedReader.readLine()) != null) {

                    System.out.println(message);
                    execute(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     *
     * Execute operations
     */

    public static void execute(String command) {
        String[] parts=command.split("\\|");
        String op=parts[0];
        if(op.equals("invalid message!")) {

        }
        else if(op.equals("duplicate name")){
            Platform.runLater(new Runnable() {
                @Override

                public void run() {
                    // Alert window
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning Message");
                    alert.setHeaderText("Invalid Username!");
                    alert.setContentText("The username has already existed!");
                    alert.showAndWait();
                    IdentifyUsername.getInstance().textField1.setText("");
                }
            });
        }
        else if (op.equals("Name successful")){
            Platform.runLater(new Runnable() {
                @Override

                public void run() {
                    // Verify user name successfully, then close IdentifyUsername stage and open MainPage stage
                    IdentifyUsername.getInstance().closeStage();
                    MainPage.getInstance().init(socket,bufferedWriter,bufferedReader);
                    MainPage.getInstance().step2();
                    MainPage.getInstance().label.setText(parts[1]+"'s Main Page");
                }
            });
        }
       else if(op.equals("watchmode")){
            watchmode(parts);
        }
        else if(op.equals("updatePlayer")) {
            updatePlayer(parts);
        }
        else if(op.equals("notinvite")){
            notinvite();
        }
        else if(op.equals("startstart")){
            start(parts);
        }
        else if(op.equals("invite")) {
            invite(parts);
        }
        else if(op.equals("accept")) {
            inviteAccept(parts);
        }
        else if(op.equals("notstart")) {
            notstart(parts);
        }
        else if(op.equals("notturn")) {

        }
        else if(op.equals("deny")) {
            inviteDeny(parts);
        }
        else if(op.equals("vote")) {
            vote(parts);
        }
        else if(op.equals("updateGame")) {
            updateGame(parts);
        }else if(op.equals("updateOneScore")){
            updateOneScore(parts);
        }
        else if(op.equals("game over")) {
            Rank(parts);
        }
        else if(op.equals("watch")){
            watch(parts);
        }
        else  if(op.equals("nowatch")){
            nowatch();
        }else if(op.equals("notaccept")){
            notaccept();
        }
        else if(op.equals("inviter")){
            Platform.runLater(new Runnable() {
                @Override

                public void run() {
                    // Alert window
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Warning");
                    alert.setHeaderText(parts[1]);

                    ButtonType confirm = new ButtonType("Ok");
                    alert.showAndWait();
                }
            });
        }
    }


    /**
     *
     * Initialize watch mode
     */

    public static void watch(String[] parts){

        Platform.runLater(new Runnable() {
            @Override

            public void run() {
                Game.getInstance().init(socket,bufferedWriter,bufferedReader);
                Game.getInstance().startAGame();
                MainPage.getInstance().closeStage();
                Game.getInstance().pass.setDisable(true);
                Game.getInstance().confirm.setDisable(true);
                Game.getInstance().undo.setDisable(true);
                Game.getInstance().submit.setDisable(true);
                Game.getInstance().quit.setText("WatchQuit");

                for(int i = 0; i < 13; i++){
                    Game.getInstance().alphabetF[i].setDisable(true);
                    Game.getInstance().alphabetS[i].setDisable(true);
                }
                Game.getInstance().display.setText(parts[1] + "'s turn to play"); // abc's turn to play--- update abc
                Game.getInstance().label.setText(parts[2] + "'s Scrabble Game");
            }
        });
    }


    /**
     *
     * Watch mode
     */

    public static void watchmode(String [] parts){

        for(int i = Integer.valueOf(parts[3]); i <=Integer.valueOf(parts[4]); i++){
            for (int j = Integer.valueOf(parts[5]); j <= Integer.valueOf(parts[6]); j++){
                Game.getInstance().labels[i][j].setStyle("-fx-background-color: #fcff1f;" +
                        "-fx-border-width:  0.2;" +
                        "-fx-border-insets: 1;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-color: black;");
            }
        }
    }


    /**
     *
     * No game is processing
     */

    public static void nowatch(){

        Platform.runLater(new Runnable() {
            @Override

            public void run() {
                // Alert window
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning");
                alert.setHeaderText("No game is existed in process!!!!!");

                ButtonType confirm = new ButtonType("Ok");
                alert.getButtonTypes().setAll(confirm);
                alert.showAndWait();
            }
        });
    }


    /**
     *
     * Error message during the process of invitation
     */

    public static void notinvite(){

        Platform.runLater(new Runnable() {
            @Override

            public void run() {

                // Alert window
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning!");
                alert.setHeaderText("You cannot invite anyone!");

                ButtonType confirm = new ButtonType("OK");
                alert.getButtonTypes().setAll(confirm);
                alert.showAndWait();
            }
        });
    }


    /**
     *
     * Error message during the process of start a game
     */

    public static void notaccept(){
        System.out.println("not accept function");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Alert window
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning!");
                alert.setHeaderText("A game has already started!");

                ButtonType confirm = new ButtonType("OK");
                alert.getButtonTypes().setAll(confirm);
                alert.showAndWait();
            }
        });
    }


    /**
     *
     * Game stage after changing into another player
     */

    public static void updatePlayer(String[] parts) {

        Platform.runLater(new Runnable() {
            @Override

            public void run() {
                for(int i = 0; i < Game.getInstance().labels.length; i++){
                    for (int j = 0; j < Game.getInstance().labels[i].length; j++){
                        if((i == 0 || i == 19)&&(j == 0 || j ==19)){
                            Game.getInstance().labels[i][j].setStyle("-fx-background-color: #47d6ff;" +
                                    "-fx-border-width:  0.2;" +
                                    "-fx-border-insets: 1;" +
                                    "-fx-border-radius: 5;" +
                                    "-fx-border-color: black;");
                        }
                        else {
                            Game.getInstance().labels[i][j].setStyle("-fx-background-color: white;" +
                                    "-fx-border-width:  0.2;" +
                                    "-fx-border-insets: 1;" +
                                    "-fx-border-radius: 5;" +
                                    "-fx-border-color: black;");
                        }}
                }
                for(int i = 0; i< 13; i++){
                    Game.getInstance().alphabetF[i].setDisable(false);
                    Game.getInstance().alphabetS[i].setDisable(false);
                }
                Game.getInstance().submit.setDisable(false);
                Game.getInstance().pass.setDisable(false);
                String turnName=parts[1];
                Game.getInstance().init(socket,bufferedWriter,bufferedReader);
                Game.getInstance().display.setText(turnName + "'s turn to play"); // abc's turn to play--- update abc

                if(parts[2].equals("close")){
                    Game.getInstance().confirm.setDisable(true);
                    Game.getInstance().undo.setDisable(true);
                    Game.getInstance().submit.setDisable(true);
                    Game.getInstance().pass.setDisable(true);
                    for(int i = 0; i <13; i++){
                        Game.getInstance().alphabetF[i].setDisable(true);
                        Game.getInstance().alphabetS[i].setDisable(true);
                    }
                }
                else if(parts[2].equals("open")){

                }
            }
        });
    }


    /**
     *
     * Invite players
     */

    public static void invite(String[] parts) {

        String inviter=parts[1];
        Platform.runLater(new Runnable() {
            @Override

            public void run() {
                MainPage.getInstance().init(socket,bufferedWriter,bufferedReader);

                // Alert window
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invite");
                alert.setHeaderText(inviter + " invites you to join a scrabble game!");
                alert.setContentText("Would you like to accept?");

                ButtonType accept = new ButtonType("Accept");
                ButtonType deny = new ButtonType("Deny");
                alert.getButtonTypes().setAll(accept, deny);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == accept){
                    MessageListener.getInstance().write("inviteResponse|accept|"+inviter);
                } else if (result.get() == deny) {
                    MessageListener.getInstance().write("inviteResponse|deny|"+inviter);
                }
            }
        });

    }


    /**
     *
     * Accept the invitation
     */

    public static void inviteAccept(String[] parts) {

        String a ="";
        for (int i = 1; i<parts.length;i++){
            a = a + parts[i] + "\n";
        }
        MainPage.getInstance().init(socket,bufferedWriter,bufferedReader);
        MainPage.getInstance().textArea.setText(a);
    }


    /**
     *
     * Deny the invitation
     */

    public static void inviteDeny(String[] parts) {
        System.out.println(428+"inviteDeny function");
        System.out.println("deny");
        Platform.runLater(new Runnable() {
            @Override

            public void run() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Error Message");
                alert.setHeaderText("No one joins the game");

                ButtonType confirm = new ButtonType("OK");
                alert.getButtonTypes().setAll(confirm);
                alert.showAndWait();
            }
        });
    }

    /**
     *
     * Start a game
     */

    public static void start(String[] parts) {

        Platform.runLater(new Runnable() {
            @Override

            public void run() {
                MainPage.getInstance().closeStage();
                String turnName=parts[1];
                Game.getInstance().init(socket,bufferedWriter,bufferedReader);
                // Game game = new Game();
                Game.getInstance().startAGame();
                Game.getInstance().display.setText(turnName + "'s turn to play"); // abc's turn to play--- update abc
                Game.getInstance().label.setText(parts[3] + "'s Scrabble Game");
                if(parts[2].equals("close")){
                    Game.getInstance().confirm.setDisable(true);
                    Game.getInstance().undo.setDisable(true);
                    Game.getInstance().submit.setDisable(true);
                    Game.getInstance().pass.setDisable(true);
                    for(int i = 0; i <13; i++){
                        Game.getInstance().alphabetF[i].setDisable(true);
                        Game.getInstance().alphabetS[i].setDisable(true);
                    }
                }
                else if(parts[2].equals("open")){

                }

            }
        });
    }


    /**
     *
     * Error message during the process of start a game
     */

    public static void notstart(String[] parts) {

        String s=parts[1];

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Message");
                alert.setHeaderText("You can't start a game");
                alert.setContentText("A game has been started.");
                alert.showAndWait();
            }
        });
    }


    /**
     *
     * Vote for the chosen word
     */

    public static void vote(String[] parts) {

        String name =parts[1];
        String word =parts[2];
        Platform.runLater(new Runnable() {
            @Override

            public void run() {
                MessageListener.getInstance().init(socket,bufferedWriter,bufferedReader);

                for(int i = Integer.valueOf(parts[3]); i <=Integer.valueOf(parts[4]); i++){
                    for (int j = Integer.valueOf(parts[5]); j <= Integer.valueOf(parts[6]); j++){
                        Game.getInstance().labels[i][j].setStyle("-fx-background-color: #fcff1f;" +
                                "-fx-border-width:  0.2;" +
                                "-fx-border-insets: 1;" +
                                "-fx-border-radius: 5;" +
                                "-fx-border-color: black;");
                    }
                }

                // Alert window
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Vote");
                alert.setHeaderText(name + " chooses " + word);
                alert.setContentText("Do you agree this is a word?");

                ButtonType agree = new ButtonType("Agree");
                ButtonType disagree = new ButtonType("Disagree");
                alert.getButtonTypes().setAll(agree, disagree);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == agree){
                    MessageListener.getInstance().write("voteresponse|"+word+"|agree");
                } else if (result.get() == disagree) {
                    MessageListener.getInstance().write("voteresponse|"+word+"|disagree");
                }
                ObservableList<String> observableList = FXCollections.observableArrayList("","");
                Game.getInstance().listView.setItems(observableList);
            }
        });
    }


    /**
     *
     * Update the score for each turn
     */

    public static void updateOneScore(String[] parts) {

        String a ="";
        for (int i = 1; i< parts.length; i++){
            a= a+parts[i]+" scores \n";
        }
        System.out.println(a);
        Game.getInstance().init(socket,bufferedWriter,bufferedReader);
        Game.getInstance().textArea.setText(a);
    }


    /**
     *
     * Concurrent game
     */

    public static void updateGame(String[] parts) {
        Platform.runLater(new Runnable() {
            @Override

            public void run() {
                int x =Integer.valueOf(parts[1]) ;
                int y =Integer.valueOf(parts[2]) ;
                String content = parts[3];
                Game.getInstance().labels[x][y].setText(content);
            }
        });
    }


    /**
     *
     * All the players' rank
     */

    public static void Rank(String[] parts) {

        Platform.runLater(new Runnable() {
            @Override

            public void run() {
                // Alert window
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Ranking!");
                alert.setHeaderText("The winner is " + parts[1]);
                alert.setContentText("The score is " + parts[2]);

                ButtonType confirm = new ButtonType("OK");
                alert.getButtonTypes().setAll(confirm);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == confirm){
                    Game.getInstance().stage.close();
                    MainPage.getInstance().stage.show();
                    MainPage.getInstance().textArea.setText("");
                    Game.getInstance().clear();
                }
            }
        });
    }
}