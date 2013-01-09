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
 *     Last modified: 09.01.13 20:42
 */

package com.p000ison.dev.simpleclans2.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public interface Flags extends Serializable {
    Map<String, Object> getData();

    void deserialize(String jsonString);

    String serialize();

    boolean hasFlags();

    boolean getBoolean(String key);

    double getDouble(String key);

    byte getByte(String key);

    short getShort(String key);

    long getLong(String key);

    int getInteger(String key);

    float getFloat(String key);

    char getChar(String key);

    boolean removeEntry(String key);

    <T> Set<T> getSet(String key);

    Set<String> getStringSet(String key);

    void set(String key, Object obj);

    void setBoolean(String key, boolean bool);

    String getString(String key);

    void setString(String key, String value);

    Object get(String key);
}
