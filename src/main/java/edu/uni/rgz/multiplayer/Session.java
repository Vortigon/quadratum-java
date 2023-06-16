package edu.uni.rgz.multiplayer;

import edu.uni.rgz.multiplayer.dto.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Session extends Thread {
	private final Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private int sessionId;

	public Session(Socket socket, int sessionId) {
		this.socket = socket;
		this.sessionId = sessionId;

		try {
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
			sendConnectedClientsIdList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getIp() {
		return socket.getInetAddress().getHostAddress();
	}

	public int getSessionId() {
		return sessionId;
	}

	public void sendConnectedClientsIdList() {
		try {
			List<Session> sessions = Server.getSessions();
			List <Integer> idList = new ArrayList<>();
			for (Session session : sessions) {
				if (session != this) {
					idList.add(session.sessionId);
				}
			}
			out.writeObject(new ConnectedClientsIdListDto(idList));
		}
		catch (IOException exception){
			System.out.println(exception.getMessage() + "\n" + exception.getCause());
			exception.printStackTrace();
		}
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (!socket.isClosed()) {
				Object dto = in.readObject();
				System.out.println("got some dto");
				if (dto instanceof CreateRoomRequestDto requestDto) {
					System.out.println("Host " + sessionId + " created room");
					Server.getRooms().add(new Room(sessionId));
				} else if (dto instanceof DisconnectRequestDto requestDto) {
					System.out.println("Session " + sessionId + " disconnected");
					Server.getSessions().remove(this);
					Server.getRooms().removeIf(room -> room.getHostId() == sessionId);
//					Server.getSessions().forEach(Session::sendConnectedClientsIdList); // ????
					break;
				} else if (dto instanceof JoinRequestDto requestDto) {
					System.out.println("Guest " + sessionId + " requests to join room of Host " + requestDto.getToHostId());
					boolean sent = false;
					for (Room room : Server.getRooms()) {
						if (room.getHostId() == requestDto.getToHostId()) {
							room.addGuest(sessionId);
							for (Session session : Server.getSessions()) {
								if (session.sessionId == requestDto.getToHostId()) {
									session.out.writeObject(requestDto);
									sent = true;
									break;
								}
							}
							break;
						}
					}
					if (sent) {
						out.writeObject(requestDto);
					} else {
						out.writeObject(new JoinFailedDto()); //!!!!!!!!!!!!!!!!!!!
					}
				} else if (dto instanceof TurnDto) {

				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage() + " " + e.getCause());
			e.printStackTrace();
			Server.getSessions().remove(this);
			Server.getSessions().forEach(Session::sendConnectedClientsIdList);
		}
	}
}
