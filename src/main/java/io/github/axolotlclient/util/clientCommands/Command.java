package io.github.axolotlclient.util.clientCommands;

import java.util.List;

public class Command {

    String command;
    CommandSuggestionCallback suggestions;
    CommandExecutionCallback exec;

    public Command(String command, CommandSuggestionCallback suggestions, CommandExecutionCallback onExecute) {
        this.command = command;
        this.suggestions = suggestions;
        this.exec = onExecute;
    }

    public String getCommandName(){
        return command;
    }

    public void execute(String[] args) {
        exec.onExecution(args);
    }

    public List<String> getSuggestions(String[] args) {
        return suggestions.getSuggestions(args);
    }

    public interface CommandExecutionCallback {
        void onExecution(String[] args);
    }

    public interface CommandSuggestionCallback {
        List<String> getSuggestions(String[] args);
    }
}
