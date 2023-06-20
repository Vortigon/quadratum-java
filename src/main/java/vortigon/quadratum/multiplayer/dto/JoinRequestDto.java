package vortigon.quadratum.multiplayer.dto;

public class JoinRequestDto {
	private int toHostId, fromGuestId;

	public JoinRequestDto(int toHostId) {
		this.toHostId = toHostId;
	}

	public void setFromGuestId(int fromGuestId) {
		this.fromGuestId = fromGuestId;
	}

	public int getFromGuestId() {
		return fromGuestId;
	}

	public int getToHostId() {
		return toHostId;
	}
}
