package com.darellturtles;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.user.User;

import java.util.List;

public class Mover {

    private List<User> users;
    private ServerVoiceChannel destination;


    public Mover(List<User> users, ServerVoiceChannel destination) {
        this.users = users;
        this.destination = destination;
    }


    public void moveUsers(){
        for (User i : users){
            i.move(destination);
        }
    }


} // End of mover class

