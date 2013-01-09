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
 *     Last modified: 10.10.12 21:57
 */

package com.p000ison.dev.simpleclans2.commands.clan.rank;

import com.p000ison.dev.simpleclans2.SimpleClans;
import com.p000ison.dev.simpleclans2.api.chat.Align;
import com.p000ison.dev.simpleclans2.api.chat.ChatBlock;
import com.p000ison.dev.simpleclans2.api.clan.Clan;
import com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayer;
import com.p000ison.dev.simpleclans2.api.rank.Rank;
import com.p000ison.dev.simpleclans2.commands.GenericPlayerCommand;
import com.p000ison.dev.simpleclans2.language.Language;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Represents a ListPermissionsCommand
 */
public class ListRanksCommand extends GenericPlayerCommand {

    public ListRanksCommand(SimpleClans plugin)
    {
        super("ListRanks", plugin);
        setArgumentRange(0, 0);
        setUsages(Language.getTranslation("usage.view.ranks", plugin.getSettingsManager().getRankCommand()));
        setIdentifiers(Language.getTranslation("view.ranks.command"));
        setPermission("simpleclans.leader.rank.list");
        setType(Type.RANK);
    }

    @Override
    public String getMenu(ClanPlayer clanPlayer)
    {
        if (clanPlayer != null) {
            return Language.getTranslation("menu.ranks.view", plugin.getSettingsManager().getRankCommand());
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

        Clan clan = clanPlayer.getClan();

        Set<Rank> ranks = clan.getRanks();

        List<Rank> sorted = new ArrayList<Rank>(ranks);

        Collections.sort(sorted);

        if (ranks.isEmpty()) {
            ChatBlock.sendMessage(player, ChatColor.DARK_RED + Language.getTranslation("no.ranks.created"));
            return;
        }

        int page = 0;
        int completeSize = ranks.size();

        if (args.length == 1) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {
                ChatBlock.sendMessage(player, Language.getTranslation("number.format"));
                return;
            }
        }

        ChatBlock chatBlock = new ChatBlock();

        ChatBlock.sendHead(player, plugin.getSettingsManager().getClanColor() + clan.getTag(), Language.getTranslation("ranks"));

        ChatBlock.sendBlank(player);

        chatBlock.setAlignment(Align.CENTER, Align.LEFT, Align.LEFT, Align.CENTER);
        chatBlock.addRow(Language.getTranslation("id"), Language.getTranslation("tag"), Language.getTranslation("name"), Language.getTranslation("priority"));

        int[] boundings = getBoundings(completeSize, page);

        for (int i = boundings[0]; i < boundings[1]; i++) {
            Rank rank = sorted.get(i);

            chatBlock.addRow(ChatColor.GRAY.toString() + rank.getID(), ChatColor.GRAY + rank.getTag(), ChatColor.GRAY + rank.getName(), ChatColor.GRAY.toString() + rank.getPriority());
        }


        chatBlock.sendBlock(player);
    }
}
