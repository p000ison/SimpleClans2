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
 *     Last modified: 27.02.13 17:00
 */

package com.p000ison.dev.simpleclans2.claiming.data;

import com.p000ison.dev.simpleclans2.api.SCCore;
import com.p000ison.dev.simpleclans2.api.clan.Clan;
import com.p000ison.dev.simpleclans2.api.clan.ClanFlags;
import com.p000ison.dev.simpleclans2.api.clan.ClanManager;
import com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayer;
import com.p000ison.dev.simpleclans2.api.clanplayer.ClanPlayerManager;
import com.p000ison.dev.simpleclans2.api.clanplayer.PlayerFlags;
import com.p000ison.dev.simpleclans2.claiming.Claim;
import com.p000ison.dev.simpleclans2.claiming.ClaimLocation;
import com.p000ison.dev.simpleclans2.claiming.SettingsManager;
import com.p000ison.dev.simpleclans2.claiming.SimpleClansClaiming;
import com.p000ison.dev.simpleclans2.claiming.tax.TaxesTask;
import com.p000ison.dev.sqlapi.Database;
import com.p000ison.dev.sqlapi.query.PreparedSelectQuery;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a ClaimingManager
 */
public class ClaimingManager {

    private static final String POWER_KEY = "power", HOME_CHUNK_KEY = "homechunk", TAXES_PER_MEMBER = "taxes_per_member", KICK_PLAYER = "kick_player";
    private final SimpleClansClaiming plugin;

    private final Database database;
    private final ChunkCache<Claim> cache;

    private final TaxesTask taxesTask;

    private final SettingsManager settingsManager;

    private final PreparedSelectQuery<Claim> selectClaimByCoords, selectClaimsByClan;

    public ClaimingManager(SimpleClansClaiming plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
        settingsManager = new SettingsManager(plugin);
        database.registerTable(Claim.class);
        selectClaimByCoords = database.<Claim>select().from(Claim.class).where().preparedEquals("x").and().preparedEquals("y").and().preparedEquals("z").select().prepare();
        selectClaimsByClan = database.<Claim>select().from(Claim.class).where().preparedEquals("clan").select().prepare();

        this.cache = new ChunkCache<Claim>(10, 300, 60) {
            @Override
            public Claim load(ClaimLocation key) throws Exception {
                selectClaimByCoords.set(0, key.getX());
                selectClaimByCoords.set(1, key.getY());
                selectClaimByCoords.set(2, key.getZ());
                List<Claim> results = selectClaimByCoords.getResults();
                return results.isEmpty() ? null : results.get(0);
            }
        };

        plugin.getServer().getScheduler().runTaskTimer(plugin, taxesTask = new TaxesTask(this), 0L, 20L);
    }

    public void clean() {
        cache.clean();
    }

    public Claim getClaimAt(ClaimLocation location) {
        return cache.getData(location);
    }

    public void loadClaims(Clan clan) {
        selectClaimsByClan.set(0, clan.getID());
        for (Claim claim : selectClaimsByClan.getResults()) {
            cache.load(new ClaimLocation(claim.getX(), claim.getZ(), claim.getY()), claim);
        }
    }

    public Set<Claim> getClaims(Clan clan) {
        Set<Claim> claims = new HashSet<Claim>();


        for (Claim claim : cache.getData().values()) {
            if (claim.getClanID() == clan.getID()) {
                claims.add(claim);
            }
        }

        return claims;
    }

    public void claimArea(Clan clan, ClaimLocation location) {
        Claim claim = new Claim(clan.getID(), location);
        database.insert(claim);
        cache.load(location, claim);
    }

    public Set<Claim> getSurroundingClaims(ClaimLocation chunkLocation) {
        Set<Claim> claims = new HashSet<Claim>();

        for (Claim claim : cache.getData().values()) {
            if (claim.getX() == chunkLocation.getX()
                    || claim.getX() + 1 == chunkLocation.getX()
                    || claim.getX() - 1 == chunkLocation.getX()

                    || claim.getY() == chunkLocation.getY()
                    || claim.getY() + 1 == chunkLocation.getY()
                    || claim.getY() - 1 == chunkLocation.getY()

                    || claim.getZ() == chunkLocation.getZ()
                    || claim.getZ() + 1 == chunkLocation.getZ()
                    || claim.getZ() - 1 == chunkLocation.getZ()) {
                claims.add(claim);
            }
        }

        return claims;
    }

    public boolean isSurrounding(ClaimLocation chunkLocation, Clan clan) {
        for (Claim claim : getSurroundingClaims(chunkLocation)) {
            if (claim.getClanID() == clan.getID()) {
                return true;
            }
        }

        return false;
    }

    public int getClaimAmount(Clan clan) {
        int i = 0;

        for (Claim claim : cache.getData().values()) {
            if (claim.getClanID() == clan.getID()) {
                i++;
            }
        }

        return i;
    }

    public Clan getClanAt(Location location) {
        Claim claim = getClaimAt(location);
        if (claim == null) {
            return null;
        }

        Clan clan = getClanManager().getClan(claim.getClanID());

        if (clan == null) {
            //delete claim
        }

        return clan;
    }

    public Clan getClanAt(ClaimLocation location) {
        Claim claim = getClaimAt(location);
        if (claim == null) {
            return null;
        }

        Clan clan = getClanManager().getClan(claim.getClanID());

        if (clan == null) {
            //delete claim
        }

        return clan;
    }

    public Claim getClaimAt(Location location) {
        return getClaimAt(toClaimLocation(location));
    }

    public ClaimLocation toClaimLocation(Location location) {
        return ClaimLocation.toClaimLocation(location, getIDByWorld(location.getWorld()), 16, 256);
    }

    public boolean isClanAt(Location location) {
        return getClanAt(location) != null;
    }

    public SCCore getCore() {
        return plugin.getCore();
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public ClanPlayerManager getClanPlayerManager() {
        return this.getCore().getClanPlayerManager();
    }

    public ClanManager getClanManager() {
        return this.getCore().getClanManager();
    }



    public int getIDByWorld(String world) {
        return settingsManager.getMapIDs().inverse().get(world);
    }

    public int getIDByWorld(World world) {
        return getIDByWorld(world.getName());
    }


    public static void setPower(ClanPlayer cp, double value) {
        PlayerFlags flags = cp.getFlags();

        flags.set(POWER_KEY, value);
    }

    public static double getPower(ClanPlayer cp) {
        PlayerFlags flags = cp.getFlags();

        return flags.getDouble(POWER_KEY);
    }

    public static void setKickPlayers(Clan clan, boolean kick) {
        ClanFlags flags = clan.getFlags();

        flags.setBoolean(KICK_PLAYER, kick);
    }

    public static boolean isKickPlayers(Clan clan) {
        ClanFlags flags = clan.getFlags();

        return flags.getBoolean(KICK_PLAYER);
    }

    public static void setTaxesPerMember(Clan clan, double value) {
        ClanFlags flags = clan.getFlags();

        flags.set(TAXES_PER_MEMBER, value);
    }

    public static double getTaxesPerMember(Clan clan) {
        ClanFlags flags = clan.getFlags();

        return flags.getDouble(TAXES_PER_MEMBER);
    }

    public static double getPower(Clan clan) {
        double power = 0;

        for (ClanPlayer cp : clan.getAllMembers()) {
            power += getPower(cp);
        }
        return power;
    }

    public static void setHomeChunk(Clan clan, ClaimLocation location) {
        ClanFlags flags = clan.getFlags();

        flags.setString(HOME_CHUNK_KEY, location.toString());
    }

    public static ClaimLocation getHomeChunk(Clan clan) {
        ClanFlags flags = clan.getFlags();
        return ClaimLocation.toLocation(flags.getString(HOME_CHUNK_KEY));
    }
}
