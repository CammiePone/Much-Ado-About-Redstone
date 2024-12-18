package dev.cammiescorner.maar.common.registry;

import dev.cammiescorner.maar.MAAR;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;

public class ModSounds {
	//-----Sound Map-----//
	public static final LinkedHashMap<SoundEvent, Identifier> SOUNDS = new LinkedHashMap<>();

	//-----Sound Events-----//
	public static final SoundEvent PULSER_FIRE = create("pulser_fire");
	public static final SoundEvent PULSER_DIE = create("pulser_die");

	//-----Registry-----//
	public static void register() {
		SOUNDS.keySet().forEach(sound -> Registry.register(Registry.SOUND_EVENT, SOUNDS.get(sound), sound));
	}

	private static SoundEvent create(String name) {
		SoundEvent sound = new SoundEvent(MAAR.id(name));
		SOUNDS.put(sound, MAAR.id(name));
		return sound;
	}
}
