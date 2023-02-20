package io.github.axolotlclient.credits;

import lombok.Getter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Credits {

	@Getter
	private static final Set<Credits> contributors = new LinkedHashSet<>(), otherPeople = new LinkedHashSet<>();

	static {
		contributor("moehreag", "Author, Programming", "https://github.com/moehreag");
		contributor("YakisikliBaran", "Turkish Translation");
		contributor("TheKodeToad", "Contributor", "Motion Blur", "Freelook", "Zoom");
		contributor("DragonEggBedrockBreaking", "Bugfixing", "Inspiration of new Features");
		contributor("gart", "gartbin dev and host", "Image sharing help", "https://gart.sh", "Backend developer");

		otherPerson("DarkKronicle", "Author of KronHUD, the best HUD mod!");
		otherPerson("AMereBagatelle", "Author of the excellent FabricSkyBoxes Mod");
	}

	@Getter
	private final String name;
	@Getter
	private final String[] things;

	public Credits(String name, String... things){
		this.name = name;
		this.things = things;
	}

	public static void contributor(String name, String... things){
		Credits c = new Credits(name, things);
		contributors.add(c);
	}

	public static void otherPerson(String name, String... things){
		Credits c = new Credits(name, things);
		otherPeople.add(c);
	}
}
