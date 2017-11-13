package com.utn.mobile.keepapp.domain;



public class Notificacion {

    private String userTo;
    private String userFrom;
    private String message;
    private Boolean read;
    private String topic;

    public Notificacion(String userTo, String userFrom, String message) {
        this.userTo = userTo;
        this.userFrom = userFrom;
        this.message = message;
        this.read = false;
        this.topic = userTo; //el topic es el id del user
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
