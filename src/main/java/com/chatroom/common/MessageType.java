package com.chatroom.common;

public enum MessageType{

    // client -> server
    QUIT,
    LOGIN_REQUEST, LOGOUT,
    REGISTER_CONFIRM,
    CHATROOM,
    PROFILE_OPEN, PROFILE_REQUEST,
    ADD, ACCEPT, BLOCK, REMOVE,

    // server -> client
    CHAT,
    INBOX,
    PROFILE_RESPONSE,

    // shared
    PICTURE
}