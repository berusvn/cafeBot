package com.beanbeanjuice.utility.sections.moderation.voicechat;

import com.beanbeanjuice.CafeBot;
import com.beanbeanjuice.utility.logger.LogLevel;
import io.github.beanbeanjuice.cafeapi.cafebot.voicebinds.VoiceChannelBind;
import io.github.beanbeanjuice.cafeapi.exception.CafeException;
import io.github.beanbeanjuice.cafeapi.exception.ConflictException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A handler for {@link net.dv8tion.jda.api.entities.VoiceChannel} and their corresponding {@link net.dv8tion.jda.api.entities.Role Roles}.
 *
 * @author beanbeanjuice
 */
public class VoiceChatRoleBindHandler {

    /**
     * A map of Guild IDs containing a map of Voice Chat IDs and Voice Channel Binds.
     */
    private HashMap<String, ArrayList<VoiceChannelBind>> guildVoiceBinds;

    /**
     * Creates a new {@link VoiceChatRoleBindHandler} object.
     */
    public VoiceChatRoleBindHandler() {
        guildVoiceBinds = new HashMap<>();
        updateVoiceBindCache();
    }

    /**
     * Retrieves all bound {@link String roleIDs} for a specified {@link String voiceChannelID}.
     * @param guildID The {@link String guildID} that contains the {@link ArrayList} of {@link VoiceChannelBind}.
     * @param voiceChannelID The specified {@link String voiceChannelID} to look for.
     * @return The {@link ArrayList} of {@link String roleID} for the {@link String voiceChannelID}.
     */
    @NotNull
    public ArrayList<String> getBoundRolesForChannel(@NotNull String guildID, @NotNull String voiceChannelID) {
        ArrayList<String> roleIDs = new ArrayList<>();
        try {

            // Goes through each bind and makes sure it gets the one that has the channel specified.
            for (VoiceChannelBind bind : guildVoiceBinds.get(guildID)) {
                if (bind.getVoiceChannelID().equals(voiceChannelID)) {
                    roleIDs.add(bind.getRoleID());
                }
            }
            return roleIDs;
        } catch (NullPointerException e) {
            // If this runs, the guildID does not exist.
            return roleIDs;
        }
    }

    /**
     * Retrieves all {@link VoiceChannelBind} in the {@link io.github.beanbeanjuice.cafeapi.CafeAPI CafeAPI} for a specified {@link String guildID}.
     * @param guildID The {@link String guildID} to look for.
     * @return The {@link HashMap} with keys of {@link String voiceChannelID} and a value of {@link ArrayList} of {@link String roleID}.
     */
    @NotNull
    public HashMap<String, ArrayList<String>> getAllBoundChannelsForGuild(@NotNull String guildID) {
        HashMap<String, ArrayList<String>> channels = new HashMap<>();

        // Goes through each bind.
        try {
            guildVoiceBinds.get(guildID).forEach((bind) -> {

                String voiceChannelID = bind.getVoiceChannelID();
                String roleID = bind.getRoleID();

                // If the key doesn't exist. Make a new one.
                if (!channels.containsKey(voiceChannelID)) {
                    channels.put(voiceChannelID, new ArrayList<>());
                }

                channels.get(voiceChannelID).add(roleID);
            });

            return channels;
        } catch (NullPointerException e) {
            return channels;
        }
    }

    /**
     * Updates the cache for the {@link VoiceChannelBind}.
     */
    private void updateVoiceBindCache() {
        try {
            guildVoiceBinds = CafeBot.getCafeAPI().voiceChannelBinds().getAllVoiceChannelBinds();
        } catch (CafeException e) {
            CafeBot.getLogManager().log(this.getClass(), LogLevel.ERROR, "Error Caching Voice Binds: " + e.getMessage(), e);
        }
    }

    /**
     * Binds a {@link String roleID} to a specified {@link String voiceChannelID}.
     * @param guildID The {@link String guildID} that contains the {@link String voiceChannelID}.
     * @param voiceChannelID The {@link String voiceChannelID} to bind to.
     * @param roleID The {@link String roleID} to bind to the {@link String voiceChannelID}.
     * @return True, if the {@link String roleID} was successfully bound to the {@link String voiceChannelID}.
     */
    @NotNull
    public Boolean bindRoleToVoiceChannel(@NotNull String guildID, @NotNull String voiceChannelID, @NotNull String roleID) {
        try {
            VoiceChannelBind voiceChannelBind = new VoiceChannelBind(voiceChannelID, roleID);

            CafeBot.getCafeAPI().voiceChannelBinds().addVoiceChannelBind(guildID, voiceChannelBind);

            if (!guildVoiceBinds.containsKey(guildID)) {
                guildVoiceBinds.put(guildID, new ArrayList<>());
            }

            guildVoiceBinds.get(guildID).add(voiceChannelBind);
            return true;
        } catch (ConflictException e) {
            return false;
        } catch (CafeException e) {
            CafeBot.getLogManager().log(this.getClass(), LogLevel.ERROR, "Error Updating Bind: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Unbinds a {@link String roleID} from a specified {@link String voiceChannelID}.
     * @param guildID The {@link String guildID} containing the {@link String voiceChannelID}.
     * @param voiceChannelID The {@link String voiceChannelID} to remove the {@link String roleID} from.
     * @param roleID The {@link String roleID} to remove.
     * @return True, if the {@link String roleID} was successfully unbound from the {@link String voiceChannelID}.
     */
    @NotNull
    public Boolean unBindRoleFromVoiceChannel(@NotNull String guildID, @NotNull String voiceChannelID, @NotNull String roleID) {

        try {
            CafeBot.getCafeAPI().voiceChannelBinds().deleteVoiceChannelBind(guildID, new VoiceChannelBind(voiceChannelID, roleID));

            if (guildVoiceBinds.containsKey(guildID)) {
                ArrayList<VoiceChannelBind> tempBindList = new ArrayList<>(guildVoiceBinds.get(guildID));
                tempBindList.forEach((bind) -> {
                    guildVoiceBinds.get(guildID).remove(bind);
                });
            }

            return true;
        } catch (CafeException e) {
            CafeBot.getLogManager().log(this.getClass(), LogLevel.ERROR, "Error Removing Bind: " + e.getMessage(), e);
            return false;
        }

    }

}
