package edu.uni.rgz.multiplayer;

public class Room {
	private int hostId, guestId = -1;

	public Room(int host) {
		hostId = host;
	}

	public void addGuest(int guest) {
		guestId = guest;
	}

	public int getHostId() {
		return hostId;
	}

	public int getGuestId() {
		return guestId;
	}
}
