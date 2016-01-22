package application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Server extends Application {
	Database files;
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Button quit = new Button("quit");
			files = new Database();
			quit.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent e) {
					try {
						files.toFile();
					} catch (FileNotFoundException | UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.exit(0);
				}
			});
			root.setCenter(quit);
			addFileServer();
			searchFileServer();
			fileRequested();
			Scene scene = new Scene(root,100,100);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void addFileServer() {
		class AddFile implements Runnable {

			public void run() {
				DatagramSocket datagramSocket = null;
				try {
					datagramSocket = new DatagramSocket(Configuration.ADD_FILE_PORT);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] byteResponse = null;
				try {
					byteResponse = "OK".getBytes("utf8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while (!Thread.currentThread().isInterrupted()) {
					DatagramPacket reclievedPacket
					= new DatagramPacket( new byte[Configuration.BUFFER_SIZE], Configuration.BUFFER_SIZE);

					try {
						datagramSocket.receive(reclievedPacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					int length = reclievedPacket.getLength();
					String message = "";
					try {
						message = new String(reclievedPacket.getData(), 0, length, "utf8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					InetAddress address = reclievedPacket.getAddress();
					int port = reclievedPacket.getPort();
					String ip = address.toString().substring(1);
					files.addLine(new String(message + "\t" + ip));
					DatagramPacket response
					= new DatagramPacket(
							byteResponse, byteResponse.length, address, port);
					try {
						datagramSocket.send(response);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		};
		Thread addFileThread = new Thread(new AddFile());
		addFileThread.start();

	}

	private void searchFileServer() {
		class SearchFile implements Runnable {

			public void run() {
				DatagramSocket datagramSocket = null;
				try {
					datagramSocket = new DatagramSocket(Configuration.SEARCH_FILE_PORT);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				while (!Thread.currentThread().isInterrupted()) {
					DatagramPacket reclievedPacket
					= new DatagramPacket( new byte[Configuration.BUFFER_SIZE], Configuration.BUFFER_SIZE);

					try {
						datagramSocket.receive(reclievedPacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					int length = reclievedPacket.getLength();
					String message = "";
					try {
						message = new String(reclievedPacket.getData(), 0, length, "utf8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String[] parts = message.split("\t");
					List<DatabaseLine> linesToSend = new ArrayList<DatabaseLine>();
					if(parts[0].equals("file name")) {
						for(DatabaseLine a : files.files) {
							if(a.isFilename(parts[1]))
								linesToSend.add(a);
						}
					} else {
						for(DatabaseLine a : files.files) {
							if(a.isShaSum(parts[1]))
								linesToSend.add(a);
						}
					}
					String responseString = "";
					for(DatabaseLine a : linesToSend) {
						responseString += a.toString() + "\n";
					}

					byte[] byteResponse = null;
					try {
						byteResponse = responseString.getBytes("utf8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					InetAddress address = reclievedPacket.getAddress();
					int port = reclievedPacket.getPort();
					DatagramPacket response
					= new DatagramPacket(
							byteResponse, byteResponse.length, address, port);
					try {
						datagramSocket.send(response);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		};
		Thread addFileThread = new Thread(new SearchFile());
		addFileThread.start();
	}
	
	private void fileRequested() {
		class FileRequest implements Runnable {
			public void run() {
				DatagramSocket datagramSocket = null;
				try {
					datagramSocket = new DatagramSocket(Configuration.REQUEST_FILE_PORT);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				while (!Thread.currentThread().isInterrupted()) {
					DatagramPacket reclievedPacket
					= new DatagramPacket( new byte[Configuration.BUFFER_SIZE], Configuration.BUFFER_SIZE);

					try {
						datagramSocket.receive(reclievedPacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					int length = reclievedPacket.getLength();
					String message = "";
					try {
						message = new String(reclievedPacket.getData(), 0, length, "utf8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String[] parts = message.split("\t");
					InetAddress address = reclievedPacket.getAddress();
					String send = parts[2] + "\t" + address.toString().substring(1);
					
					
					String responseString = "0";
					try {
						if(sendRequest(send) != 0)
						responseString = "1";
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					byte[] byteResponse = null;
					try {
						byteResponse = responseString.getBytes("utf8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int port = reclievedPacket.getPort();
					DatagramPacket response
					= new DatagramPacket(
							byteResponse, byteResponse.length, address, port);
					try {
						datagramSocket.send(response);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		};
		Thread addFileThread = new Thread(new FileRequest());
		addFileThread.start();
	}


	public int sendRequest(String send) throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		int connectionStatus[] = new int[1];
		class sendLine implements Runnable {
			
			public void run() {

				String[] getIp = send.split("\t");
				InetAddress serverAddress = null;
				try {
					serverAddress = InetAddress.getByName(getIp[1]);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatagramSocket socket = null;
				try {
					socket = new DatagramSocket();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] stringContents = null;
				try {
					stringContents = send.getBytes("utf8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatagramPacket sentPacket = new DatagramPacket(stringContents, stringContents.length);
				sentPacket.setAddress(serverAddress);
				sentPacket.setPort(Configuration.CLIENT_REQUEST_PORT);
				try {
					socket.send(sentPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				DatagramPacket reclievePacket = new DatagramPacket( new byte[Configuration.BUFFER_SIZE], Configuration.BUFFER_SIZE);
				try {
					socket.setSoTimeout(2000);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try{
					socket.receive(reclievePacket);

					connectionStatus[0] = 0;
				}catch (SocketTimeoutException ste){

					connectionStatus[0] = -1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				latch.countDown();
			}
		}
		Thread addFileThread = new Thread(new sendLine());
		addFileThread.start();
		latch.await();
		return connectionStatus[0];
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
