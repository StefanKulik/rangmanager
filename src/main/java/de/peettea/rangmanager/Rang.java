package de.peettea.rangmanager;

import lombok.Getter;
import net.luckperms.api.model.group.Group;
import org.bukkit.ChatColor;

@Getter
public class Rang {

    private final Group group;
    private final String name;
    private final String displayName;
    private final int weight;
    private final String prefix;
    private final ChatColor color;

    public Rang(Group group) {
        this.group = group;
        this.name = group.getName();
        this.displayName = group.getDisplayName();
        this.weight = group.getWeight().getAsInt();
        this.prefix = group.getCachedData().getMetaData().getPrefix();
        this.color = ChatColor.valueOf(group.getCachedData().getMetaData().getMetaValue("color"));
    }
}
