package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.axolotlclient.util.clientCommands.CommandResponse;

import java.util.*;

public class EnumOption extends OptionBase<String> {

    private int i;

    protected String[] values;

    public EnumOption(String name, Object[] e, String def) {
        super(name, def);
        List<String> l = new ArrayList<>();
        for(Object v:e){
            l.add(v.toString());
        }
        values = l.toArray(new String[0]);
    }

    public EnumOption(String name, String[] e, String def) {
        super(name, def);
        values = e;
    }

    @Override
    public OptionType getType() {
        return OptionType.ENUM;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        for(int i=0; i<values.length;i++){
            String v = values[i];
            if(Objects.equals(v, element.getAsString())){
                this.i = i;
                break;
            }
        }
    }

    @Override
    public void setDefaults() {
        if(def==null){
            i=0;
            return;
        }

        for(int i=0;i< values.length; i++){
            String v = values[i];
            if(Objects.equals(v, def)){
                this.i=i;
                break;
            }
        }
    }

    public String get(){
        return values[i];
    }

    public String next() {
        i++;
        if(i > values.length-1)i=0;
        return get();
    }

    public String last(){
        i--;
        if(i<0)i=values.length-1;
        return get();
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(get());
    }

    @Override
    protected CommandResponse onCommandExecution(String[] args) {
        if(args.length>0){
            if(args[0].equals("next")){
                next();
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+"!");
            } else if(args[0].equals("last")){
                last();
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+"!");
            }

            for (int i=0;i<values.length;i++){
                if(args[0].equalsIgnoreCase(values[i])){
                    this.i=i;
                    return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+" (Index: "+i+")!");
                }
            }

            try {
                int value = Integer.parseInt(args[0]);
                if(value>values.length-1 || value < 0){
                    throw new IndexOutOfBoundsException();
                }
                i=value;
                return new CommandResponse(true, "Successfully set "+getName()+" to "+get()+" (Index: "+i+")!");
            } catch (IndexOutOfBoundsException e){
                return new CommandResponse(false, "Please specify an index within the bounds of 0<=i<"+values.length+"!");
            } catch (NumberFormatException ignored){
                return new CommandResponse(false, "Please specify either next, last or an index for a specific value!");
            }
        }
        return new CommandResponse(true, getName() + " is currently set to '"+get()+"'.");
    }

    @Override
    public List<String> getCommandSuggestions() {
        return Arrays.asList(values);
    }
}
