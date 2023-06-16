package edu.uni.rgz.multiplayer;

import edu.uni.rgz.multiplayer.dto.ConnectedClientsIdListDto;
import edu.uni.rgz.multiplayer.dto.CreateRoomRequestDto;
import edu.uni.rgz.multiplayer.dto.DisconnectRequestDto;
import edu.uni.rgz.multiplayer.dto.JoinRequestDto;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class Client extends Thread {
	private Socket socket;
	private boolean isConnected;
	private final Object clientLock = new Object();
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int sessionId;
	private Room createdRoom;
//	private Habitat habitat;
	private List<Integer> connectedClientsIds;
	private List<Room> rooms;

	public Client() {
		this.isConnected = false;
//		this.habitat = null;
		connectedClientsIds = new LinkedList<>();
	}

	public boolean isConnected() {
		return isConnected;
	}

	public String getAddress() {
		return socket.getInetAddress().getHostName();
	}

	public List<Integer> getConnectedClientsIds() {
		return connectedClientsIds;
	}

//	public void setHabitat(Habitat habitat) {
//		this.habitat = habitat;
//	}

	public void connect(String address, int port) throws Exception {
		try {
			this.socket = new Socket(address, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			isConnected = true;
			if (this.getState() == State.NEW) {
				this.start();
			} else {
				synchronized (clientLock) {
					clientLock.notify();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
			throw new Exception("Error occurred while trying to establish connection");
		}
	}

	@Override
	public void run() {
		while (true) {
			synchronized (clientLock) {
				while (!isConnected) {
					try {
						clientLock.wait();
					} catch (InterruptedException e) {
						System.out.println(e.getMessage() + " " + e.getCause());
						e.printStackTrace();
					}
				}
			}

			try {
				while (!socket.isClosed() && isConnected) {
					Object dto = in.readObject();

					if (dto instanceof ConnectedClientsIdListDto) {
						connectedClientsIds = ((ConnectedClientsIdListDto) dto).idList();
					} else if (dto instanceof JoinRequestDto requestDto) {
						if (requestDto.getToHostId() == sessionId) {

						}
					}



				}
			} catch (Exception e) {
				System.out.println(e.getMessage() + " " + e.getCause());
				e.printStackTrace();
			}
		}
	}

	public void requestJoinToRoom(int toHostId) {
		System.out.println("known connections" + connectedClientsIds);
		System.out.println("Requesting to join to " + toHostId);

		try {
			JoinRequestDto requestDto = new JoinRequestDto(toHostId);
			out.writeObject(requestDto);
		} catch (Exception e) {
			System.out.println(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
	}

	public void requestCreateRoom() {
		System.out.println("Requesting to create room");
		try {
			CreateRoomRequestDto requestDto = new CreateRoomRequestDto();
			out.writeObject(requestDto);
		} catch (Exception e) {
			System.out.println(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
		}
	}

	public void disconnect() throws Exception {
		try {
			out.writeObject(new DisconnectRequestDto());
			isConnected = false;
		} catch (Exception e) {
			throw new Exception(e.getMessage() + " " + e.getCause());
		}
	}
}
