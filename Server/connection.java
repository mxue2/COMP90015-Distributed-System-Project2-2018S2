package Server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * 948602 Xinwei Luo
 * 927096 Lixuan Ding
 * 950214 Lei REN
 * 897082 Min XUE
 */

public class connection extends Thread {
    private  Socket clientSocket;
    private  Scanner in;
    private  PrintStream out;
    private  static int multi=1;


    /**
     * A constructor that can transform socket as a parameter.
     */

    public connection(Socket soc) {
        try {
            this.clientSocket = soc;
            in = new Scanner(clientSocket.getInputStream());
            System.out.println(in);
            out = new PrintStream(clientSocket.getOutputStream());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * override run().
     */

    public void run() {
        try {
            while (true) {
                String command = in.nextLine();
                System.out.println(command);
                String rsp = execute(command);
                out.println(rsp);
            }
        } catch (Exception e) {
            System.out.println(getName() + " closed");
        }
    }


    /**
     * Execute the command and return the result string.
     * @throws IOException
     */

    public  String execute(String command) throws IOException {
        String res = "invalid message!";
        String[] parts = command.split("\\|");

        if (parts.length < 1) {
            return res = "Error: Bad command " + command;
        }
        String op = parts[0];
        if(op.equals("validate")){
            res = valide(parts,clientSocket);
        }
        else if(op.equals("position")){
            res = addChar(parts);
        }
        else if (op.equals("invite")) {
            System.out.println("invite successful");
            System.out.println(command);
            if(Server.Start) {
                res = "notinvite|";
            }else {
                invite(parts);
            }
        }
        else if (op.equals("inviteResponse")) {
            inviteRes(parts);
        }
        else if(op.equals("startstart")){
            StartGame();
        }
        else if(op.equals("submmit")) {
            System.out.println(command);
            submmit(parts);
        }
        else if(op.equals("voteresponse") ){
            voteRes(parts);
        }
        else if(op.equals("pass") ) {
            res = pass();
        }
        else if(op.equals("quit")){

            GameOver();
        }
        else if(op.equals("updateOnePlayer")){
            res = updateOnePlayer();
        }else if(op.equals("watch")){
            watchGame();
        }else if(op.equals("remove")){
            remove();
        }
        return res+"\n";
    }


    /**
     *
     * Validate whether username is unique or not
     */

    public String valide(String[] parts,Socket soc) {
        String res = "Name successful|"+parts[1];

        if (Server.Player.containsKey(parts[1])) {
            res = "duplicate name";
        }else {
            Server.Player.put(parts[1], clientSocket);
            Server.Player2.put(clientSocket,parts[1]);
        }
        return res;
    }


    /**
     *
     * If one user quit the MainPage, then remove his name from the list of online users
     */

    public void  remove(){
        String name;
        name = Server.Player2.get(clientSocket);
        Server.Player.remove(name);
        Server.Player2.remove(clientSocket);
        System.out.println("The client has been removed!");
    }


    /**
     *
     * Invite one player
     */

    public synchronized void invite(String [] parts) throws IOException {
        String inviter = Server.Player2.get(clientSocket);
        Socket invite;
        Server.nowPlayer.put(inviter,clientSocket);
        //Server.counter++;

        for(int i = 1;i < parts.length;i++) {
            invite = Server.Player.get(parts[i]);
            PrintStream outDic = null;
            if (inviter.equals(parts[i])) {
                try {
                    outDic = new PrintStream(clientSocket.getOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                outDic.println("inviter"
                        + "|"
                        + "You are the inviter!");
            } else {
                try {
                    outDic = new PrintStream(invite.getOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                outDic.println("invite"
                        + "|"
                        + inviter
                        + "|");
            }
        }
    }


    /**
     *
     * Get response from the invited users
     */

    public void inviteRes(String[] parts) {
        String name = Server.Player2.get(clientSocket);
        String result = parts[1];
        String Inviter = parts[2];
        Server.ResponseCounter++;
        System.out.println(Inviter);
        System.out.println(name);

        if(result.equals("accept")) {
            if(!Server.Start) {
               // Server.counter++;
                String allname = "accept";
                Server.nowPlayer.put(name, clientSocket);
                for (String key : Server.nowPlayer.keySet()) {
                    allname = allname + "|" + key;
                }
                for (String key : Server.nowPlayer.keySet()) {
                    Socket inviter = Server.nowPlayer.get(key);
                    PrintStream outDic = null;
                    try {
                        outDic = new PrintStream(inviter.getOutputStream());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    outDic.println(allname);
                }
            }else{
                String res = "notaccept|";
                PrintStream outDic = null;
                try {
                    outDic = new PrintStream(clientSocket.getOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                outDic.println(res);
            }
        }
    }


    /**
     *
     * Start a game
     */

    public void StartGame() {
        String res = "invalid message!";
        if(Server.Start == false) {
            String name;
            name=Server.Player2.get(clientSocket);

            if(Server.nowPlayer.size() == 0) {
                Server.nowPlayer.put(name, clientSocket);
                //Server.counter++;
            }
            else if(!Server.nowPlayer.containsKey(name)){
                Server.nowPlayer.clear();
                //Server.counter = 0;
                Server.nowPlayer.put(name, clientSocket);
                //Server.counter++;
            }

            Server.Start = true;
            namelist();
            initialScore();
            taketurn();
            res = "startstart|"+Server.turnName;
            for (String key : Server.nowPlayer.keySet()) {
                String res2;
                if(key.equals(Server.turnName)){
                    res2 = res+"|open|"+key;
                    Socket player;
                    player = Server.Player.get(key);
                    PrintStream outDic = null;
                    try {
                        outDic = new PrintStream(player.getOutputStream());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    outDic.println(res2);
                }
                else{
                    res2 = res+"|close|"+key;
                    Socket player;
                    player = Server.Player.get(key);
                    PrintStream outDic = null;
                    try {
                        outDic = new PrintStream(player.getOutputStream());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    outDic.println(res2);
                }
            }
        }
        else {
            res = "notstart| A game has been started.";
            PrintStream outDic = null;
            try {
                outDic = new PrintStream(clientSocket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            outDic.println(res);
        }
    }


    /**
     *
     * Deny the invitation
     */

   public String denyAccept(){
        String res = "invalid message!";

        if(Server.nowPlayer.size() == 0) {
            res = "deny|";
        }
        return res;
    }


    /**
     *
     * Watch mode
     */

    public void watchGame(){
        String res = "invalid message!";
        if(!Server.Start) {
            res = "nowatch|no game started";
            PrintStream outDic = null;
            try {
                outDic = new PrintStream(clientSocket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            outDic.println(res);
        }
        else{
            String name = Server.Player2.get(clientSocket);
            Server.watchPlayer.put(name,clientSocket);
            res = "watch"+"|"+Server.turnName+"|"+name;
            PrintStream outDic = null;
            try {
                outDic = new PrintStream(clientSocket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            outDic.println(res);
            updateWatchGame();
        }
    }


    /**
     *
     * Add a character
     */

    public String addChar(String[] parts) {
        String res = "invalid message!";
        Server.PassCounter=0;
        String name = Server.Player2.get(clientSocket);
        if(Server.turnName!=name) {
            res = "notturn|not your turn|"+parts[1]+"|"+parts[2];
        }
        else if(parts.length == 4){
            int i = Integer.valueOf(parts[1]);
            int j = Integer.valueOf(parts[2]);
            String s = parts[3];
            Server.game[i][j] = s;
            updateGame(parts);
        }
        return res;
    }


    /**
     *
     * After submmitting a word, then send a vote window to all players
     */

    public void submmit(String[] parts) {
        String name = Server.Player2.get(clientSocket);
        String word = parts[1];
        multi = Integer.valueOf(parts[2]);
        String pos = parts[3]+"|"+parts[4]+"|"+parts[5]+"|"+parts[6]+"|";
        System.out.println("the multi" + multi);
        String res = "vote|"
                +name
                +"|"
                +word+"|"+pos;

        for (String key : Server.nowPlayer.keySet()) {
            Socket player;
            player = Server.Player.get(key);
            PrintStream outDic = null;
            try {
                outDic = new PrintStream(player.getOutputStream());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            outDic.println(res);
        }
        for (String key : Server.watchPlayer.keySet()) {
            String res2 = "watchmode|"
                    +name
                    +"|"
                    +word+"|"+pos;
            Socket player;
            player = Server.Player.get(key);
            PrintStream outDic = null;
            try {
                outDic = new PrintStream(player.getOutputStream());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            outDic.println(res2);
        }
    }


    /**
     *
     * Get voting response from all players
     */

    public void voteRes(String[] parts) {
        String voter = Server.Player2.get(clientSocket);
        String name = Server.turnName;
        String word = parts[1];
        String result = parts[2];
        int sco = 0;
        Server.AllVoter.add(voter);

        if(result.equals("agree")) {
            Server.Voter.add(voter);
        }
        if(Server.AllVoter.size() == Server.nowPlayer.size()) {
            if (Server.Voter.size() == Server.nowPlayer.size()) {
                sco = score(word, result);
            }
            updateOneScore(name, sco);
            updatePlayer();
            Server.AllVoter.clear();
            Server.Voter.clear();
        }
    }


    /**
     *
     * Calculate score for wach turn
     */

    public int score(String word,String result) {
        int sco = 0;
        if(result.equals("agree")) {
            sco = multi*word.length();
        }
        return sco;
    }


    /**
     *
     * One player passes his turn, other player plays the game.
     * All players pass, then game over.
     */

    public String pass() {
        Server.PassCounter ++;
        String res = "invalid message!";
        if(Server.PassCounter!=Server.nowPlayer.size()) {
            updatePlayer();
        }
        else {
            Server.Start = false;
            GameOver();
        }
        return res;
    }


    /**
     *
     * Game over, then initialize the game status
     */

    public void clear(){

        Server.nowPlayer.clear();
        Server.watchPlayer.clear();
        Server.Score.clear();
        Server.Voter.clear();
        Server.AllVoter.clear();
       // Server.counter = 0;
        Server.Number = 0;
        Server.ResponseCounter = 0;
        Server.PassCounter = 0;
        Server.turnName = null;
        for(int i = 0;i < 10000;i++){
            Server.nameList[i] = null;
        }
        for(int i = 0;i < 20;i++){
            for(int j= 0;j < 20;j++){
                Server.game[i][j] = "";
            }
        }
    }


    /**
     *
     * Game over, then show ranking window
     */

    public synchronized void GameOver() {
        String name = Server.Player2.get(clientSocket);
        String rank = ranking();
        String res = "game over|" + rank;

        if(Server.watchPlayer.containsKey(name)){
            PrintStream outDic = null;
            try {
                outDic = new PrintStream(clientSocket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            outDic.println(res);
            Server.watchPlayer.remove(name);
        }
        else {
            Server.Start=false;
            for (String key : Server.nowPlayer.keySet()) {
                Socket player;
                player = Server.Player.get(key);
                PrintStream outDic = null;
                try {
                    outDic = new PrintStream(player.getOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                outDic.println(res);
                System.out.println(res);
            }
            for (String key : Server.watchPlayer.keySet()) {
                Socket player;
                player = Server.Player.get(key);
                PrintStream outDic = null;
                try {
                    outDic = new PrintStream(player.getOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                outDic.println(res);
            }
            clear();
        }
    }


    /**
     *
     * Update one player
     */

    public String updateOnePlayer(){
        String res = "updateOnePlayer";
        for(String key:Server.Player.keySet()) {
            res = res+"|"+key;
        }
        PrintStream outDic = null;
        try {
            outDic = new PrintStream(clientSocket.getOutputStream());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        outDic.println(res);
        return res;
    }


    /**
     *
     * Update one score
     */

    public synchronized void updateOneScore(String name,int sco) {

        int now = Server.Score.get(name);
        System.out.println(now);
        Server.Score.remove(name);
        now = now+sco;
        Server.Score.put(name, now);
        String res = "updateOneScore";

        for(String key:Server.nowPlayer.keySet()){
            res = res
                    +"|"
                    +key
                    +" gets "
                    +String.valueOf(Server.Score.get(key));
        }
        for (String key : Server.nowPlayer.keySet()) {
            Socket player;
            player = Server.Player.get(key);
            PrintStream outDic = null;
            try {
                outDic = new PrintStream(player.getOutputStream());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            outDic.println(res);
        }
        for (String key : Server.watchPlayer.keySet()) {
            Socket player;
            player = Server.Player.get(key);
            PrintStream outDic = null;
            try {
                outDic = new PrintStream(player.getOutputStream());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            outDic.println(res);
        }

    }


    /**
     *
     * Show the ranking result
     */

    public String ranking() {
        String res = "";
        int n = Server.nowPlayer.size();
        String[] rank = new String[n];
        int i = 0;

        for(String key:Server.nowPlayer.keySet()) {
            rank[i] = key;
            i++;
        }
        for(i = 0;i < n;i++)
            for(int j = i + 1;j < n;j++) {
                if(Server.Score.get(rank[i])<Server.Score.get(rank[j])) {
                    String m = rank[i];
                    rank[i] = rank[j];
                    rank[j] = m;
                }
            }
        res=
                rank[0]
                        +"|"
                        +String.valueOf(Server.Score.get(rank[0]));
        return res;
    }


    /**
     *
     * Initialize the score
     */

    public synchronized void initialScore() {

        for(String key:Server.nowPlayer.keySet()) {
            Server.Score.put(key, 0);
        }
        for(String key:Server.nowPlayer.keySet()) {
            System.out.println(Server.Score.get(key));
        }
    }

    /**
     *
     * Update the game
     */

    public synchronized void updateGame(String []parts) {
        String res = "updateGame";
        res = res
                +"|"
                +parts[1]
                +"|"
                +parts[2]
                +"|"
                +parts[3];

        for (String key : Server.nowPlayer.keySet()) {
            Socket player;
            player = Server.Player.get(key);
            PrintStream outDic = null;
            try {
                outDic = new PrintStream(player.getOutputStream());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            outDic.println(res);
        }
        for (String key : Server.watchPlayer.keySet()) {
            Socket player;
            player = Server.Player.get(key);
            PrintStream outDic = null;
            try {
                outDic = new PrintStream(player.getOutputStream());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            outDic.println(res);
        }
    }


    /**
     *
     * Update watch mode
     */

    public void updateWatchGame() {
        for(int i = 0;i < 20;i++)
            for(int j = 0;j < 20;j++) {
                if(Server.game[i][j] != "") {
                    String res = "updateGame"
                            +"|"
                            +Integer.toString(i)
                            +"|"
                            +Integer.toString(j)
                            +"|"
                            +String.valueOf(Server.game[i][j]);
                    PrintStream outDic = null;
                    try {
                        outDic = new PrintStream(clientSocket.getOutputStream());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    outDic.println(res);
                }
            }
    }


    /**
     *
     * Update players
     */

    public synchronized void updatePlayer() {
        String res = "updatePlayer";
        taketurn();
        res = res
                +"|"
                +Server.turnName;
        for (String key : Server.nowPlayer.keySet()) {
            String res2;
            if (key.equals(Server.turnName)) {
                res2 = res + "|open";
                Socket player;
                player = Server.Player.get(key);
                PrintStream outDic = null;
                try {
                    outDic = new PrintStream(player.getOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                outDic.println(res2);
            } else {
                res2 = res + "|close";
                Socket player;
                player = Server.Player.get(key);
                PrintStream outDic = null;
                try {
                    outDic = new PrintStream(player.getOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                outDic.println(res2);
            }
        }
        for (String key : Server.watchPlayer.keySet()) {
            Socket player;
            res = res+"|close";
            player = Server.Player.get(key);
            PrintStream outDic = null;
            try {
                outDic = new PrintStream(player.getOutputStream());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            outDic.println(res);
        }
    }


    /**
     *
     * The name list of players
     */

    public void namelist() {


        int i = 0;
        for(String key:Server.nowPlayer.keySet()) {
            Server.nameList[i] = key;
            i++;
        }
    }


    /**
     *
     * All players take turns to play the game
     */

    public void taketurn() {
        String name = null;
        int count=Server.nowPlayer.size();
        int i = Server.Number % count;
        name = Server.nameList[i];
        Server.turnName = name;
        Server.Number++;
    }
}