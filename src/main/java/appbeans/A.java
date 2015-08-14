package appbeans;

import java.util.Random;

/**
 * Created by mkhanwalkar on 8/12/15.
 */
public class A implements InputProcessor {


    String[] words = new String[]{"nathan", "mike", "jackson", "golda", "bertels"};
    Random rand = new Random();

    @Override
    public Object[] getValues() {
        String word = words[rand.nextInt(words.length)];
        return new Object[]{word};
    }

   }
