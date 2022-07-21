package io.github.axolotlclient.util.clientCommands;

import java.util.HashMap;

public class ClientCommands {

    private static final ClientCommands Instance = new ClientCommands();

    private final HashMap<String, Command> commands = new HashMap<>();

    public static ClientCommands getInstance(){
        return Instance;
    }

    public void registerCommand(String command, Command.CommandExecutionCallback onExecution){
        registerCommand(command, null, onExecution);
    }

    public void registerCommand(String command, Command.CommandSuggestionCallback suggestions, Command.CommandExecutionCallback onExecution){
        commands.put(command, new Command(command, suggestions, onExecution));
    }

    public HashMap<String, Command> getCommands(){
        return commands;
    }
}
