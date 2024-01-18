package de.peettea.rangmanager;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.track.DemotionResult;
import net.luckperms.api.track.PromotionResult;
import net.luckperms.api.track.Track;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RangManager {

    //definiere Ränge in Enum
    //Admin = MC OP Rang
    // Custom Chat Nachricht vom Spieler mit Rang Anzeige
    // Tablist mit Rang

    LuckPerms api;

    public RangManager(LuckPerms api) {
        this.api = api;
    }

    public void setUserGroup(Player player, String displayName) {
        UUID id = player.getUniqueId();
        User user = api.getUserManager().getUser(id);
        Group group = api.getGroupManager().getLoadedGroups().stream().filter(g -> g.getDisplayName().equals(displayName)).findAny().get();
        Group oldPrimaryGroup = api.getGroupManager().getGroup(user.getPrimaryGroup());
        InheritanceNode node = InheritanceNode.builder(group.getName()).value(true).build();
        user.data().add(node);
        user.setPrimaryGroup(group.getName());

        InheritanceNode deleteNode = InheritanceNode.builder(oldPrimaryGroup.getName()).value(true).build();
        if(user.data().contains(deleteNode, NodeEqualityPredicate.EXACT).asBoolean()) {
            user.data().remove(deleteNode);
        }

        api.getUserManager().saveUser(user);
    }

    public PromotionResult promotePlayer(Player target) {
        UUID id = target.getUniqueId();
        User user = api.getUserManager().getUser(id);
        Track rangordnung = api.getTrackManager().getTrack("rang");
        PromotionResult promote = rangordnung.promote(user, api.getContextManager().getStaticContext());
        api.getUserManager().saveUser(user);

        return promote;
    }

    public DemotionResult demotePlayer(Player target) {
        UUID id = target.getUniqueId();
        User user = api.getUserManager().getUser(id);
        Track rangordnung = api.getTrackManager().getTrack("rang");
        DemotionResult demote = rangordnung.demote(user, api.getContextManager().getStaticContext());
        api.getUserManager().saveUser(user);

        return demote;
    }

    public void setPlayerOP(Player target) {
        target.setOp(true);
        UUID id = target.getUniqueId();
        User user = api.getUserManager().getUser(id);
        InheritanceNode owner = InheritanceNode.builder("owner").value(true).build();
        user.data().add(owner);
        api.getUserManager().saveUser(user);
    }

    public void deOpPlayer(Player target) {
        target.setOp(false);
        UUID id = target.getUniqueId();
        User user = api.getUserManager().getUser(id);
        InheritanceNode deleteNode = InheritanceNode.builder("owner").value(true).build();
        user.data().remove(deleteNode);
        api.getUserManager().saveUser(user);
    }

    public Rang findPlayerRang(Player player) {
        User user = api.getUserManager().getUser(player.getUniqueId());
        Group group = api.getGroupManager().getGroup(user.getPrimaryGroup());
        if(player.isOp()) {
            group = api.getGroupManager().getGroup("owner");
        }
        return new Rang(group);
    }
}
