package com.beanbeanjuice.command.music;

import com.beanbeanjuice.main.BeanBot;
import com.beanbeanjuice.utility.command.CommandContext;
import com.beanbeanjuice.utility.command.ICommand;
import com.beanbeanjuice.utility.command.usage.Usage;
import com.beanbeanjuice.utility.command.usage.categories.CategoryType;
import com.beanbeanjuice.utility.lavaplayer.GuildMusicManager;
import com.beanbeanjuice.utility.lavaplayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
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
 * The command used for seeing which song is currently playing.
 *
 * @author beanbeanjuice
 */
public class NowPlayingCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx, ArrayList<String> args, User user, GuildMessageReceivedEvent event) {
        BeanBot.getGuildHandler().getCustomGuild(event.getGuild()).setLastMusicChannel(event.getChannel());

        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inVoiceChannel()) {
            event.getChannel().sendMessage(botMustBeInVoiceChannelEmbed()).queue();
            return;
        }

        if (selfVoiceState.inVoiceChannel()) {

            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
            AudioPlayer audioPlayer = musicManager.audioPlayer;
            final AudioTrack audioTrack = audioPlayer.getPlayingTrack();

            if (audioTrack == null) {
                event.getChannel().sendMessage(noTrackPlaying()).queue();
                return;
            }

            final AudioTrackInfo info = audioTrack.getInfo();
            event.getChannel().sendMessage(nowPlaying(info.title, info.author, info.uri)).queue();

        }
    }

    @NotNull
    private MessageEmbed nowPlaying(@NotNull String title, @NotNull String author, @NotNull String url) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Now Playing", url);
        String message = String.format("`%s` by `%s`", title, author);
        embedBuilder.setDescription(message);
        embedBuilder.setColor(Color.cyan);
        return embedBuilder.build();
    }

    @NotNull
    private MessageEmbed noTrackPlaying() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("There is no track currently playing.");
        embedBuilder.setColor(Color.orange);
        return embedBuilder.build();
    }

    @NotNull
    private MessageEmbed botMustBeInVoiceChannelEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("I'm not currently playing music.");
        embedBuilder.setColor(Color.red);
        return embedBuilder.build();
    }

    @Override
    public String getName() {
        return "now-playing";
    }

    @Override
    public ArrayList<String> getAliases() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("np");
        arrayList.add("nowplaying");
        return arrayList;
    }

    @Override
    public String getDescription() {
        return "Shows which song is currently playing.";
    }

    @Override
    public String exampleUsage() {
        return "`!!nowplaying`";
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
