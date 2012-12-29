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

package com.p000ison.dev.simpleclans2.database.response;

import com.p000ison.dev.simpleclans2.util.Logging;

import java.util.LinkedList;

/**
 * Represents a ResponseTask
 */
public class ResponseTask extends LinkedList<Response> implements Runnable {

    private static final long serialVersionUID = 1L;

    @Override
    public void run()
    {
        Response response;


        while ((response = this.poll()) != null) {
            if (response.needsRetriever() && response.getRetriever() == null) {
                continue;
            }
            if (!response.response()) {
                Logging.debug("Failed to response response-able!");
            }
        }
    }
}
