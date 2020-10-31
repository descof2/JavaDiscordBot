package com.darellturtles;

import org.javacord.api.entity.message.Message;
import java.util.List;


public class Poll {
    private final Message message;
    private final List<String> userInput;

    public Poll(Message message, List<String> userInput){
        this.message = message;
        this.userInput = userInput;
    }

    public void createPoll(){
        String pollText = "";

        for (int i = 1; i < userInput.size(); i++){
            pollText = pollText.concat(userInput.get(i)).concat(" ");
        }

        if (pollText.isEmpty()){
            message.getChannel().sendMessage("!poll command missing question. Try again.");
            return;
        }

        message.delete(); // Delete the original poll command to cleanup chat
        Message pollMessage = message.getChannel().sendMessage(pollText + " - " + message.getAuthor().getDisplayName()).join();
        pollMessage.addReactions("\uD83D\uDC4D","\uD83D\uDC4E" ); // Add thumbs up and thumbs down reactions
    }
}// end of class
