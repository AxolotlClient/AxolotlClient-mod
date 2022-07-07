package io.github.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnumOption extends OptionBase {

    private int i;

    protected String[] values;

    private final String def;

    public EnumOption(String name, Object[] e, String def) {
        super(name);
        List<String> l = new ArrayList<>();
        for(Object v:e){
            l.add(v.toString());
        }
        values = l.toArray(new String[0]);
        this.def = def;
    }

    public EnumOption(String name, String[] e, String def) {
        super(name);
        values = e;
        this.def = def;
    }

    @Override
    public OptionType getType() {
        return OptionType.ENUM;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        for(int i=0; i<values.length-1;i++){
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

        for(int i=0;i< values.length-1; i++){
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

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(get());
    }
}
