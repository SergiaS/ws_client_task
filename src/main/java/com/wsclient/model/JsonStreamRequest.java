package com.wsclient.model;

import java.util.Arrays;

public class JsonStreamRequest {
    private final String method;
    private final String[] params;
    private final int id;

    private transient final int ID_DEFAULT_VALUE = 47; // not necessary

    public JsonStreamRequest(String method, String[] params) {
        this.method = method;
        this.params = params;
        this.id = ID_DEFAULT_VALUE;
    }

    public JsonStreamRequest(String method, String[] params, int id) {
        this.method = method;
        this.params = params;
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public String[] getParams() {
        return params;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "RequestJson{" +
                "method=" + method +
                ", params=" + Arrays.toString(params) +
                ", id=" + id +
                '}';
    }

    public static JsonStreamRequest sample() {
        return new JsonStreamRequest("SUBSCRIBE",
                new String[]{"btcusdt@aggTrade", "btcusdt@depth"});
    }
}
