package jeff.mosquito.game;

import java.util.ArrayList;
import java.util.List;

public class HighScoreSerializer {
    // Methode zur Serialisierung einer Liste von Datenpaaren in einen einzelnen String
    public static String serializeHighScore(List<HighScore> highscores) {
        StringBuilder sb = new StringBuilder();

        for (HighScore highscore : highscores) {
            String serializedPair = highscore.getName() + "," + highscore.getPoints() + ";";
            sb.append(serializedPair);
        }

        return sb.toString();
    }

    // Methode zur Deserialisierung eines Strings in eine Liste von Datenpaaren
    public static List<HighScore> deserializeHighScore(String serializedData) {
        List<HighScore> highscores = new ArrayList<>();

        String[] pairStrings = serializedData.split(";");

        for (String pairString : pairStrings) {
            String[] pairData = pairString.split(",");
            String name = pairData[0];
            int points = Integer.parseInt(pairData[1]);
            highscores.add(new HighScore(name, points));
        }

        return highscores;
    }
}