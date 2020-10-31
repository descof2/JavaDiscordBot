package com.darellturtles;

import org.javacord.api.entity.message.Message;


public class Helper {
    private final Message message;

    public Helper(Message message) {
        this.message = message;
    }

    public void printCommands(){
        message.getChannel().sendMessage(
            "!help - List all usable commands \n" +
            "!help sounds - List all usable sounds \n" +
            "!jackboxtime - Move everyone to jackbox channel. Include names after the command to exclude moving those users [COMING SOON]\n" +
            "!generaltime - Move everyone from jackbox back to general\n" +
            "!poll [Question] - Creates a poll for users to vote on\n" +
            "!joey - Links a random JoeysWorldTourVideo:pizza:\n" +
            "!chugs - Links a random BadlandsChugs:beverage_box:\n");
    }

    public void printPees(){
        message.getChannel().sendMessage(
            "!pee pee - You went pee pee\n"+
            "!pee totals - Print all time pees\n" +
            "!pee dailies - Print total pees for the day \n");
    }
} // end of class
