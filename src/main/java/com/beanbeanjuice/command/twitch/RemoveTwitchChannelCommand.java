package com.beanbeanjuice.command.twitch;

import com.beanbeanjuice.main.BeanBot;
import com.beanbeanjuice.utility.command.CommandContext;
import com.beanbeanjuice.utility.command.ICommand;
import com.beanbeanjuice.utility.command.usage.Usage;
import com.beanbeanjuice.utility.command.usage.categories.CategoryType;
import com.beanbeanjuice.utility.command.usage.types.CommandType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

/**
 * A command to remove a twitch channel.
 *
 * @author beanbeanjuice
 */
public class RemoveTwitchChannelCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx, ArrayList<String> args, User user, GuildMessageReceivedEvent event) {
        if (!BeanBot.getGeneralHelper().isModerator(event.getMember(), event.getGuild(), event)) {
            return;
        }

        if (!BeanBot.getGuildHandler().getCustomGuild(event.getGuild().getId()).removeTwitchChannel(args.get(0))) {
            event.getChannel().sendMessage(notAddedEmbed()).queue();
            return;
        }

        event.getChannel().sendMessage(successfulRemoveEmbed(args.get(0))).queue();
    }

    @NotNull
    private MessageEmbed successfulRemoveEmbed(@NotNull String twitchName) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Successfully Removed the Twitch Channel");
        embedBuilder.setColor(BeanBot.getGeneralHelper().getRandomColor());
        embedBuilder.setDescription("Successfully removed `" + twitchName + "`!");
        return embedBuilder.build();
    }

    @NotNull
    private MessageEmbed notAddedEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.red);
        embedBuilder.setDescription("Unable to remove the twitch channel. The twitch channel may not be added.");
        embedBuilder.setAuthor("Error Removing Twitch Channel");
        return embedBuilder.build();
    }

    @Override
    public String getName() {
        return "remove-twitch-channel";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("removetwitchchannel");
        return arrayList;
    }

    @Override
    public String getDescription() {
        return "Remove a twitch channel from the live channel!";
    }

    @Override
    public String exampleUsage() {
        return "`!!removetwitchchannel beanbeanjuice`";
    }

    @Override
    public Usage getUsage() {
        Usage usage = new Usage();
        usage.addUsage(CommandType.TEXT, "Twitch Channel Name", true);
        return usage;
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.TWITCH;
    }
}
