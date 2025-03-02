package com.beanbeanjuice.utility.command.usage.types;

import org.jetbrains.annotations.NotNull;

/**
 * An enum used for handling {@link com.beanbeanjuice.utility.command.ICommand ICommand} errors.
 *
 * @author beanbeanjuice
 */
public enum CommandErrorType {

    NUMBER ("A *whole* number is required."),
    USER ("A mentioned user is required."),
    LINK ("A link is required."),
    ROLE ("A mentioned role is required."),
    DATE ("A date is required. Use the format (MM-DD). Basically, a month and year are required."),
    TEXT_CHANNEL ("A text channel ID is required. You can right click on a text channel and press copy ID, or you can use #example-channel."),
    VOICE_CHANNEL ("A voice channel ID is required. You can right click on a voice channel and press copy ID. Please google for more information."),
    LANGUAGE_CODE ("A language code is required. Correct language codes are `en_US` - English, `hi` - Hindi, `es` - Spanish, `fr` - French," +
            " `ja` - Japanese, `ru` - Russian, `en_GB` - English (UK), `de` - German, `it` - Italian, `ko` - Korean, `pt-BR` - Brazilian Portuguese, `ar` - Arabic, `tr` - Turkish."),
    SUCCESS ("Successful command."),
    TOO_MANY_ARGUMENTS ("There are too many arguments."),
    NOT_ENOUGH_ARGUMENTS ("There are not enough arguments.");

    private final String description;

    /**
     * Creates a new instance of the {@link CommandErrorType}.
     * @param description The error message.
     */
    CommandErrorType(@NotNull String description) {
        this.description = description;
    }

    /**
     * @return The description of the {@link CommandErrorType}.
     */
    @NotNull
    public String getDescription() {
        return description;
    }

}
