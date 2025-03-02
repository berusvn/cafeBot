package com.beanbeanjuice.utility.listener;

import com.beanbeanjuice.CafeBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A {@link ListenerAdapter} that listens to {@link GuildMessageReceivedEvent} and gives an
 * appropriate response depending on if it is enabled in the {@link Guild}.
 *
 * @author beanbeanjuice
 */
public class AIResponseListener extends ListenerAdapter {

    private final HashMap<ArrayList<String>, ArrayList<String>> messageMap;

    public AIResponseListener() {
        messageMap = new HashMap<>();

        createMaps();
    }

    private void createMaps() {
        createHelloMaps();
        createLOLMaps();
        createSproutMaps();
        createCafeBotMaps();
        createKenzieMaps();
        createMadisonMaps();
        createSorryMaps();
        createLillyMaps();
        createBanzaiMaps();
        createAttackMaps();
        createPPMaps();
        createNahMaps();
        createSTFUMaps();
    }

    private void createHelloMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("hello");
        commandTerms.add("hi");
        commandTerms.add("hey");

        responses.add("Hi, {user}!");
        responses.add("Hey hey, {user}! ^-^");
        responses.add("Hey, {user}! How are you?");
        responses.add("Hello, {user}.");
        responses.add("Hi! ^-^ The weather's nice today right? I can't tell... I'm just a robot...");
        responses.add("Hi hi {user}! How has your day been so far? Well, I hope it has been well...");
        responses.add("Hey hey {user}! Do you want to order some coffee from me?");

        messageMap.put(commandTerms, responses);
    }

    private void createLOLMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("lol");
        commandTerms.add("haha");
        commandTerms.add("lmao");
        commandTerms.add("xd");
        commandTerms.add("bruh");
        commandTerms.add("bro");
        commandTerms.add("lmfao");

        responses.add("Really? That's all you can do to respond?");
        responses.add("That response was dryer than the Sahara Desert.");
        responses.add("How long did it take you to think of that response?");
        responses.add("Maybe if you thought up a better response, you wouldn't be seeing this message.");
        responses.add("You're fun to talk to in a conversation huh?");
        responses.add("If that's all you can say, then you need some help...");
        responses.add("Is there nothing else you have to say?");
        responses.add("Can you be less of a dry texter?");
        responses.add("Could you speak any less? (If you can, please do. That response was dryer than this gin.)");

        messageMap.put(commandTerms, responses);
    }

    private void createSproutMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("sprout");
        commandTerms.add("kuromi");

        responses.add("That's a Sprout moment...");
        responses.add("If you're seeing this, then you said a word that reminds me of some clown named sprout who pulls sprout moments all the time.");
        responses.add("Imagine breaking a bee hive without silk touch in Minecraft...");
        responses.add("Stfu sprout...");
        responses.add("Soap?");
        responses.add("Who? Asked?");

        messageMap.put(commandTerms, responses);
    }

    private void createCafeBotMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("cafebot");
        commandTerms.add("cafe bot");
        commandTerms.add("what is cafebot");
        commandTerms.add("who is cafebot");
        commandTerms.add("what is cafe bot");
        commandTerms.add("who is cafe bot");

        responses.add("Hi, {user}. I'm cafeBot! A general-purpose bot who can also serve you some coffee!");
        responses.add("Hey, that's me! ^-^");
        responses.add("Someone called?");
        responses.add("Am I a real human yet?");

        messageMap.put(commandTerms, responses);
    }

    private void createKenzieMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("kenzie");
        commandTerms.add("kenzieisme");

        responses.add("Kenzie? The doctor?");
        responses.add("Did someone say Kenzie? The one who has an AMD gpu? Pepelaugh?");

        messageMap.put(commandTerms, responses);
    }

    private void createMadisonMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("madison");
        commandTerms.add("madie");

        responses.add("Madison? The one who's short?");
        responses.add("Madison moment...");
        responses.add("Imagine being 5 feet tall. Oh wait... Madison doesn't have to imagine.");

        messageMap.put(commandTerms, responses);
    }

    private void createSorryMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("im sorry");
        commandTerms.add("i'm sorry");
        commandTerms.add("i am sorry");
        commandTerms.add("sorry");

        responses.add("Don't worry, {user}! Everything will be forgiven and everything will be alright.");
        responses.add("Why are you sorry? Everything will be okay in the future. Don't worry.");
        responses.add("It's okay that you're sorry, but try not to be so hard on yourself! ^-^");

        messageMap.put(commandTerms, responses);
    }

    private void createLillyMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("lilly");
        commandTerms.add("otter pop");
        commandTerms.add("who's lilly");

        responses.add("You know who likes otter pops? Lilly. That's who.");
        responses.add("{user}, do you know Lilly? She's super cute.");
        responses.add("Lilly is SO CUTE.");
        responses.add("Who else thinks Lilly is adorable? Everyone, of course.");
        responses.add("Lilly pop!");
        responses.add("Lilly baby :pleading_face:!");
        responses.add("Lilly *LOVES* being wrapped in a blanket burrito.");

        messageMap.put(commandTerms, responses);
    }

    private void createAttackMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("stfu cafebot");
        commandTerms.add("cafebot sucks");
        commandTerms.add("cafebot is bad");
        commandTerms.add("shut up cafebot");
        commandTerms.add("cafebot is a bitch");

        responses.add("Who asked? Literally? Who?");
        responses.add("I still have more of a purpose than you do.");
        responses.add("Imagine talking back to someone made of code?");
        responses.add("I'm still more intelligent than you.");
        responses.add("Imagine thinking I care?");
        responses.add("Imagine having an argument with a bot? Pepelaugh? Pepelaugh.");
        responses.add("You need friends don't you? You're literally arguing with code.");

        messageMap.put(commandTerms, responses);
    }

    private void createBanzaiMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("banzai");

        responses.add("More like `bottom fragzai`. Lmao.");
        responses.add("Who? Asked?");

        messageMap.put(commandTerms, responses);
    }

    private void createPPMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("pp");

        responses.add("poopoo");

        messageMap.put(commandTerms, responses);
    }

    private void createNahMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("nah");

        responses.add("yah");

        messageMap.put(commandTerms, responses);
    }

    private void createSTFUMaps() {
        ArrayList<String> commandTerms = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();

        commandTerms.add("stfu");
        commandTerms.add("shut up");

        responses.add("You stfu, {user}...");
        responses.add("You shut up, {user}.");

        messageMap.put(commandTerms, responses);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();

        if (event.getAuthor().isBot()) {
            return;
        }

        if (!CafeBot.getGuildHandler().getCustomGuild(guild).getAIState()) {
            return;
        }

        String message = event.getMessage().getContentRaw().toLowerCase();

        messageMap.forEach((commandTerms, commandResponses) -> {
            if (commandTerms.contains(message.replace(".", "").replace("?", ""))) {
                event.getMessage().reply(parseMessage(
                        commandResponses.get(CafeBot.getGeneralHelper().getRandomNumber(0, commandResponses.size())),
                        event.getAuthor()
                )).queue();
            }
        });
    }

    private String parseMessage(@NotNull String message, @NotNull User user) {
        message = message.replace("{user}", user.getAsMention());
        return message;
    }

}
