package io.github.axolotlclient.config;

public class CommandResponse {

    public boolean success;
    public String response;

    public CommandResponse(boolean success, String response){
        this.success = success;
        this.response = response;
    }

}
