package com.darellturtles;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.user.User;
import java.io.File;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;



// Definitions:
//  Pee (Piss): Unit of urination
//  Gradient: From a scale of 1 to 6, with 1 being the lightest (Basically clear)
//            what was the shade (gradient) of the pee
//

public class Pee {
    private final Message message;

    public Pee(Message message){
        this.message = message;
    }

    private MongoCollection<Document> getCollection(String collectionName){
        ConnectionString connString = new ConnectionString(System.getenv("MONGO_CONNECTION_STRING"));

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("Users"); // Assume all collections are in the same database

        return database.getCollection(collectionName);
    }


    private double getUserAverageGradient(String user){
        MongoCollection<Document> collection = getCollection("UserGradients");

        double n = 0;
        double gradientSum = 0;

        FindIterable<Document> iterator = collection.find();
        for (Document document : iterator) {
            if (document.get("userName").toString().equals(user)){
                gradientSum += Double.parseDouble(document.get("peeGradient").toString());
                n += 1;
            }
        }

        if (n == 0) // Make sure not to divide by 0
            n = -1;

        double avgGradient = gradientSum / n;
        return Math.round(avgGradient * 100.0) / 100.0; // Round to two decimal places
    }

    // Output:
    // UserName - Total pees in collection - Average gradient of pees
    //
    public void printUserTotals(){
        MongoCollection<Document> collection = getCollection("UserData");

        FindIterable<Document> iterator = collection.find();
        for (Document document : iterator) {
            message.getChannel().sendMessage(document.get("_id").toString() + " :sweat_drops:  " +
                    document.get("total").toString() + " total pees" + " with average gradient score of " + getUserAverageGradient(document.get("_id").toString()));
        }
    }

    public void printUserDailies(){
        MongoCollection<Document> collection = getCollection("DailyTotals");

        FindIterable<Document> iterator = collection.find();
        for (Document document : iterator) {
            message.getChannel().sendMessage(document.get("_id").toString() + " :toilet: " + document.get("dailyTotal").toString() + " total pees today");
        }
    }

    // Checks _id field
    private boolean userInCollection(MongoCollection<Document> collection, String user){
        FindIterable<Document> iterator = collection.find();

        for (Document document : iterator){
            if(document.get("_id").toString().equals(user))
                return true;
        }
        return false;
    }


    private int numReactionToInt(Reaction reaction){
        if (reaction.getEmoji().equalsEmoji("1️⃣"))
            return 1;

        if (reaction.getEmoji().equalsEmoji("2️⃣"))
            return 2;

        if (reaction.getEmoji().equalsEmoji("3️⃣"))
            return 3;

        if (reaction.getEmoji().equalsEmoji("4️⃣"))
            return 4;

        if (reaction.getEmoji().equalsEmoji("5️⃣"))
            return 5;

        if (reaction.getEmoji().equalsEmoji("6️⃣"))
            return 6;

        return -1;
    }


    public void addOnePee() throws InterruptedException {
        String user = message.getAuthor().getDiscriminatedName(); // User to add to database

        // Collections needed
        MongoCollection<Document> userDataCol = getCollection("UserData");
        MongoCollection<Document> userGradientCol = getCollection("UserGradients");
        MongoCollection<Document> dailyTotalsCol = getCollection("DailyTotals");

        // Send the pee gradient chart
        File chartImage = new File(System.getenv("PEE_CHART_PATH"));
        Message chartMessage = message.getChannel().sendMessage(
                "To record your pee, select which gradient most closely matched to your pee pee :toilet:\nComplete in 10 seconds", chartImage).join();
        chartMessage.addReactions("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣");


        // Wait for user to react, they are given 20 seconds
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                Document insertion;
                int gradientInt = -1;

                for (Reaction i : chartMessage.getReactions()) {
                    for (User j : i.getUsers().join()) {
                        if (j.getDiscriminatedName().equals(user)) {
                            gradientInt = numReactionToInt(i);
                        }
                    }
                }

                // Record reaction
                if (gradientInt == -1) { // Will catch if someone other than the intended user responds
                    message.getChannel().sendMessage("Pisser did not respond in time. Voiding the piss.. try again");
                    chartMessage.delete(); // Delete chart message for some tidiness
                    return;
                }

                // Add pee to UserData collection
                // Check to see if user is already in UserData collection
                if (!userInCollection(userDataCol, user)) {  // User is not already in database
                    insertion = new Document("_id", user).append("total", 1);
                    userDataCol.insertOne(insertion);
                }
                else { // User is already in UserData collection
                    userDataCol.updateOne(eq("_id", user), inc("total", 1));
                }

                // Add user's gradient into the UserGradients collection along with date recorded in American Central Time
                Instant now = Instant.now().minusSeconds(21600); // Instant returns a time date in GMT which is 6 hours ahead of American Central thus subtract 6 hours to get central
                insertion = new Document("userName", user).append("peeGradient", gradientInt).append("date", now);
                userGradientCol.insertOne(insertion);

                message.getChannel().sendMessage("Successfully recorded a gradient score of " + gradientInt + " for user " + user);
                chartMessage.delete(); // Delete chart message for some tidiness

                // Add pee to the DailyTotals collection
                // Daily pee count should be reset to 0 at midnight
                // Resetting of the collection is done via CRON Trigger on MongoDB
                // CRON: 0 5 * * *
                if (!userInCollection(dailyTotalsCol, user)) {  // User is not already in database
                    insertion = new Document("_id", user).append("dailyTotal", 1);
                    dailyTotalsCol.insertOne(insertion);
                }
                else { // User is already in DailyTotals collection
                    dailyTotalsCol.updateOne(eq("_id", user), inc("dailyTotal", 1));
                }
            } // End of run()
        }; // End of timerTask

        timer.schedule(timerTask, 10000); // 20000ms = 20seconds
    } // End of addOnePee()

} // End of pee class
