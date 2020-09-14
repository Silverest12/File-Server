package server;

import java.util.Random;

public class StringUtils {

    public static String generateRandomName () {
        StringBuilder nameBuilder = new StringBuilder();

        Random randGen = new Random();

        int nameLength = randGen.nextInt(10) + 8;

        for(int i = 0; i < nameLength; i++) {
            int upOrLowOrInt = randGen.nextInt(3);

            if(upOrLowOrInt == 0) {
                nameBuilder.append((char) (randGen.nextInt(25) + 65));
            } else if(upOrLowOrInt == 1){
                nameBuilder.append((char) (randGen.nextInt(25) + 97));
            } else {
                nameBuilder.append((char) (randGen.nextInt(9) + 48));
            }
        }

        return nameBuilder.toString();
    }
}
