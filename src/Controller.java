import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.NumberFormatException;

public class Controller {

    private Integer cport;
    private Integer R;
    private Integer timeout;
    private Integer rebalance_period;

    private ServerSocket socket;

    private ArrayList<ClientConnection> clients;
    private ArrayList<DstoreConnection> dstores;

    public Controller(Integer cport, Integer R, Integer timeout, Integer rebalance_period) throws Exception {
        this.cport = cport;
        this.R = R;
        this.timeout = timeout;
        this.rebalance_period = rebalance_period;

        this.socket = new ServerSocket(this.cport);

        this.clients = new ArrayList<ClientConnection>();
        this.dstores = new ArrayList<DstoreConnection>();
    }

    public void addClient(ClientConnection c) {
        System.out.println("(i) New client detected");
        this.clients.add(c);
        new Thread(c).start();
    }

    public void addDstore(DstoreConnection c) {
        System.out.println("(i) New dstore detected");
        this.dstores.add(c);
        new Thread(c).start();
    }

    public Socket await() throws Exception {
        return socket.accept();
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Error: invalid amount of command line arguments provided");
            return;
        }

        Integer cport;
        Integer R;
        Integer timeout;
        Integer rebalance_period;
        try {
            cport = Integer.parseInt(args[0]);
            R = Integer.parseInt(args[1]);
            timeout = Integer.parseInt(args[2]);
            rebalance_period = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("Error: unable to parse command line arguments");
            return;
        }

        Controller controller;
        try {
            controller = new Controller(cport, R, timeout, rebalance_period);
        } catch (Exception e) {
            System.out.println("Error: socket creation failed\n    (!) " + e.getMessage());
            return;
        }

        while (true) {
            try {
                new Thread(new IdentifyConnection(controller, controller.await())).start();
                System.out.println("(i) New connection");
            } catch (Exception e) {
                System.out.println("Error: unable to accept client connection\n    (!) " + e.getMessage());
            }
        }
    }
}
