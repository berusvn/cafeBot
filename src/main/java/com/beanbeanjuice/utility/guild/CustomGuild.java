package com.beanbeanjuice.utility.guild;

import com.beanbeanjuice.main.BeanBot;
import com.beanbeanjuice.utility.lavaplayer.GuildMusicManager;
import com.beanbeanjuice.utility.lavaplayer.PlayerManager;
import com.beanbeanjuice.utility.twitch.Twitch;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A {@link CustomGuild} that contains {@link net.dv8tion.jda.api.entities.Guild Guild} information.
 *
 * @author beanbeanjuice
 */
public class CustomGuild {

    private String guildID;
    private String prefix;
    private String moderatorRoleID;
    private String liveChannelID;
    private ArrayList<String> twitchChannels;
    private String mutedRoleID;
    private String liveNotificationsRoleID;
    private Boolean notifyOnUpdate;
    private String updateChannelID;

    private Timer timer;
    private TimerTask timerTask;
    private TextChannel lastMusicChannel;

    private ArrayList<TextChannel> deletingMessagesChannels;

    /**
     * Creates a new {@link CustomGuild} object.
     * @param guildID The ID of the {@link Guild}.
     * @param prefix The prefix the bot will use for the {@link Guild}.
     * @param moderatorRoleID The ID of the moderator {@link Role} for the {@link Guild}.
     * @param liveChannelID The ID of the {@link TextChannel} to send twitch notifications to in the {@link Guild}.
     * @param twitchChannels The {@link ArrayList<String>} of twitch channels for the {@link Guild}.
     * @param mutedRoleID The ID of the muted {@link Role} for the {@link Guild}.
     * @param liveNotificationsRoleID The ID of the live notifications {@link Role} for the {@link Guild}.
     * @param notifyOnUpdate The {@link Boolean} of whether or not to notify the {@link Guild} on an update to the Bot.
     * @param updateChannelID The ID of the {@link TextChannel} to send the bot update notifications to.
     */
    public CustomGuild(@NotNull String guildID, @NotNull String prefix, @NotNull String moderatorRoleID,
                       @NotNull String liveChannelID, @NotNull ArrayList<String> twitchChannels, @NotNull String mutedRoleID,
                       @NotNull String liveNotificationsRoleID, @NotNull Boolean notifyOnUpdate, @NotNull String updateChannelID) {
        this.guildID = guildID;
        this.prefix = prefix;
        this.moderatorRoleID = moderatorRoleID;
        this.liveChannelID = liveChannelID;
        this.twitchChannels = twitchChannels;
        this.mutedRoleID = mutedRoleID;
        this.liveNotificationsRoleID = liveNotificationsRoleID;
        this.notifyOnUpdate = notifyOnUpdate;
        this.updateChannelID = updateChannelID;

        // Checks if a Listener has already been created for that guild.
        // This is so that if the cache is reloaded, it does not need to recreate the Listeners.
        if (BeanBot.getTwitchHandler().getTwitch(guildID) == null) {
            BeanBot.getTwitchHandler().addTwitchToGuild(guildID, new Twitch(this.guildID, this.twitchChannels));
        }

        deletingMessagesChannels = new ArrayList<>();

    }

    /**
     * Sets the {@link Boolean} for if the {@link Guild} should be notified on an update.
     * @param answer The {@link Boolean} answer.
     * @return Whether or not updating it was successful.
     */
    public Boolean setNotifyOnUpdate(@NotNull Boolean answer) {
        if (BeanBot.getGuildHandler().setNotifyOnUpdate(guildID, answer)) {
            notifyOnUpdate = answer;
            return true;
        }
        return false;
    }

    /**
     * @return The current state of whether or not the {@link Guild} should be notified on an update.
     */
    public Boolean getNotifyOnUpdate() {
        return notifyOnUpdate;
    }

    /**
     * Sets the Live Notifications {@link Role} ID for the {@link Guild}.
     * @param role The {@link Role} for the Live Notifications {@link Role}.
     * @return Whether or not it was successful.
     */
    public Boolean setLiveNotificationsRole(@NotNull Role role) {
        return setLiveNotificationsRoleID(role.getId());
    }

    /**
     * Sets the Live Notifications {@link Role} ID for the {@link Guild}.
     * @param roleID The ID for the Live Notifications {@link Role}.
     * @return Whether or not it was successful.
     */
    public Boolean setLiveNotificationsRoleID(@NotNull String roleID) {

        // Only set it if it updates in the database.
        if (BeanBot.getGuildHandler().setLiveNotificationsRoleID(guildID, roleID)) {
            liveNotificationsRoleID = roleID;
            return true;
        }
        return false;
    }

    /**
     * @return The live notifications {@link Role}.
     */
    @Nullable
    public Role getLiveNotificationsRole() {
        return BeanBot.getGuildHandler().getGuild(guildID).getRoleById(liveNotificationsRoleID);
    }

    /**
     * Remove a {@link TextChannel} from the list of currently deleting {@link Message}s.
     * @param channel The {@link TextChannel} to remove.
     */
    public void removeTextChannelFromDeletingMessages(@NotNull TextChannel channel) {
        deletingMessagesChannels.remove(channel);
    }

    /**
     * Add a {@link TextChannel} to the list of currently deleting {@link Message}s.
     * @param channel The {@link TextChannel} to add.
     */
    public void addTextChannelToDeletingMessages(@NotNull TextChannel channel) {
        deletingMessagesChannels.add(channel);
    }

    /**
     * Checks to make sure that a {@link TextChannel} does not already have {@link Message}s being
     * currently deleted.
     * @param channel The {@link TextChannel} to check.
     * @return Whether or not it already has {@link Message}s being deleted in it.
     */
    @NotNull
    public Boolean containsTextChannelDeletingMessages(@NotNull TextChannel channel) {
        return deletingMessagesChannels.contains(channel);
    }

    /**
     * @return The {@link String} ID of the live channel to send messages.
     */
    @NotNull
    public String getLiveChannelID() {
        return liveChannelID;
    }

    /**
     * Sets the last channel the music commands were sent in.
     * @param lastMusicChannel The {@link TextChannel} of the last music command.
     */
    public void setLastMusicChannel(TextChannel lastMusicChannel) {
        this.lastMusicChannel = lastMusicChannel;
    }

    /**
     * Starts checking for an {@link net.dv8tion.jda.internal.audio.AudioConnection AudioConnection} in the current {@link Guild}.
     */
    public void startAudioChecking() {
        timer = new Timer();
        final int[] seconds = {0};
        int secondsToLeave = 300;
        timerTask = new TimerTask() {

            @Override
            public void run() {
                boolean voicePassed = false;
                boolean queuePassed = false;
                seconds[0] += 1;
                Guild guild = BeanBot.getGuildHandler().getGuild(guildID);
                Member selfMember = guild.getSelfMember();
                GuildVoiceState selfVoiceState = selfMember.getVoiceState();

                ArrayList<Member> membersInVoiceChannel = new ArrayList<>(selfVoiceState.getChannel().getMembers());
                membersInVoiceChannel.remove(selfMember);
                GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(BeanBot.getGuildHandler().getGuild(guildID));

                // Checking if the bot is alone in the VC.
                if (membersInVoiceChannel.isEmpty() && seconds[0] >= secondsToLeave) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setAuthor("Music Bot");
                    embedBuilder.setDescription("Leaving the voice channel as it is empty...");
                    embedBuilder.setColor(BeanBot.getGeneralHelper().getRandomColor());
                    sendMessageInLastMusicChannel(embedBuilder.build());
                    musicManager.scheduler.player.stopTrack();
                    musicManager.scheduler.queue.clear();
                    guild.getAudioManager().closeAudioConnection();
                    timer.cancel();
                    return;
                }

                // This means that they are in a Voice Channel.
                voicePassed = !membersInVoiceChannel.isEmpty();

                // Checks if the bot is currently playing something and if the queue is empty.
                if (musicManager.scheduler.queue.isEmpty() && musicManager.audioPlayer.getPlayingTrack() == null && seconds[0] >= secondsToLeave) {
                    guild.getAudioManager().closeAudioConnection();
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setAuthor("Music Bot");
                    embedBuilder.setColor(BeanBot.getGeneralHelper().getRandomColor());
                    embedBuilder.setDescription("Leaving the voice channel as the music queue is empty...");
                    sendMessageInLastMusicChannel(embedBuilder.build());
                    guild.getAudioManager().closeAudioConnection();
                    timer.cancel();
                    return;
                }

                // This means that there are songs in the queue and a song is currently playing.
                if (!musicManager.scheduler.queue.isEmpty() || musicManager.audioPlayer.getPlayingTrack() != null) {
                    queuePassed = true;
                }

                if (voicePassed && queuePassed) {
                    seconds[0] = 0;
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    /**
     * Sends a message in the specified last music {@link TextChannel}.
     * @param embed The {@link MessageEmbed} to be sent.
     */
    private void sendMessageInLastMusicChannel(MessageEmbed embed) {
        try {
            lastMusicChannel.sendMessage(embed).queue();
        } catch (NullPointerException ignored) {}
    }

    /**
     * Stops checking for an {@link net.dv8tion.jda.internal.audio.AudioConnection AudioConnection} in the current {@link Guild}.
     */
    public void stopAudioChecking() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * @return The {@link Twitch} associated with the {@link Guild}.
     */
    @NotNull
    public Twitch getTwitch() {
        return BeanBot.getTwitchHandler().getTwitch(guildID);
    }

    /**
     * Update the muted {@link Role} for the {@link Guild}.
     * @param mutedRoleID The ID of the muted {@link Role}.
     * @return Whether or not the {@link Role} was successfully updated in the database.
     */
    @NotNull
    public Boolean updateMutedRole(String mutedRoleID) {
        return BeanBot.getGuildHandler().updateGuildMutedRole(guildID, mutedRoleID);
    }

    /**
     * @return The ID for the {@link CustomGuild}.
     */
    @NotNull
    public String getGuildID() {
        return guildID;
    }

    /**
     * @return The prefix for the {@link CustomGuild}.
     */
    @NotNull
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix for the {@link CustomGuild}.
     * @param newPrefix The new prefix.
     * @return Whether or not setting the prefix was successful.
     */
    @NotNull
    public Boolean setPrefix(String newPrefix) {
        return BeanBot.getGuildHandler().updateGuildPrefix(guildID, newPrefix);
    }

    /**
     * @return The muted {@link Role} for the current {@link Guild}.
     */
    @Nullable
    public Role getMutedRole() {
        try {
            return BeanBot.getGuildHandler().getGuild(guildID).getRoleById(mutedRoleID);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * @return The moderator {@link Role} for the current {@link Guild}.
     */
    @Nullable
    public Role getModeratorRole() {
        try {
            return BeanBot.getGuildHandler().getGuild(guildID).getRoleById(moderatorRoleID);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Checks if the {@link CustomGuild} contains the specified twitch channel.
     * @param twitchChannel The twitch channel specified/
     * @return Whether or not the {@link CustomGuild} contains the twitch channel.
     */
    @NotNull
    public Boolean containsChannel(String twitchChannel) {
        for (String channel : twitchChannels) {
            if (channel.equalsIgnoreCase(twitchChannel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a twitch channel to the {@link Guild}.
     * @param twitchChannel The name of the twitch channel to add.
     * @return Whether or not the twitch channel was successfully added.
     */
    @NotNull
    public Boolean addTwitchChannel(String twitchChannel) {

        twitchChannel = twitchChannel.toLowerCase();

        if (twitchChannels.contains(twitchChannel)) {
            return false;
        }

        twitchChannels.add(twitchChannel.toLowerCase());
        BeanBot.getTwitchHandler().getTwitch(guildID).getTwitchChannelNamesHandler().addTwitchChannelName(twitchChannel);
        return BeanBot.getGuildHandler().addTwitchChannel(guildID, twitchChannel);
    }

    /**
     * Removes a twitch channel from the {@link Guild}.
     * @param twitchChannel The name of the twitch channel to be removed.
     * @return Whether or not the twitch channel was successfully removed.
     */
    @NotNull
    public Boolean removeTwitchChannel(String twitchChannel) {

        twitchChannel = twitchChannel.toLowerCase();

        if (!twitchChannels.contains(twitchChannel)) {
            return false;
        }

        twitchChannels.remove(twitchChannel.toLowerCase());
        BeanBot.getTwitchHandler().getTwitch(guildID).getTwitchChannelNamesHandler().removeTwitchChannelName(twitchChannel);
        return BeanBot.getGuildHandler().removeTwitchChannel(guildID, twitchChannel);
    }

    /**
     * Update the twitch channel for the {@link CustomGuild}.
     * @param newLiveChannelID The new channel ID for the channel.
     * @return Whether or not the channel was successfully updated.
     */
    @NotNull
    public Boolean updateTwitchDiscordChannel(String newLiveChannelID) {
        return BeanBot.getGuildHandler().updateTwitchDiscordChannel(guildID, newLiveChannelID);
    }

}
