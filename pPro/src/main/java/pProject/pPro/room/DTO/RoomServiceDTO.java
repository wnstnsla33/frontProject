package pProject.pPro.room.DTO;

public class RoomServiceDTO<T> {

    private RoomEnum state;
    private T data;

    public RoomServiceDTO(RoomEnum state) {
        this.state = state;
    }

    public RoomServiceDTO(RoomEnum state, T data) {
        this.state = state;
        this.data = data;
    }

    public RoomEnum getState() {
        return state;
    }

    public T getData() {
        return data;
    }

    public void setState(RoomEnum state) {
        this.state = state;
    }

    public void setData(T data) {
        this.data = data;
    }
}

