package me.libraryaddict.disguise.disguisetypes.watchers;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MetaIndex;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;

public class OcelotWatcher extends TameableWatcher {

    public OcelotWatcher(Disguise disguise) {
        super(disguise);
    }

    public Type getType() {
        return Ocelot.Type.getType(getData(MetaIndex.OCELOT_TYPE));
    }

    public void setType(Type newType) {
        setData(MetaIndex.OCELOT_TYPE, newType.getId());
        sendData(MetaIndex.OCELOT_TYPE);
    }
}
