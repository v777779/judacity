package ru.vpcb.jokelibrary;

import java.util.Random;

import javax.naming.Context;

public class JokeLibrary {
    private Random mRnd;
    private int mPosition;
    private final String[] strings = new String[]{
            "etc. - end of thinking capacity",
            "WHY whenever I sit down to work, someone wakes me up?",
            "My middle finger salutes you!",
            "A man is the only living being, who is able have sex by phone.",
            "A bag of money is a symbol of richness. Or enormous inflation.",
            "You cannot buy friends. That's absolutely true. But you can sell them quite profitably...",
            "Nowadays you need a fixed telephone line only to find your smartphone.",
            "You cannot treat yourself as an adult if your photos from school are in digital format.",
            "Alcohol kills brain cells. But not all of them - only those, which refuse to drink.",
            "Most of the products in shopping malls can be categorized into one of the two: trash bags or trash for bags.",
            "I don't mind going to work but this 8 hour wait to go home is bullshit.",
            "I don't sing in the shower. I perform.",
            "While driving, I feel like a goddess, I drive, others pray.",
            "You need to call woman twice. First time that she could find the phone in her bag, second - to answer.",
            "Dear women, please stop losing weight. We'll lie on the boards in the coffins.",
            "Forgive your enemy, but remember his name.",
            "Help a woman when she is in trouble and she will remember you when she will be in trouble again.",
            "Many people are alive only because its illegal to shoot them.",
            "Alcohol doesn't solve any problem, but neither does milk.",
            "Smoking kills, but if you don't smoke, that still doesn't mean you'll never die.",
            "It's easy to make woman happy. But expensive.",
            "Beer is now cheaper than fuel. Drink, don't drive.",
            "Smoking a cigarette reduces life by 5 minutes.Laughing increases life by 10 minutes. Conclusion: a laughing smoker never dies.",
            "My wallet is like an onion. When I open it, it makes me cry...",
            "When a girl cries for you - don't be too emotional. Those tears are like a loan - you will have to pay it back with great interest.",
            "Alcohol! Because no great story started with someone eating a salad.",
            "The average woman would better choose to be beautifull than to have brains, cause the average man can see better than he thinks.",
            "Today I feel like a tampon, in the right place at the wrong time.",
            "Women spend their whole life to find the right man just to tell him everyday that he is wrong.",
            "I know my drinking limits. The problem is that I can never reach them - I simply fall down.",
            "Always be yourself. Unless you can be a unicorn. Then always be a unicorn.",
            "Age is just a number, jail is just a room.",
            "If I'm not back in five minutes, just wait longer...",
            "Fat people live shorter, but eat longer.",
            "I don't get exited by money, they soothe me.",
            "Take my advice. I don't use it anyway.",
            "Talent is like an orgasm - it's difficult both to hide and to pretend it.",
            "Don't worry if the spring came late. Scientists tell that this may happen before every ice-age.",
            "Some people's x-rays are better than their photos."
    };


    public JokeLibrary() {
        mRnd = new Random();
        mPosition = 0;
    }


    public String getJoke() {
        return strings[mRnd.nextInt(strings.length)];
    }

    public String getNext() {
        mPosition++;
        if (mPosition >= strings.length) mPosition = 0;
        return strings[mPosition];
    }

    public String getPrev() {
        mPosition--;
        if (mPosition < 0) mPosition = strings.length - 1;
        return strings[mPosition];
    }
}
