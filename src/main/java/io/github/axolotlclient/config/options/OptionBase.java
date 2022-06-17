package io.github.axolotlclient.config.options;

public abstract class OptionBase implements Option {
    public String name;
    public OptionBase(String name){
	    this.name=name;
    }

	public String getName(){
		return name;
	}
}
