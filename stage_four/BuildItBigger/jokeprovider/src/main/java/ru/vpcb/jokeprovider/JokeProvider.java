package ru.vpcb.jokeprovider;

import java.util.Random;

import static ru.vpcb.constants.Constants.JOKE_MESSAGES;

/**
 *  Custom Joke Provider class
 *  All strings placed int Constants class because it's impossible
 *  to get  them from Android Resource
 *
 *
 */
public class JokeProvider {
    private Random mRnd;
    private int mPosition;



    public JokeProvider() {
        mRnd = new Random();
        mPosition = 0;
    }


    public String getJoke() {
        return JOKE_MESSAGES[mRnd.nextInt(JOKE_MESSAGES.length)];
    }

    public String getNext() {
        mPosition++;
        if (mPosition >= JOKE_MESSAGES.length) mPosition = 0;
        return JOKE_MESSAGES[mPosition];
    }

    public String getPrev() {
        mPosition--;
        if (mPosition < 0) mPosition = JOKE_MESSAGES.length - 1;
        return JOKE_MESSAGES[mPosition];
    }
}
