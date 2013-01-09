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


package com.p000ison.dev.simpleclans2.clan;

import com.p000ison.dev.simpleclans2.api.JSONFlags;
import com.p000ison.dev.simpleclans2.api.clan.ClanFlags;
import com.p000ison.dev.simpleclans2.vectors.PlayerPosition;
import org.bukkit.Location;

/**
 * Represents a the JSONFlags of a clan
 */
public class CraftClanFlags extends JSONFlags implements ClanFlags {

    @Override
    public void setHomeLocation(Location location)
    {
        super.setString("home", new PlayerPosition(location).serialize());
    }

    @Override
    public Location getHomeLocation()
    {
        String locationString = super.getString("home");

        if (locationString == null) {
            return null;
        }

        return PlayerPosition.deserialize(locationString).toLocation();
    }

    @Override
    public void setClanCapeURL(String url)
    {
        super.setString("cape-url", url);
    }

    @Override
    public String getClanCapeURL()
    {
        return super.getString("cape-url");
    }

    @Override
    public void setFriendlyFire(boolean bool)
    {
        super.setBoolean("ff", bool);
    }

    @Override
    public boolean isFriendlyFireEnabled()
    {
        return super.getBoolean("ff");
    }
}
