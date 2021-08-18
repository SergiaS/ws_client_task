package com.wsclient.model;

public class RequestCommand {
    private final String action;
    private final String channel; // stream
    private final String symbol;

    public RequestCommand(String action, String channel, String symbol) {
        this.action = action;
        this.channel = channel;
        this.symbol = symbol;
    }

    public String getAction() {
        return action;
    }

    public String getChannel() {
        return channel;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "RequestCommand{" +
                "action='" + action + '\'' +
                ", channel='" + channel + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
