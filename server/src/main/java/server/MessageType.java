package server;

public enum MessageType {
    CONNECT,
    DISCONNECT,
    CHAT,
    MOVE,
    GAME_START,
    GAME_UPDATE,
    GAME_END,
    CONNECT_ACK,
    ERROR,
    CHECK_USERNAME,
    USERNAME_AVAILABLE,
    USERNAME_TAKEN
}
