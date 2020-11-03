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
            "!help pee - list all pee related commands \n" +
            "!poll [Question] - Creates a poll for users to vote on\n" +
            "!joey - [COMING SOON AGAIN] Links a random JoeysWorldTourVideo:pizza:\n" +
            "!chugs - [COMING SOON AGAIN] Links a random BadlandsChugs:beverage_box:\n");
    }

    public void printPees(){
        message.getChannel().sendMessage(
            "!pee pee - You went pee pee\n"+
            "!pee totals - Print all time pees\n" +
            "!pee daily - Print total pees for the day \n");
    }
} // end of class
