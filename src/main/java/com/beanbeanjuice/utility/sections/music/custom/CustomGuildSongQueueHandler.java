package com.beanbeanjuice.utility.sections.music.custom;

import com.beanbeanjuice.CafeBot;
import com.beanbeanjuice.utility.sections.music.lavaplayer.GuildMusicManager;
import com.beanbeanjuice.utility.sections.music.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

/**
 * A {@link CustomGuildSongQueueHandler} class for handling {@link CustomSong} in a {@link net.dv8tion.jda.api.entities.Guild Guild}.
 *
 * @author beanbeanjuice
 */
public class CustomGuildSongQueueHandler {

    private final String guildID;
    private ArrayList<CustomSong> customSongQueue;
    private ArrayList<CustomSong> unshuffledQueue;
    private ArrayList<CustomSong> repeatQueue;
    private CustomSong currentSong;
    private Stack<CustomSong> songOrderStack;
    private boolean songPlaying = false;
    private boolean playlistRepeat = false;
    private boolean songRepeat = false;
    private boolean shuffle = false;
    private Timer audioTimer;
    private TimerTask audioTimerTask;
    private boolean loadHasFailed = false;
    private boolean fromTrackStuck = false;

    /**
     * Creates a new {@link CustomGuildSongQueueHandler} object.
     * @param guildID The ID for that {@link net.dv8tion.jda.api.entities.Guild Guild}.
     */
    public CustomGuildSongQueueHandler(@NotNull String guildID) {
        this.guildID = guildID;
        customSongQueue = new ArrayList<>();
        unshuffledQueue = new ArrayList<>();
        repeatQueue = new ArrayList<>();
        songOrderStack = new Stack<>();
    }

    /**
     * @return The {@link Stack} of {@link CustomSong} that were added.
     */
    public Stack<CustomSong> getSongOrderStack() {
        return songOrderStack;
    }

    /**
     * Reorders the last {@link CustomSong} in the queue.
     */
    public void moveLast() {
        CustomSong lastSong = songOrderStack.pop();
        customSongQueue.remove(lastSong);
        customSongQueue.add(0, lastSong);
    }

    /**
     * @return The length of the current song queue in milliseconds.
     */
    @NotNull
    public Long getQueueLengthMS() {
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(CafeBot.getGuildHandler().getGuild(guildID));
        if (customSongQueue.isEmpty() && musicManager.audioPlayer.getPlayingTrack() == null) {
            return 0L;
        }

        long totalTime = 0;

        if (musicManager.audioPlayer.getPlayingTrack() != null) {
            totalTime += musicManager.audioPlayer.getPlayingTrack().getDuration() - musicManager.audioPlayer.getPlayingTrack().getPosition();
        }

        for (CustomSong customSong : customSongQueue) {
            totalTime += customSong.getLengthMS();
        }
        return totalTime;
    }

    /**
     * Adds a custom song to the {@link ArrayList<CustomSong>}.
     * @param customSong The {@link CustomSong} to add.
     * @param fromRepeat Whether or not the {@link CustomSong} was added from the repeat mechanism.
     */
    public void addCustomSong(@NotNull CustomSong customSong, @NotNull Boolean fromRepeat) {

        // Adds the song to the "undo" stack.
        songOrderStack.add(customSong);

        // Adds the song randomly if needed.
        if (shuffle) {
            customSongQueue.add(CafeBot.getGeneralHelper().getRandomNumber(0, customSongQueue.size()), customSong);
        } else {
            customSongQueue.add(customSong);
        }
        unshuffledQueue.add(customSong);

        if (!fromRepeat && playlistRepeat) {
            repeatQueue.add(customSong);
        }

        if (!songPlaying) {
            queueNextSong();
        }
    }

    /**
     * Adds a song to the front of the queue.
     * @param customSong The {@link CustomSong} to be added.
     */
    public void reAddSong(@NotNull CustomSong customSong) {
        customSongQueue.add(0, customSong);
        loadHasFailed = true;
    }

    /**
     * Adds a song to the front of the queue.
     * @param customSong The {@link CustomSong} to be added.
     * @param fromTrackStuck Whether or not re-adding was because the {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack AudioTrack} was stuck.
     */
    public void reAddSong(@NotNull CustomSong customSong, @NotNull Boolean fromTrackStuck) {
        /*
         * If the track is already stuck, do nothing. This is to prevent
         * the track from being completely stuck over and over again.
         *
         * If the track wasn't stuck, and it is now, then
         * re-add the song and make sure the trackStuck variable is true.
         */
        if (!this.fromTrackStuck) {
            if (fromTrackStuck) {
                this.fromTrackStuck = true;
                reAddSong(customSong);
            }
        }
    }

    /**
     * Queues up the next {@link CustomSong}.
     */
    public void queueNextSong() {
        try {
            songPlaying = true;
            currentSong = customSongQueue.remove(0);
            unshuffledQueue.remove(currentSong);
            PlayerManager.getInstance().loadAndPlay(currentSong.getSearchString(), CafeBot.getGuildHandler().getGuild(guildID));

            // If the load has failed, don't say queueing next song.
            if (!loadHasFailed) {
                try {
                    CafeBot.getGuildHandler().getCustomGuild(guildID).getLastMusicChannel().sendMessage(nowPlaying()).queue();
                } catch (NullPointerException ignored) {
                }
            }

            // Resets the `loadHasFailed` variable.
            // Resets the `fromTrackStuck` variable.
            loadHasFailed = false;
            fromTrackStuck = false;
        } catch (IndexOutOfBoundsException e) {
            if (playlistRepeat) {
                songPlaying = false;
                for (CustomSong customSong : repeatQueue) {
                    addCustomSong(customSong, true);
                }
            } else {
                currentSong = null;
            }
        }

        if (currentSong == null && customSongQueue.isEmpty()) {
            songPlaying = false;
        }
    }

    @NotNull
    private MessageEmbed nowPlaying() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Now Playing");
        embedBuilder.setDescription("`" + currentSong.getName() + "` by `" + currentSong.getAuthor() + "` [`"
                + currentSong.getLengthString() + "`]\n\n" +
                "**Requested By**: " + currentSong.getRequester().getAsMention());
        embedBuilder.setColor(Color.cyan);
        embedBuilder.setFooter("Songs Left: " + customSongQueue.size());
        return embedBuilder.build();
    }

    /**
     * Clears everything in the {@link CustomGuildSongQueueHandler}.
     */
    public void clear() {
        customSongQueue.clear();
        repeatQueue.clear();
        unshuffledQueue.clear();
        playlistRepeat = false;
        songRepeat = false;
        shuffle = false;
        currentSong = null;
        songPlaying = false;
        audioTimer.cancel();
        audioTimerTask.cancel();

        // Unpauses the Music Player
        PlayerManager.getInstance().getMusicManager(CafeBot.getGuildHandler().getGuild(guildID)).audioPlayer.setPaused(false);
    }

    /**
     * Sets the shuffle state for the {@link CustomGuildSongQueueHandler}.
     * @param bool The {@link Boolean} to set the shuffle state to.
     */
    public void setShuffle(@NotNull Boolean bool) {
        shuffle = bool;

        if (shuffle) {
            unshuffledQueue = new ArrayList<>(customSongQueue);
            Collections.shuffle(customSongQueue);
        } else {
            customSongQueue = new ArrayList<>(unshuffledQueue);
            unshuffledQueue.clear();
        }
    }

    /**
     * @return The current shuffle state for the {@link CustomGuildSongQueueHandler}.
     */
    @NotNull
    public Boolean getShuffle() {
        return shuffle;
    }

    /**
     * Sets the playlist repeat state for the {@link CustomGuildSongQueueHandler}.
     * @param bool The {@link Boolean} to set the playlist repeat state to.
     */
    public void setPlaylistRepeating(@NotNull Boolean bool) {
        playlistRepeat = bool;

        if (playlistRepeat) {
            repeatQueue = new ArrayList<>(customSongQueue);
            repeatQueue.add(0, currentSong);
            songRepeat = false;
        } else {
            repeatQueue.clear();
        }
    }

    /**
     * @return The current playlist repeat state for the {@link CustomGuildSongQueueHandler}.
     */
    @NotNull
    public Boolean getPlaylistRepeating() {
        return playlistRepeat;
    }

    /**
     * @return The current song repeat state for the {@link CustomGuildSongQueueHandler}.
     */
    @NotNull
    public Boolean getSongRepeating() {
        return songRepeat;
    }

    /**
     * Sets the song repeat state for the {@link CustomGuildSongQueueHandler}.
     * @param bool The {@link Boolean} to set the song repeat state to.
     */
    public void setSongRepeating(@NotNull Boolean bool) {
        songRepeat = bool;

        if (songRepeat) {
            playlistRepeat = false;
            repeatQueue.clear();
        }
    }

    /**
     * @return The current {@link CustomSong} playing.
     */
    public CustomSong getCurrentSong() {
        return currentSong;
    }

    /**
     * @return The current {@link CustomSong} queue.
     */
    public ArrayList<CustomSong> getCustomSongQueue() {
        return customSongQueue;
    }

    /**
     * Sets the song playing state for the {@link CustomGuildSongQueueHandler}.
     * @param bool The {@link Boolean} to set the song playing state to.
     */
    public void setSongPlayingStatus(@NotNull Boolean bool) {
        songPlaying = bool;
    }

    /**
     * Starts checking for an {@link net.dv8tion.jda.internal.audio.AudioConnection AudioConnection} in the current {@link Guild}.
     */
    public void startAudioChecking() {

        if (audioTimer != null) {
            audioTimer.cancel();
            audioTimer = null;
            audioTimerTask.cancel();
            audioTimerTask = null;
        }

        audioTimer = new Timer();
        int secondsToLeave = 300;
        audioTimerTask = new TimerTask() {

            int seconds = 0;
            final Guild guild = CafeBot.getGuildHandler().getGuild(guildID);
            final Member selfMember = guild.getSelfMember();
            final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(CafeBot.getGuildHandler().getGuild(guildID));

            // Takes all of the necessary precautions to disconnect and leave the voice channel.
            public void disconnectFromChannel() {
                musicManager.scheduler.player.stopTrack();
                musicManager.scheduler.queue.clear();
                clear();
                guild.getAudioManager().closeAudioConnection();
                musicManager.scheduler.inVoiceChannel = false;
            }

            @Override
            public void run() {
                boolean voicePassed = false;
                boolean queuePassed = false;
                seconds += 1;

                GuildVoiceState selfVoiceState = selfMember.getVoiceState();

                // Checks if the voice state is still in the channel.
                try {
                    if (!selfVoiceState.inVoiceChannel()) {
                        musicManager.scheduler.inVoiceChannel = false;
                    }
                } catch (NullPointerException e) {
                    disconnectFromChannel();
                    return;
                }

                musicManager.scheduler.inVoiceChannel = selfVoiceState.inVoiceChannel();

                try {
                    ArrayList<Member> membersInVoiceChannel = new ArrayList<>(selfVoiceState.getChannel().getMembers());

                    // Removes all the bots from the ArrayList
                    membersInVoiceChannel.removeIf(member -> member.getUser().isBot());

                    // Checking if the bot is alone in the VC.
                    if (membersInVoiceChannel.isEmpty() && seconds >= secondsToLeave) {
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle("Music Bot");
                        embedBuilder.setDescription("Aww... it's lonely in this voice channel. I guess I'll go...");
                        embedBuilder.setColor(CafeBot.getGeneralHelper().getRandomColor());
                        CafeBot.getGuildHandler().getCustomGuild(guild).sendMessageInLastMusicChannel(embedBuilder.build());
                        disconnectFromChannel();
                        return;
                    }

                    // This means that they are in a Voice Channel.
                    voicePassed = !(membersInVoiceChannel.isEmpty());
                } catch (NullPointerException ignored) {}

                // Checks if the bot is currently playing something and if the queue is empty.
                if (musicManager.scheduler.queue.isEmpty() && musicManager.audioPlayer.getPlayingTrack() == null && seconds >= secondsToLeave) {
                    guild.getAudioManager().closeAudioConnection();
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Music Bot");
                    embedBuilder.setColor(CafeBot.getGeneralHelper().getRandomColor());
                    embedBuilder.setDescription("There's no music playing... sooooo I'm gonna go because this is boring.");
                    CafeBot.getGuildHandler().getCustomGuild(guild).sendMessageInLastMusicChannel(embedBuilder.build());
                    disconnectFromChannel();
                    return;
                }

                // This means that there are songs in the queue and a song is currently playing.
                if (!musicManager.scheduler.queue.isEmpty() || musicManager.audioPlayer.getPlayingTrack() != null) {
                    queuePassed = true;
                }

                if (voicePassed && queuePassed) {
                    seconds = 0;
                }
            }
        };
        audioTimer.scheduleAtFixedRate(audioTimerTask, 1000, 1000);
    }

}
