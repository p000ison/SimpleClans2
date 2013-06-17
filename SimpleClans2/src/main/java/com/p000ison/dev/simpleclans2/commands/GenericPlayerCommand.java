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
 *     Last modified: 29.01.13 20:55
 */


package com.p000ison.dev.simpleclans2.commands;

import com.p000ison.dev.simpleclans2.SimpleClans;
import com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayer;
import com.p000ison.dev.simpleclans2.api.commands.ClanPlayerCommand;
import com.p000ison.dev.simpleclans2.language.Language;
import org.bukkit.ChatColor;

import java.text.MessageFormat;

/**
 * Represents a GenericPlayerCommand
 */
public abstract class GenericPlayerCommand extends ClanPlayerCommand {

    private SimpleClans plugin;

    public GenericPlayerCommand(String name, SimpleClans plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public boolean isAllowed(ClanPlayer cp, com.p000ison.dev.commandlib.CommandSender sender) {
        if (cp != null) {
            if (noClan) {
                if (sender != null) {
                    sender.sendMessage(ChatColor.RED + MessageFormat.format(Language.getTranslation("you.must.first.resign"), cp.getClan().getName()));
                }
                return false;
            }

            if (trusted && !cp.isTrusted()) {
                if (sender != null) {
                    sender.sendMessage(ChatColor.RED + Language.getTranslation("only.trusted.players.can.do.this"));
                }
                return false;
            }
            if (leader && !cp.isLeader()) {
                if (sender != null) {
                    sender.sendMessage(ChatColor.RED + Language.getTranslation("no.leader.permissions"));
                }
                return false;
            }

            if (clanVerified && !cp.getClan().isVerified()) {
                if (sender != null) {
                    sender.sendMessage(ChatColor.RED + Language.getTranslation("get.your.clan.verified.to.access.advanced.features"));
                }
                return false;
            }

            if (clanNotVerified && cp.getClan().isVerified()) {
                if (sender != null) {
                    sender.sendMessage(ChatColor.GRAY + Language.getTranslation("your.clan.is.already.verified"));
                }
                return false;
            }

            if (rankPerm != null && (!cp.hasRankPermission(rankPerm) || !cp.isLeader())) {
                if (sender != null) {
                    sender.sendMessage(ChatColor.RED + Language.getTranslation("no.rank.permissions"));
                }
                return false;
            }
        } else {
            if (hasClan) {
                if (sender != null) {
                    sender.sendMessage(ChatColor.RED + Language.getTranslation("not.a.member.of.any.clan"));
                }
                return false;
            }
        }

        return true;
    }

    public SimpleClans getPlugin() {
        return plugin;
    }
}
