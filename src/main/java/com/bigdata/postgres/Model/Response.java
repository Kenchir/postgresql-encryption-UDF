package com.bigdata.postgres.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
//import com.google.api.client.util.Key;

public class Response {
    @JsonProperty("identifier")
    private String identifier;
    @JsonProperty("key")
    private String key;
    @JsonProperty("expired")
    private boolean expired;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getKey() {
        return key;
    }



    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
