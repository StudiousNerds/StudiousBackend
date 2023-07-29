package nerds.studiousTestProject.exception.notfound;


import nerds.studiousTestProject.exception.ErrorCode;

public class RoomNotFoundException extends NotFoundException {
    public RoomNotFoundException() {
        super(ErrorCode.ROOM_NOT_FOUND, "룸을 찾을 수 없습니다.");
    }
}
