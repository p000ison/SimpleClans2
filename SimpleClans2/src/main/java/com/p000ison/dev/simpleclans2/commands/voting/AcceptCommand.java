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

package com.p000ison.dev.simpleclans2.commands.voting;

import com.p000ison.dev.commandlib.CallInformation;
import com.p000ison.dev.simpleclans2.SimpleClans;
import com.p000ison.dev.simpleclans2.api.chat.ChatBlock;
import com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayer;
import com.p000ison.dev.simpleclans2.api.request.Request;
import com.p000ison.dev.simpleclans2.api.request.RequestManager;
import com.p000ison.dev.simpleclans2.commands.GenericPlayerCommand;
import com.p000ison.dev.simpleclans2.language.Language;
import org.bukkit.entity.Player;

/**
 * Represents a AcceptCommand
 */
public class AcceptCommand extends GenericPlayerCommand {

    public AcceptCommand(SimpleClans plugin) {
        super("Accept", plugin);
        setDescription(Language.getTranslation("description.accept"));
        setIdentifiers(Language.getTranslation("accept.command"));
        addPermission("simpleclans.member.accept");
    }

    @Override
    public void execute(Player player, ClanPlayer cp, String[] arguments, CallInformation info) {
        ClanPlayer clanPlayer = getPlugin().getClanPlayerManager().getCreateClanPlayerExact(player);
        Request request = getPlugin().getRequestManager().vote(clanPlayer, RequestManager.Result.ACCEPT);

        if (request == null) {
            ChatBlock.sendMessage(player, Language.getTranslation("nothing.to.accept"));
        }
    }
}
