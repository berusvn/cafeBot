package com.beanbeanjuice.utility.guild;

import org.jetbrains.annotations.NotNull;

/**
 * An enum used for {@link CustomChannel}.
 */
public enum CustomChannel {

    LIVE("Live Channel"),
    UPDATE("Update Channel"),
    COUNTING("Counting Channel"),
    POLL("Poll Channel"),
    RAFFLE("Raffle Channel"),
    BIRTHDAY("Birthday Channel"),
    WELCOME("Welcome Channel"),
    LOG("Log Channel"),
    VENTING("Venting Channel"),
    DAILY("Daily Channel");

    private final String name;

    /**
     * Creates a new {@link CustomChannel} object.
     * @param name The name of the enum.
     */
    CustomChannel(@NotNull String name) {
        this.name = name;
    }

    /**
     * @return The formatted name of the enum.
     */
    public String getName() {
        return name;
    }

}
