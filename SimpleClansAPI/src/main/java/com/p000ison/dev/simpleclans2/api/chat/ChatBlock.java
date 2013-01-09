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
 *     Last modified: 09.01.13 19:15
 */

package com.p000ison.dev.simpleclans2.api.chat;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A ChatBlock is used to send tables with rows and columns to a  {@link org.bukkit.command.CommandSender}.
 *
 * @author p000ison
 * @author phaed420
 */
public class ChatBlock {

    private static final int COLUMN_SPACING = 12;
    private static final int MAX_LINE_LENGTH = 315;

    private static String prefix = null;
    private static ChatColor headColor, subColor;

    private List<Row> rows = new ArrayList<Row>();
    private Align[] alignment = null;
    private double[] columnSizes = null;

    /**
     * Gets the align of the column.
     *
     * @param column The column.
     * @return The align of this column.
     */
    private Align getAlign(int column)
    {
        if (alignment == null) {
            return null;
        }

        return alignment[column];
    }

    /**
     * Gets the max-width of a column
     *
     * @param col The index of the column.
     * @return The width
     */
    public double getMaxWidth(int col)
    {
        double maxWidth = 0;

        for (Row row : rows) {
            StringBuilder[] columns = row.getColumns();
            if (col < columns.length) {
                maxWidth = Math.max(maxWidth, msgLength(columns[col]));
            }
        }

        return maxWidth;
    }

    /**
     * Generates the sizes of each column.
     */
    private void generateColumnSizes()
    {
        if (columnSizes == null) {
            // generate columns sizes

            int col_count = rows.get(0).getLenght();

            columnSizes = new double[col_count];

            for (int i = 0; i < col_count; i++) {
                // add custom column spacing if specified

                columnSizes[i] = getMaxWidth(i) + COLUMN_SPACING;
            }
        }
    }

    /**
     * Ads a row to this block.
     *
     * @param sections An array of the sections of this row.
     */
    public void addRow(Object... sections)
    {
        StringBuilder[] builderSections = new StringBuilder[sections.length];

        for (int i = 0; i < sections.length; i++) {
            Object toAdd = sections[i];

            if (toAdd == null) {
                throw new IllegalArgumentException(String.format("No argument of a row can be null! (Index: %s)", i));
            }

            builderSections[i] = new StringBuilder(toAdd.toString());
        }

        rows.add(new Row(builderSections));
    }

    public void addRow(Row row)
    {
        rows.add(row);
    }

    /**
     * Sends the complete block to a {@link org.bukkit.command.CommandSender}.
     *
     * @param sender The retriever.
     * @return Weather is was successfully.
     * @throws IllegalArgumentException If there are no rows added, or the alignment is miss-setted.
     */
    public boolean sendBlock(CommandSender sender)
    {
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("No rows added!");
        } else if (alignment == null) {
            throw new IllegalArgumentException("You have to set the alignment first!");
        }

        generateColumnSizes();

        int firstRowLength = rows.get(0).getLenght();

        if (alignment.length != firstRowLength) {
            throw new IllegalArgumentException(String.format("The number of alignments must equal the number of sections! %s != %s", alignment.length, firstRowLength));
        }

        if (columnSizes.length != firstRowLength) {
            throw new IllegalArgumentException(String.format("The number of alignments must equal the number of sections! %s != %s", columnSizes.length, firstRowLength));
        }

        for (Row row : rows) {
            StringBuilder[] columns = row.getColumns();
            StringBuilder finalRow = new StringBuilder();

            for (int column = 0; column < row.getLenght(); column++) {
                StringBuilder section = columns[column];
                double columnSize = columnSizes[column];
                Align align = getAlign(column);

                if (align == null) {
                    align = Align.LEFT;
                }

                double sectionLength = msgLength(section);

                switch (align) {
                    case RIGHT:
                        if (sectionLength > columnSize) {
                            cropLeft(section, columnSize);
                        } else if (sectionLength < columnSize) {
                            padLeft(section, columnSize);
                        }
                        break;
                    case LEFT:
                        if (sectionLength > columnSize) {
                            cropRight(section, columnSize);
                        } else if (sectionLength < columnSize) {
                            padRight(section, columnSize);
                        }
                        break;
                    case CENTER:
                        if (sectionLength > columnSize) {
                            cropRight(section, columnSize);
                        } else if (sectionLength < columnSize) {
                            center(section, columnSize);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Align not found!");
                }

                finalRow.append(section).append(ChatColor.RESET);
            }

            cropRight(finalRow, MAX_LINE_LENGTH);

            ChatBlock.sendMessage(sender, finalRow.toString());
        }
        return true;
    }

    /**
     * Sets the alignment of each row.
     * <p/>
     * <p>Example:</p>
     * <p/>
     * block.setAlignment(Align.LEFT, Align.RIGHT);
     * <p/>
     * <p>This will produce something like this:</p>
     * <p/>
     * |TestString    |    TextString|
     *
     * @param alignment An array of alignments
     * @see com.p000ison.dev.simpleclans2.api.chat.Align
     */
    public void setAlignment(Align... alignment)
    {
        this.alignment = alignment;
    }

    public void clear()
    {
        this.rows.clear();
    }

    /**
     * Crops the string right in the {@link StringBuilder}.
     *
     * @param text   The message to crop right.
     * @param length The lenght it of the section it should be crop right.
     */
    public static void cropRight(StringBuilder text, double length)
    {
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException("The text can not be null or empty!");
        }

        while (msgLength(text) >= length) {
            text.deleteCharAt(text.length() - 1);
        }
    }


    /**
     * Crops the string left in the {@link StringBuilder}.
     *
     * @param text   The message to crop left.
     * @param length The lenght it of the section it should be crop left.
     */
    public static void cropLeft(StringBuilder text, double length)
    {
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException("The text can not be null or empty!");
        }

        while (msgLength(text) >= length) {
            text.deleteCharAt(0);
        }
    }

    /**
     * Pads the string right in the {@link StringBuilder}.
     *
     * @param text   The message to pad right.
     * @param length The lenght it of the section it should be pad right.
     */
    public static void padRight(StringBuilder text, double length)
    {
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException("The text can not be null or empty!");
        }

        double msgLenght = msgLength(text);

        if (msgLenght > length) {
            return;
        }

        while (msgLenght < length) {
            msgLenght += 4;
            text.append(' ');
        }
    }

    /**
     * Pads the string left in the {@link StringBuilder}.
     *
     * @param text   The message to pad left.
     * @param length The lenght it of the section it should be pad left.
     */
    public static void padLeft(StringBuilder text, double length)
    {
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException("The text can not be null or empty!");
        }

        double msgLength = msgLength(text);

        if (msgLength > length) {
            return;
        }

        StringBuilder empty = new StringBuilder();

        while (msgLength < length) {
            msgLength += 4;
            empty.append(' ');
        }

        text.insert(0, empty);
    }

    /**
     * Centers the string in the {@link StringBuilder}.
     *
     * @param text       The message to center.
     * @param lineLength The lenght it of the section it should be centered in.
     */
    public static void center(StringBuilder text, double lineLength)
    {
        double length = msgLength(text);
        double diff = lineLength - length;

        // if too big for line return it as is

        if (diff < 0) {
            return;
        }

        double sideSpace = diff / 2;

        // pad the left with space

        padLeft(text, length + sideSpace);

        // pad the right with space

        padRight(text, length + sideSpace + sideSpace);
    }

    /**
     * Returns the length of a string.
     *
     * @param text The text to check.
     * @return The length of the string.
     */
    public static int msgLength(StringBuilder text)
    {
        int length = 0;

        // Loop through all the characters, skipping any color characters and their following color codes

        int textLength = text.length() - 1;

        for (int x = 0; x < text.length(); x++) {
            char currentChar = text.charAt(x);

            //ignore colors, but only if there is enought space. A § at the end of the line will not be recognized
            if (currentChar == '\u00a7' && x < textLength) {
                char nextChar = text.charAt(x + 1);
                if (ChatColor.getByChar(nextChar) == null) {
                    continue;
                }
            }

            int len = charLength(currentChar);
            if (len > 0) {
                length += len;
            } else {
                x++;
            }
        }
        return length;
    }

    /**
     * Returns the length of a char
     *
     * @param character The character to check
     * @return The lenght of the char
     */
    public static int charLength(char character)
    {
        if ("i.:,;|!".indexOf(character) != -1) {
            return 2;
        } else if ("l'".indexOf(character) != -1) {
            return 3;
        } else if ("tI[]".indexOf(character) != -1) {
            return 4;
        } else if ("fk{}<>\"*()".indexOf(character) != -1) {
            return 5;
        } else if ("abcdeghjmnopqrsuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890\\/#?$%-=_+&^".indexOf(character) != -1) {
            return 6;
        } else if ("@~".indexOf(character) != -1) {
            return 7;
        } else if (character == ' ') {
            return 4;
        } else {
            return -1;
        }
    }

    /**
     * Sends a message to a player.
     *
     * @param sender  The retriever
     * @param message The message
     */
    public static void sendMessage(CommandSender sender, String message)
    {
        if (prefix != null) {
            message = prefix + message;
        }

        sender.sendMessage(message);
    }

    /**
     * Sends messages to a player
     *
     * @param sender   The retriever
     * @param messages The message
     */
    public static void sendMessage(CommandSender sender, String... messages)
    {
        for (int i = 0; i < messages.length; i++) {
            String message = messages[i];
            if (prefix != null) {
                messages[i] = prefix + message;
            }

            ChatBlock.sendMessage(sender, message);
        }
    }

    /**
     * Sends a blank line.
     *
     * @param receiver The retriever.
     */
    public static void sendBlank(CommandSender receiver)
    {
        receiver.sendMessage("");
    }

    /**
     * Sends a blank line.
     *
     * @param receiver The retriever.
     * @param amount   How oftern this should be sent
     */
    public static void sendBlank(CommandSender receiver, int amount)
    {
        for (int i = 0; i < amount; i++) {
            sendBlank(receiver);
        }
    }

    /**
     * Sends a single line line to the player. This will crop the overflow right.
     *
     * @param receiver The retriever
     * @param message  The message.
     */
    public static void sendSingle(CommandSender receiver, String message)
    {
        receiver.sendMessage("");
    }

    /**
     * Sets the prefix for messages
     *
     * @param prefix The prefix to set or <strong>null</strong> if you do not like one
     */
    public static void setPrefix(String prefix)
    {
        ChatBlock.prefix = prefix;
    }

    /**
     * Convert color hex values
     *
     * @param text The message to colorize
     * @return The colored string
     */
    public static String parseColors(String text)
    {
        if (text == null) {
            return null;
        } else if (text.isEmpty()) {
            return text;
        }

        StringBuilder tempTextBuilder = new StringBuilder(text);

        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (character == '&') {
                int nextChar = i + 1;
                if (nextChar < text.length()) {
                    char colorChar = text.charAt(nextChar);

                    if (colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102) {
                        tempTextBuilder.setCharAt(i, '\u00a7');
                    }
                }
            }
        }

        return tempTextBuilder.toString();
    }

    /**
     * Sends a header to a {@link org.bukkit.command.CommandSender}.
     *
     * @param sender   The retriever
     * @param head     The head
     * @param subtitle The sub-title
     */
    public static void sendHead(CommandSender sender, String head, String subtitle)
    {
        StringBuilder header = new StringBuilder(head).append(' ');

        if (subtitle != null) {
            if (subColor != null) {
                header.append(subColor);
            }

            header.append(subtitle).append(' ');
        }

        if (headColor != null) {
            header.append(headColor);
        }

        while (msgLength(header) < MAX_LINE_LENGTH) {
            header.append('-');
        }

        StringBuilder sb = new StringBuilder();

        while (msgLength(sb) < MAX_LINE_LENGTH) {
            sb.append('a');
        }

        ChatBlock.sendMessage(sender, header.toString());
    }


    /**
     * Sets the color of the header
     *
     * @param headColor The color to set
     */
    public static void setHeadColor(ChatColor headColor)
    {
        ChatBlock.headColor = headColor;
    }

    /**
     * Sets the sub-color of a header
     *
     * @param subColor The color to set
     */
    public static void setSubColor(ChatColor subColor)
    {
        ChatBlock.subColor = subColor;
    }

    /**
     * Gets the heading color
     *
     * @return The heading color
     */
    public static ChatColor getHeadingColor()
    {
        return headColor;
    }

    /**
     * Gets the sub color in a header
     *
     * @return The sub color
     */
    public static ChatColor getSubPageColor()
    {
        return subColor;
    }

    /**
     * Gets the last color in this string, starting at a specific index
     *
     * @param input      The input string
     * @param from       Where to start or -1 if you want to start at the end of the string
     * @param colorChars The color characters, in most cases § or &
     * @return The color in a {@link org.bukkit.ChatColor} instance
     */
    public static ChatColor getLastColors(String input, int from, char... colorChars)
    {
        Validate.notNull(input, "The input must not be null!");
        Validate.notEmpty(input, "The input must not be empty!");

        ChatColor result = null;
        int maxIndex;

        if (from == -1) {
            maxIndex = input.length() - 1;
        } else {
            maxIndex = from;
        }

        boolean finished = false;

        // Iterate backwards to find the last
        for (int i = maxIndex; i >= 0 && !finished; i--) {
            char current = input.charAt(i);
            for (char colorChar : colorChars) {
                if (current == colorChar) {
                    if (i + 1 > maxIndex) {
                        continue;
                    }

                    char color = input.charAt(i + 1);
                    ChatColor found = ChatColor.getByChar(color);

                    if (found != null) {
                        result = found;

                        if (found.isColor() || found == ChatColor.RESET) {
                            finished = true;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Cleans a string, removes colors and lower-cases it
     *
     * @param input The input to clean
     * @return The cleaned string
     */
    public static String cleanString(String input)
    {
        return ChatColor.stripColor(input).toLowerCase(Locale.US);
    }
}

