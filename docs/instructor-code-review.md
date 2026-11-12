# Instructor Code Review Feedback

**Contact**: Dr. Yiji Zhang (yiji.zhang@northwestern.edu)

**Purpose of This Document**:
The instructor will perform code review with respect to software design, error handling, format and style on the main branch every week starting Week 6 using the letter grade A standards.
The following chapters of the textbook are considered: Chapter 1, 2, 3, 4, 5, 6, 7, 9, and 10. The corresponding lectures are considered, too.

## Week 7 Code Review
I have read every line of production code currently in the main branch. One comment:

1. StartingHandDealer#deal method should be split into a couple of smaller private methods. Similarly, in the Deck#removeCards method, the two if blocks for error handling should go into one private method too.

Otherwise, the code looks good to me!


## Week 6 Code Review
I have read every line of production code currently in the main branch. Very good progress overall! A couple of comments:
1. GameSetupController#startGame method is a bit big right now and can be broken into several private methods. For instance,
```
        if (playerCount < MINIMUM_PLAYER_COUNT
                || playerCount > MAXIMUM_PLAYER_COUNT) {
            throw new IllegalArgumentException(
                    PLAYER_COUNT_ERROR_MESSAGE
            );
        }
```
can be in `validatePlayerCount(int)` and etc.
2. Your GameSetupController#startGame method is not unit-testable at the moment. I noticed that you have integration tests for these, which is great and you want to keep those. But ideally, we would have wanted to start from unit tests. On a closer look at your design, it's a bit tricky, though. Normally, the advice I would give is to create a package-private constructor like `GameController(Deck deck, List<Player> players, List<Card> cards...)` to inject all the external dependencies into GameSetupController and use this constructor in the unit tests. In your design, however, the GameSetupController doesn't have those instance variables in the first place. Adding those only for testing purpose doesn't seem to be a good tradeoff. So here is what I suggest: Please document in the README under "Exception" something like "GameSetupController doesn't have unit tests but only integration tests. See Week 6 instructor feedback for details."


BTW the pixel art is so cute haha. Good job! Look forward to seeing the final product!
