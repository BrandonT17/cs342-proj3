package com.example.javafxapp;

// all the different types of messages that will appear in the server 

public enum MessageType {
    CONNECT,
    DISCONNECT,
    CHAT,
    MOVE,
    MOVE_VALID,
    YOUR_TURN,
    WAIT,
    GAME_START,
    GAME_UPDATE,
    GAME_END,
    CONNECT_ACK,
    ERROR,
    CHECK_USERNAME,
    USERNAME_AVAILABLE,
    USERNAME_TAKEN,
    MATCH_FOUND, // wait for partner
    READY_FOR_MATCH // solution to connection scene skip
} 
