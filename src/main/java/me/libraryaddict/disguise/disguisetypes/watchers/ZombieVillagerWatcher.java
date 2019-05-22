package me.libraryaddict.disguise.disguisetypes.watchers;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MetaIndex;
import me.libraryaddict.disguise.disguisetypes.VillagerData;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import java.util.Random;

public class ZombieVillagerWatcher extends ZombieWatcher {

    public ZombieVillagerWatcher(Disguise disguise) {
        super(disguise);

        setProfession(Profession.values()[new Random().nextInt(Profession.values().length)]);
    }

    public boolean isShaking() {
        return getData(MetaIndex.ZOMBIE_VILLAGER_SHAKING);
    }

    /**
     * Is this zombie a villager?
     *
     * @return
     */
    public boolean isVillager() {
        return getData(MetaIndex.ZOMBIE_VILLAGER_PROFESSION).getProfession() != Profession.NONE;
    }

    public void setShaking(boolean shaking) {
        setData(MetaIndex.ZOMBIE_VILLAGER_SHAKING, shaking);
        sendData(MetaIndex.ZOMBIE_VILLAGER_SHAKING);
    }

    public VillagerData getVillagerData() {
        return getData(MetaIndex.VILLAGER_DATA);
    }

    public void setVillagerData(VillagerData villagerData) {
        setData(MetaIndex.VILLAGER_DATA, villagerData);
        sendData(MetaIndex.VILLAGER_DATA);
    }

    public Profession getProfession() {
        return getVillagerData().getProfession();
    }

    public Villager.Type getType() {
        return getVillagerData().getType();
    }

    public int getLevel() {
        return getVillagerData().getLevel();
    }

    public void setProfession(Profession profession) {
        setVillagerData(new VillagerData(getType(), profession, getLevel()));
    }

    public void setType(Villager.Type type) {
        setVillagerData(new VillagerData(type, getProfession(), getLevel()));
    }

    public void setLevel(int level) {
        setVillagerData(new VillagerData(getType(), getProfession(), getLevel()));
    }
}
