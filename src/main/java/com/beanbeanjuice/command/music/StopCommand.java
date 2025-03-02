package com.beanbeanjuice.command.music;

import com.beanbeanjuice.CafeBot;
import com.beanbeanjuice.utility.command.CommandContext;
import com.beanbeanjuice.utility.command.ICommand;
import com.beanbeanjuice.utility.command.usage.Usage;
import com.beanbeanjuice.utility.command.usage.categories.CategoryType;
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
 * A command used for stopping the queue and making the bot leave.
 *
 * @author beanbeanjuice
 */
public class StopCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx, ArrayList<String> args, User user, GuildMessageReceivedEvent event) {
        ctx.getCustomGuild().setLastMusicChannel(event.getChannel());

        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inVoiceChannel()) {
            event.getChannel().sendMessageEmbeds(botMustBeInVoiceChannelEmbed()).queue();
            return;
        }

        if (!event.getMember().getVoiceState().inVoiceChannel()) {
            event.getChannel().sendMessageEmbeds(userMustBeInVoiceChannelEmbed()).queue();
            return;
        }

        if (!event.getMember().getVoiceState().getChannel().equals(self.getVoiceState().getChannel())) {
            event.getChannel().sendMessageEmbeds(userMustBeInSameVoiceChannelEmbed()).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();
        CafeBot.getGuildHandler().getCustomGuild(event.getGuild()).getCustomGuildSongQueue().clear();
        ctx.getGuild().getAudioManager().closeAudioConnection();

        // Sends that the bot successfully left the channel.
        event.getChannel().sendMessageEmbeds(successEmbed()).queue();
    }

    @NotNull
    private MessageEmbed botMustBeInVoiceChannelEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("I'm not currently in a voice channel.");
        embedBuilder.setColor(Color.red);
        return embedBuilder.build();
    }

    @NotNull
    private MessageEmbed userMustBeInSameVoiceChannelEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Sorry, you must be in the same voice channel as the bot to use this command.");
        embedBuilder.setColor(Color.red);
        return embedBuilder.build();
    }

    @NotNull
    private MessageEmbed userMustBeInVoiceChannelEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Sorry, you must be in a voice channel to use this command.");
        embedBuilder.setColor(Color.red);
        return embedBuilder.build();
    }

    @NotNull
    private MessageEmbed successEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Successfully removed tracks and left the call.");
        embedBuilder.setColor(Color.green);

        return embedBuilder.build();
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("leave");
        return arrayList;
    }

    @Override
    public String getDescription() {
        return "Stop the current music playing and make the bot leave.";
    }

    @Override
    public String exampleUsage(String prefix) {
        return "`" + prefix + "stop`";
    }

    @Override
    public Usage getUsage() {
        return new Usage();
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.MUSIC;
    }

}