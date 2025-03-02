package com.beanbeanjuice.utility.helper.api.dictionary;

import com.beanbeanjuice.CafeBot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletionException;

/**
 * An class used to connect to the Dictionary.com API.
 *
 * @author beanbeanjuice
 */
public class DictionaryAPI {

    private String DICTIONARY_API_URL = "https://api.dictionaryapi.dev/api/v2/entries/{language_code}/{word}";
    private final String word;
    private String audioLink;
    private ArrayList<DictionaryMeaning> dictionaryMeanings;

    /**
     * Creates a new {@link DictionaryAPI}.
     * @param word The word to search for.
     * @param languageCode The specified language code.
     */
    public DictionaryAPI(@NotNull String word, @NotNull String languageCode) {
        dictionaryMeanings = new ArrayList<>();
        this.word = word;
        DICTIONARY_API_URL = DICTIONARY_API_URL.replace("{language_code}", languageCode);
        DICTIONARY_API_URL = DICTIONARY_API_URL.replace("{word}", word);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().setHeader("User-Agent", CafeBot.getBotUserAgent()).uri(URI.create(DICTIONARY_API_URL)).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parse)
                .join();
    }

    /**
     * @return The {@link MessageEmbed} for the specified word.
     */
    @NotNull
    public MessageEmbed dictionaryEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(word);
        StringBuilder descriptionBuilder = new StringBuilder();

        if (audioLink != null) {
            descriptionBuilder.append("[Audio](").append(audioLink.replace("//", "https://")).append(")\n");
        }

        for (int i = 0; i < dictionaryMeanings.size(); i++) {
            descriptionBuilder.append("**Definition #").append(i+1).append("** (*").append(dictionaryMeanings.get(i).getPartOfSpeech()).append("*): ").append(dictionaryMeanings.get(i).getDefinition()).append("\n");
            descriptionBuilder.append("**Example**: ").append(dictionaryMeanings.get(i).getExample()).append("\n\n");
        }
        embedBuilder.setDescription(descriptionBuilder.toString());

        embedBuilder.setColor(CafeBot.getGeneralHelper().getRandomColor());
        return embedBuilder.build();
    }

    private String parse(String responseBody) throws CompletionException {
        ObjectMapper defaultObjectMapper = new ObjectMapper();
        try {
            JsonNode node = defaultObjectMapper.readTree(responseBody);
            try {
                audioLink = node.get(0).get("phonetics").get(0).get("audio").textValue();
            } catch (NullPointerException e) {
                audioLink = null;
            }

            for (JsonNode jsonNode : node.get(0).get("meanings")) {
                String partOfSpeech;
                try {
                    partOfSpeech = jsonNode.get("partOfSpeech").textValue();
                } catch (NullPointerException e) {
                    partOfSpeech = null;
                }
                String definition;
                try {
                    definition = jsonNode.get("definitions").get(0).get("definition").textValue();
                } catch (NullPointerException e) {
                    definition = null;
                }
                String example;
                try {
                    example = jsonNode.get("definitions").get(0).get("example").textValue();
                } catch (NullPointerException e) {
                    example = null;
                }
                dictionaryMeanings.add(new DictionaryMeaning(partOfSpeech, definition, example));
            }
            return node.get(0).toString();
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
