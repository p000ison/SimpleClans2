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
 *     Last modified: 29.10.12 22:42
 */

package com.p000ison.dev.simpleclans2.commands.admin;

import com.p000ison.dev.simpleclans2.SimpleClans;
import com.p000ison.dev.simpleclans2.commands.GenericConsoleCommand;
import com.p000ison.dev.simpleclans2.converter.Converter;
import com.p000ison.dev.simpleclans2.language.Language;
import com.p000ison.dev.simpleclans2.util.Logging;
import com.p000ison.dev.sqlapi.DatabaseConfiguration;
import com.p000ison.dev.sqlapi.DatabaseManager;
import com.p000ison.dev.sqlapi.exception.DatabaseConnectionException;
import com.p000ison.dev.sqlapi.jbdc.JBDCDatabase;
import com.p000ison.dev.sqlapi.mysql.MySQLConfiguration;
import com.p000ison.dev.sqlapi.mysql.MySQLDatabase;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;

/**
 * Represents a ConvertCommand
 */
public class ConvertCommand extends GenericConsoleCommand {

    public ConvertCommand(SimpleClans plugin)
    {
        super("ConvertCommand", plugin);
        setArgumentRange(2, 5);
        setUsages(MessageFormat.format(Language.getTranslation("usage.convert"), plugin.getSettingsManager().getClanCommand()));
        setIdentifiers(Language.getTranslation("convert.command"));
        setPermission("simpleclans.admin.convert");
    }

    @Override
    public String getMenu()
    {
        return MessageFormat.format(Language.getTranslation("menu.convert"), plugin.getSettingsManager().getClanCommand());
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        String action = args[0];
        JBDCDatabase database = null;
        DatabaseConfiguration config = null;

        try {
            if (action.equalsIgnoreCase("mysql")) {
                String[] address = args[1].split(":");
                int port = 3306;
                if (address.length == 2) {
                    try {
                        port = Integer.parseInt(address[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Language.getTranslation("number.format"));
                        return;
                    }
                }
                config = new MySQLConfiguration(args[3], args[4], address[0], port, args[2]);

                database = (JBDCDatabase) DatabaseManager.getConnection(config);
                if (database == null) {
                    database = new MySQLDatabase(config);
                }
            } else if (action.equalsIgnoreCase("sqlite")) {
//            config = new SQLiteConfiguration(new File(args[1]));
//            database = (JBDCDatabase) DatabaseManager.getConnection(config);
            }

            if (config == null) {
                sender.sendMessage("Database type not supported!");
                return;
            }

            Converter converter = new Converter(database, (JBDCDatabase) plugin.getSimpleClansDatabase());
            sender.sendMessage("Starting converting!");
            converter.convertAll();
            sender.sendMessage("Successfully converted!");
        } catch (DatabaseConnectionException e) {
            Logging.debug(e, true);
            sender.sendMessage("Converting failed!");
        }
    }
}
