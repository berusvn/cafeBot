package com.beanbeanjuice.command.interaction;

import com.beanbeanjuice.utility.command.CommandContext;
import com.beanbeanjuice.utility.command.ICommand;
import com.beanbeanjuice.utility.command.usage.Usage;
import com.beanbeanjuice.utility.command.usage.categories.CategoryType;
import com.beanbeanjuice.utility.command.usage.types.CommandType;
import com.beanbeanjuice.utility.sections.interaction.Interaction;
import io.github.beanbeanjuice.cafeapi.cafebot.interactions.InteractionType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;

/**
 * An {@link ICommand} used to hug people.
 *
 * @author beanbeanjuice
 */
public class HugCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx, ArrayList<String> args, User user, GuildMessageReceivedEvent event) {
        new Interaction(InteractionType.HUG,
                "**{sender}** *hugged* themselves! Umm... how?",
                "**{sender}** *hugged* **{receiver}**!",
                "{sender} hugged others {amount_sent} times. {receiver} was hugged {amount_received} times.",
                user,
                args,
                event.getChannel());
    }

    @Override
    public String getName() {
        return "hug";
    }

    @Override
    public ArrayList<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public String getDescription() {
        return "Hug someone!";
    }

    @Override
    public String exampleUsage(String prefix) {
        return "`" + prefix + "hug @beanbeanjuice` or `" + prefix + "hug @beanbeanjuice You're so cool`";
    }

    @Override
    public Usage getUsage() {
        Usage usage = new Usage();
        usage.addUsage(CommandType.SENTENCE, "Users + Extra Message", false);
        return usage;
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.INTERACTION;
    }

}
