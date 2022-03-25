package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServeurJeu extends Thread {
	private boolean isActive=true;
	private int nombreClient=0;
	private int nombreSecret;
	private boolean fin;
	private String gagnant;
	
	public static void main(String[] args) {
		new ServeurJeu().start();
		//System.out.println("Suite de l'application");
	}
	@Override 
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(1234);
			nombreSecret = new Random().nextInt(1000);
			System.out.println("Le serveur a choisi le nombre secret suivant : "+nombreSecret);
			while(isActive) {
				Socket socket = ss.accept();
				++nombreClient;
				new Conversation(socket,nombreClient).start();
			}
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	class Conversation extends Thread{
		private Socket socket;
		private int nombreClient;
		public Conversation(Socket s, int nombreClient) {
			this.socket = s;
			this.nombreClient=nombreClient;
		}
		@Override //////////////////
		public void run() {
			try {
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				OutputStream os = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);
				String IP = socket.getRemoteSocketAddress().toString();
				System.out.println("Connexion de client numero: "+nombreClient+" IP= "+IP);
				pw.println("Bienvenu vous etes le client numero"+nombreClient);
				pw.println("Deviner le nombre secret.....?");
				while(true) {
					String req = br.readLine();
					int nombre=0;
					boolean correctFormatRequest = false;
					try {
						nombre = Integer.parseInt(req);
						correctFormatRequest = true;
					} catch (NumberFormatException e ) {
						correctFormatRequest = false;
						
					}
					if (correctFormatRequest) {
	
					System.out.println("Client "+ IP+ "Tentative avec le nombre: "+ nombre);
					if (fin == false) {
						if(nombre>nombreSecret) {
							pw.println("\nVotre nombre est superieur au nombre secret");
						}
						else if(nombre<nombreSecret) {
							pw.println("\nVotre nombre est inferieur au nombre secret");
						}
						else {
							pw.println("\nBRAVO! vous avez gagné");
							gagnant = IP;
							System.out.println("BRAVO au gagnant, IP Client : "+gagnant);
							fin = true;
						}
					} else {
							pw.println("Jeu termine, le gagnat est :"+gagnant);
					}
					}else {
						pw.println("Format de nombre incorrect");
					}
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
