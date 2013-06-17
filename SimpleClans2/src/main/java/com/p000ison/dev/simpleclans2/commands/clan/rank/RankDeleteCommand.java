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
 *     Last modified: 10/20/12 5:08 PM
 */

package com.p000ison.dev.simpleclans2.commands.clan.rank;

import com.p000ison.dev.commandlib.CallInformation;
import com.p000ison.dev.simpleclans2.SimpleClans;
import com.p000ison.dev.simpleclans2.api.chat.ChatBlock;
import com.p000ison.dev.simpleclans2.api.clan.Clan;
import com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayer;
import com.p000ison.dev.simpleclans2.commands.GenericPlayerCommand;
import com.p000ison.dev.simpleclans2.database.statements.RemoveRankStatement;
import com.p000ison.dev.simpleclans2.language.Language;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Represents a RankCreateCommand
 */
public class RankDeleteCommand extends GenericPlayerCommand {

    public RankDeleteCommand(SimpleClans plugin) {
        super("Delete rank", plugin);
        addArgument(Language.getTranslation("argument.rank"));
        setDescription(Language.getTranslation("description.rank.delete", plugin.getSettingsManager().getRankCommand()));
        setIdentifiers(Language.getTranslation("rank.delete.command"));
        addPermission("simpleclans.leader.rank.delete");

        setNeedsClan();
        setRankPermission("manage.ranks");
    }


    @Override
    public void execute(Player player, ClanPlayer cp, String[] arguments, CallInformation info) {
        Clan clan = cp.getClan();

        long response = clan.deleteRank(arguments[0]);

        if (response == -1) {
            ChatBlock.sendMessage(player, ChatColor.DARK_RED + Language.getTranslation("rank.not.found"));
            return;
        }

        getPlugin().getDataManager().addStatement(new RemoveRankStatement(response));

        ChatBlock.sendMessage(player, ChatColor.AQUA + Language.getTranslation("rank.deleted"));
    }
}
