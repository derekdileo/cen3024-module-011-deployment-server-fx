package application;
	
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;


public class Server extends Application {
	
	// IO streams to / from Client
	DataInputStream inputFromClient;
	DataOutputStream outputToClient;
	
	// Declare Stage, TextArea, ServerSocket & Socket outside of 
	// start() method so they are accessible to closeProgram() method
	private static Stage window; 
	private static TextArea ta;
	private static ServerSocket serverSocket = null;
	private static Socket socket = null;
	
	@Override // Override the start method in the Application Class
	public void start(Stage primaryStage) {
		
		// Rename stage to window for sanity
		window = primaryStage;
		
		// Text area for displaying contents
		ta = new TextArea();
		
		// Create a scene and place it in the stage
		Scene scene = new Scene(new ScrollPane(ta), 450, 200);
		window.setTitle("Server");
		window.setScene(scene);
		window.show();
		
		// Handle close button request. 
		// Launch ConfirmBox to confirm if user wishes to quit
		window.setOnCloseRequest(e -> {
			// Consume the event to allow closeProgram() to do its job
			e.consume();
			closeProgram();
		});
		
		// Create and start an anonymous Thread to handle input from Client
		new Thread( () -> {
			
			try {
				
				// Create a server socket
				serverSocket = new ServerSocket(8000);
				
				// Print start time to Server TextArea
				Platform.runLater(() -> {
					ta.appendText("Server started at " + new Date() + '\n');
				});
				
				// Listen for a connection request
				socket = serverSocket.accept();
				
				// Initialize data input and output streams
				inputFromClient = new DataInputStream(socket.getInputStream());
				outputToClient = new DataOutputStream(socket.getOutputStream());
				
				while(true) {
					// Receive integer input from the Client
					int value = inputFromClient.readInt();
					
					// Determine if value is prime
					String result = isPrime(value);
					
					// Send result back to client
					outputToClient.writeUTF(result);
					
					// Print value and results to Server TextArea
					Platform.runLater(() -> {
						ta.appendText("\nInteger received from client: " + value + '\n');
						ta.appendText(result);
					});
					
				}
				
			} catch(IOException ex) {
				ta.appendText("Error in Server start(): " + ex.getMessage());
				ex.printStackTrace();
			}
			
		}).start();
		
	}
	
	/** A method to check if a user-selected number is prime. 
	 * @param num is the input from user */
	public static String isPrime(int num) {
		
		// Edge case
		if(num <= 1) {
			return num + ": is NOT a prime number.";
		}
		
		// Check from 2 to n-1
		for (int i = 2; i < num; i++) {
			if (num % i == 0) {
				return num + ": is NOT a prime number.";
			}
		}
		
		return num + ": IS a prime number.";
		
	}
	
	/* closeProgram() Method uses ConfirmBox class to confirm if user wants to quit */
	private static void closeProgram() {
		Boolean answer = ConfirmBox.display("", "Are you sure you want to quit?");
		
		if(answer) {
			// Close sockets and window, then exit.
			try {
				ta.appendText("Window Closed!");
				serverSocket.close();
				socket.close();
				window.close();			
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			
		}
		
	}
	
	/** Main method calls launch() to start JavaFX GUI.
	 * @param args mandatory parameters for command line method call */
	public static void main(String[] args) {
		launch(args);
	}
	
}
