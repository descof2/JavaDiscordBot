package com.darellturtles;

import com.google.gson.Gson;
import org.javacord.api.entity.message.Message;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Joke {
    private final Message message;

    public Joke(Message message) {
        this.message = message;
    }

    public static class JokeJson { // Storing JSON into java object
        private ArrayList<JsonBody> body = null;

        public JokeJson() {
        }

        public ArrayList<JsonBody> getBody() {
            return body;
        }


        public static class JsonBody { // Nested JSON body
            private String setup;
            private String punchline;

            public JsonBody() {
            }

            public String getSetup() {
                return setup;
            }

            public String getPunchline() {
                return punchline;
            }

        } // end of MyBody
    } // end of JokeJson

    public void printJoke() throws IOException, InterruptedException {

        // Grab JSON from Dad Jokes API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dad-jokes.p.rapidapi.com/random/joke"))
                .header("x-rapidapi-host", "dad-jokes.p.rapidapi.com")
                .header("x-rapidapi-key", System.getenv("DADJOKE_KEY"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String jsonString = response.body();

        // Convert JSON into java object
        Gson gson = new Gson();
        JokeJson jokeJson = gson.fromJson(jsonString, Joke.JokeJson.class);

        // Send the joke with the punchline marked as a spoiler
        message.getChannel().sendMessage(jokeJson.getBody().get(0).getSetup() + "\n" + "||" + jokeJson.getBody().get(0).getPunchline() + "||");
    }

} // end of Joke class
