package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 *
 * 948602 Xinwei Luo
 * 927096 Lixuan Ding
 * 950214 Lei REN
 * 897082 Min XUE
 */
public class Server {

    public static Hashtable<String, Socket> Player = new Hashtable<String, Socket>();
    public static Hashtable<Socket, String> Player2 = new Hashtable<Socket, String>();
    public static Hashtable<String, Socket> nowPlayer = new Hashtable<String, Socket>();
    public static Hashtable<String, Socket> watchPlayer=new Hashtable<String, Socket>();
    public static Hashtable<String,Integer> Score = new Hashtable<String,Integer>();
    public static ArrayList<String> Voter = new ArrayList<String>();
    public static ArrayList<String> AllVoter = new ArrayList<String>();
    public static boolean Start=false;
    public static String[][] game = new String[20][20];
    //public static int counter=0;
    public static int Number=0;
    public static int ResponseCounter=0;
    public static int PassCounter=0;
    public static String turnName=null;
    public static String[] nameList=new String[10000];

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        int port = Integer.valueOf(args[0]);
        for(int i = 0;i < 20;i++)
            for(int j = 0;j < 20;j++)
                game[i][j] = "";

        for(int i = 0;i < 10000;i++){
            nameList[i]=null;
        }

        // Establish socket
        ServerSocket listen = null;
        try {
            listen = new ServerSocket(port);
            System.out.println(Thread.currentThread().getName()
                    + "-server listening on port " + port
                    + " for a connection");
            int clientNum = 0;
            InetAddress address = InetAddress.getLocalHost();
            System.out.println("IP Address: " + address.getHostAddress());
            while (true) {
                Socket lis1 = listen.accept();
                System.out.println(Thread.currentThread().getName()
                        + "-Client connection accepted");
                clientNum++;
                connection con = new connection(lis1);
                con.setName("Thread" + clientNum);
                con.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (listen != null) {
                try {
                    listen.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}