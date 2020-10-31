package com.darellturtles;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import java.util.Arrays;
import java.util.List;

public class Bot {

    public static void main(String[] args) {
        DiscordApi discord = new DiscordApiBuilder()
            .setToken(System.getenv("DISCORD_API_KEY"))
            .login().join();

        discord.addMessageCreateListener(event -> {
            if (event.getMessage().getAuthor().isBotUser()) // Ensure bot doesn't respond to its own messages
                return;

            // Converting message string into a list with each word in its own index
            String messageString = event.getMessageContent();
            String[] tempString = messageString.split(" ");
            List<String> userInput;
            userInput = Arrays.asList(tempString);

            // Help Commands
            // Prints all usable commands
            if (userInput.get(0).equalsIgnoreCase("!help")) {
                Helper helper = new Helper(event.getMessage());
                if (userInput.size() == 2){
                    if (userInput.get(0).equalsIgnoreCase("!help") && userInput.get(1).equalsIgnoreCase("pee")){
                        helper.printPees();
                    }
                }
                else
                    helper.printCommands();
            }

            // Youtube Video Commands
            // Prints a random youtube video from these users
            if (userInput.get(0).equalsIgnoreCase("!joey")){ // JoeysWorldTour
                Video video = new Video();
                video.printRandomVideo();
            }

            if (userInput.get(0).equalsIgnoreCase("!chugs")){ // BadlandsChugs
                Video video = new Video();
                video.printRandomVideo();
            }

            // Poll Command
            if (userInput.get(0).equalsIgnoreCase("!poll")){
                Poll poll = new Poll(event.getMessage(), userInput);
                poll.createPoll();
            }

            // Mover Commands
            if (userInput.get(0).equalsIgnoreCase("!jackboxtime")){
                //Mover mover = new Mover();

            }

            if (userInput.get(0).equalsIgnoreCase("!generaltime")){
                //Mover mover = new Mover();
            }

            // Pee Commands
            // Record a user's pee along with the gradient of that pee
            if (userInput.get(0).equalsIgnoreCase("!pee") && userInput.get(1).equalsIgnoreCase("pee")){
                Pee pee = new Pee(event.getMessage());
                try {
                    pee.addOnePee();
                } catch (InterruptedException e) {
                    event.getMessage().getChannel().sendMessage("Something went wrong. Try again.");
                }
            }

            if (userInput.get(0).equalsIgnoreCase("!pee") && userInput.get(1).equalsIgnoreCase("totals")){ // pee totals
                Pee pee = new Pee(event.getMessage());
                pee.printUserTotals();
            }

            if (userInput.get(0).equalsIgnoreCase("!pee") && userInput.get(1).equalsIgnoreCase("daily")){
                Pee pee = new Pee(event.getMessage());
                pee.printUserDailies();
            }

        }); // end of listener

    } // end of main
} // end of class
