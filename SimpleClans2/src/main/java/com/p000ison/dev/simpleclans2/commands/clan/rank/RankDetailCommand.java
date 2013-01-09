/*
 * This file is part of SimpleClans2 (2012).
 *
 *     SimpleClans2 is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     SimpleClans2 is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with SimpleClans2.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     Last modified: 31.10.12 23:54
 */

package com.p000ison.dev.simpleclans2.commands.clan.rank;

import com.p000ison.dev.simpleclans2.SimpleClans;
import com.p000ison.dev.simpleclans2.api.chat.ChatBlock;
import com.p000ison.dev.simpleclans2.api.clan.Clan;
import com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayer;
import com.p000ison.dev.simpleclans2.api.rank.Rank;
import com.p000ison.dev.simpleclans2.clan.ranks.CraftRank;
import com.p000ison.dev.simpleclans2.commands.GenericPlayerCommand;
import com.p000ison.dev.simpleclans2.language.Language;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Represents a ListPermissionsCommand
 */
public class RankDetailCommand extends GenericPlayerCommand {

    public RankDetailCommand(SimpleClans plugin)
    {
        super("ViewRank", plugin);
        setArgumentRange(1, 1);
        setUsages(Language.getTranslation("usage.view.rank", plugin.getSettingsManager().getRankCommand()));
        setIdentifiers(Language.getTranslation("view.rank.command"));
        setPermission("simpleclans.leader.rank.detail");
        setType(Type.RANK);
    }

    @Override
    public String getMenu(ClanPlayer clanPlayer)
    {
        if (clanPlayer != null) {
            return Language.getTranslation("menu.rank.view", plugin.getSettingsManager().getRankCommand());
        }
        return null;
    }

    @Override
    public void execute(Player player, String[] args)
    {
        ClanPlayer clanPlayer = plugin.getClanPlayerManager().getClanPlayer(player);

        if (clanPlayer == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + Language.getTranslation("not.a.member.of.any.clan"));
            return;
        }

        if (!clanPlayer.isLeader() && !clanPlayer.hasRankPermission("manage.ranks")) {
            ChatBlock.sendMessage(player, ChatColor.RED + Language.getTranslation("no.leader.permissions"));
            return;
        }
        Clan clan = clanPlayer.getClan();
        ChatColor subColor = plugin.getSettingsManager().getSubPageColor();

        Rank queried;
        String query = args[0];

        try {
            long id = Long.parseLong(query);
            queried = clan.getRank(id);
        } catch (NumberFormatException e) {
            queried = clan.getRank(query);
        }

        if (queried == null) {
            ChatBlock.sendMessage(player, ChatColor.DARK_RED + Language.getTranslation("rank.not.found"));
            return;
        }

        ChatBlock.sendBlank(player);
        ChatBlock.sendHead(player, plugin.getSettingsManager().getClanColor() + Language.getTranslation("rank.head", clan.getTag()), null);

        ChatBlock.sendBlank(player, 2);


        String id = String.valueOf(queried.getID());
        String tag = queried.getTag();
        String name = queried.getName();

        ChatBlock.sendMessage(player, subColor + Language.getTranslation("id") + ": " + ChatColor.WHITE + id);
        ChatBlock.sendMessage(player, subColor + Language.getTranslation("tag") + ": " + ChatColor.WHITE + tag);
        ChatBlock.sendMessage(player, subColor + Language.getTranslation("name") + ": " + ChatColor.WHITE + name);


        Map<Integer, Boolean> permissions = queried.getPermissions();

        if (permissions == null || permissions.isEmpty()) {
            return;
        }

        ChatBlock.sendMessage(player, subColor + Language.getTranslation("permissions") + ":");
        for (Map.Entry<Integer, Boolean> entry : permissions.entrySet()) {
            String output = subColor + "  - ";

            if (!entry.getValue()) {
                output += '-';
            }

            ChatBlock.sendMessage(player, output + ChatColor.WHITE + CraftRank.getByID(entry.getKey()));
        }
    }
}
