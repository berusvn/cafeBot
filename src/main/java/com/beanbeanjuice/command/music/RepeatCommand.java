package com.beanbeanjuice.command.music;

import com.beanbeanjuice.CafeBot;
import com.beanbeanjuice.utility.command.CommandContext;
import com.beanbeanjuice.utility.command.ICommand;
import com.beanbeanjuice.utility.command.usage.Usage;
import com.beanbeanjuice.utility.command.usage.categories.CategoryType;
import com.beanbeanjuice.utility.command.usage.types.CommandType;
import com.beanbeanjuice.utility.sections.music.lavaplayer.GuildMusicManager;
import com.beanbeanjuice.utility.sections.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

/**
 * The command used for repeating the current song.
 *
 * @author beanbeanjuice
 */
public class RepeatCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx, ArrayList<String> args, User user, GuildMessageReceivedEvent event) {
        if (args.isEmpty()) {
            if (CafeBot.getGuildHandler().getCustomGuild(event.getGuild()).getCustomGuildSongQueue().getSongRepeating()) {
                event.getChannel().sendMessage(CafeBot.getGeneralHelper().successEmbed(
                        "Repeat Status",
                        "You currently have song repeating enabled."
                )).queue();
                return;
            } else if (CafeBot.getGuildHandler().getCustomGuild(event.getGuild()).getCustomGuildSongQueue().getPlaylistRepeating()) {
                event.getChannel().sendMessage(CafeBot.getGeneralHelper().successEmbed(
                        "Repeat Status",
                        "You currently have playlist repeating enabled."
                )).queue();
                return;
            } else {
                event.getChannel().sendMessage(CafeBot.getGeneralHelper().successEmbed(
                        "Repeat Status",
                        "Playlist and song repeating are both disabled. Do `" + ctx.getPrefix() + "help repeat` for more information."
                )).queue();
                return;
            }
        }

        String commandName = args.get(0).toLowerCase();

        if (!commandName.equals("song") && !commandName.equals("playlist")) {
            event.getChannel().sendMessage(CafeBot.getGeneralHelper().errorEmbed(
                    "Incorrect Command Term",
                    "You must use the command term `song` or `playlist` not `" + commandName + "`."
            )).queue();
            return;
        }

        ctx.getCustomGuild().setLastMusicChannel(event.getChannel());

        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inVoiceChannel()) {
            event.getChannel().sendMessage(botMustBeInVoiceChannelEmbed()).queue();
            return;
        }

        if (!event.getMember().getVoiceState().inVoiceChannel()) {
            event.getChannel().sendMessage(mustBeInVoiceChannelEmbed()).queue();
            return;
        }

        if (!event.getMember().getVoiceState().getChannel().equals(selfVoiceState.getChannel())) {
            event.getChannel().sendMessage(userMustBeInSameVoiceChannelEmbed()).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

        if (commandName.equals("song")) {
            final boolean newRepeating = !CafeBot.getGuildHandler().getCustomGuild(event.getGuild()).getCustomGuildSongQueue().getSongRepeating();
            CafeBot.getGuildHandler().getCustomGuild(event.getGuild()).getCustomGuildSongQueue().setSongRepeating(newRepeating);

            if (newRepeating) {
                event.getChannel().sendMessage(successEmbed("Song repeating has now been turned on.")).queue();
            } else {
                event.getChannel().sendMessage(successEmbed("Song repeating has now been turned off.")).queue();
            }
        }

        if (commandName.equals("playlist")) {

            if (!CafeBot.getGuildHandler().getCustomGuild(event.getGuild()).getCustomGuildSongQueue().getPlaylistRepeating() &&
                    CafeBot.getGuildHandler().getCustomGuild(event.getGuild()).getCustomGuildSongQueue().getCustomSongQueue().isEmpty()) {
                event.getChannel().sendMessage(CafeBot.getGeneralHelper().errorEmbed(
                        "Cannot Repeat Playlist",
                        "Cannot repeat the playlist as the playlist is currently empty."
                )).queue();
                return;
            }

            final boolean newRepeating = !CafeBot.getGuildHandler().getCustomGuild(event.getGuild()).getCustomGuildSongQueue().getPlaylistRepeating();
            CafeBot.getGuildHandler().getCustomGuild(event.getGuild()).getCustomGuildSongQueue().setPlaylistRepeating(newRepeating);

            if (newRepeating) {
                event.getChannel().sendMessage(successEmbed("Playlist repeating has now been turned on.")).queue();
            } else {
                event.getChannel().sendMessage(successEmbed("Playlist repeating has now been turned off.")).queue();
            }
        }

    }

    @NotNull
    private MessageEmbed userMustBeInSameVoiceChannelEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Sorry, you must be in the same voice channel as the bot to use this command.");
        embedBuilder.setColor(Color.red);
        return embedBuilder.build();
    }

    @NotNull
    private MessageEmbed botMustBeInVoiceChannelEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("I'm not currently in a voice channel.");
        embedBuilder.setColor(Color.red);
        return embedBuilder.build();
    }

    @NotNull
    private MessageEmbed mustBeInVoiceChannelEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Sorry, you must be in a voice channel to use this command.");
        embedBuilder.setColor(Color.red);
        return embedBuilder.build();
    }

    @NotNull
    private MessageEmbed successEmbed(@NotNull String message) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(message);
        embedBuilder.setColor(Color.green);
        return embedBuilder.build();
    }

    @Override
    public String getName() {
        return "repeat";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("loop");
        return arrayList;
    }

    @Override
    public String getDescription() {
        return "Repeat the current song playing.";
    }

    @Override
    public String exampleUsage(String prefix) {
        return "`" + prefix + "repeat playlist` or `" + prefix + "repeat song`";
    }

    @Override
    public Usage getUsage() {
        Usage usage = new Usage();
        usage.addUsage(CommandType.TEXT, "Song/Playlist", false);
        return usage;
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.MUSIC;
    }

}
