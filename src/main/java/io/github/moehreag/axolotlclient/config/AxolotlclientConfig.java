package io.github.moehreag.axolotlclient.config;

public class AxolotlclientConfig {

    public nametagConf NametagConf = new nametagConf();
    public static class nametagConf {
        public boolean showOwnNametag = false;

        public boolean useShadows = false;
    }


    public Badges badgeOptions = new Badges();
    public static class Badges {
        public boolean showBadge = true;

        //public boolean showChatBadge = true;

        public boolean CustomBadge = false;


        public String badgeText = "";
    }

    public nh NickHider = new nh();
    public static class nh {
        public boolean hideOwnName = false;
        public String OwnName = "";

        public boolean hideOtherNames = false;
        public String otherName = "Player";

        public boolean hideOwnSkin = false;
        public boolean hideOtherSkins = false;

    }

    public other General = new other();
    public static class other {
        public boolean fullBed = false;
    }


    public rpcConfig RPCConfig = new rpcConfig();
    public static class rpcConfig {

        public boolean enableRPC = true;

        public boolean showActivity = true;
    }

    public AxolotlclientConfig(nametagConf nametagConf, Badges badges, nh nickHider, other other, rpcConfig rpcConfig){
        this.NametagConf = nametagConf;
        this.badgeOptions = badges;
        this.General = other;
        this.RPCConfig = rpcConfig;
    }

}
