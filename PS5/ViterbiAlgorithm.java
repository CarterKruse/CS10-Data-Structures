import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Viterbi Algorithm
 * The Viterbi algorithm starts at the # (start) state, with a score of 0, before any observation. Then to handle
 * observation i, it propagates from each reached state at observation i-1, following each transition. The score for
 * the next state through observation i is the sum of the score at the current state through i-1 plus the transition
 * score from current to next plus the score of observation i in next.
 * <p>
 * We are not going to force a "stop" state (ending with a period, question mark, or exclamation point) since the Brown
 * corpus includes headlines that break that rule.
 *
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */
public class ViterbiAlgorithm
{
    private static HiddenMarkovModel markovModel;

    /* In the case where we do not want to completely rule out something that we have never seen, we give it a low log
    probability, which is a negative number that is worse than the observed ones, but not totally out of the realm.
     */
    private static int unseenPenalty = -100;

    /**
     * Train Model
     * Uses the HiddenMarkovModel class to train the model.
     *
     * @param trainSentencesFile The file path for the training sentences.
     * @param trainTagsFile      The file path for the training tags.
     */
    public static void trainModel(String trainSentencesFile, String trainTagsFile)
    {
        // Initializing new ArrayLists which serve to hold all the contents of the training files.
        List<String> words = new ArrayList<>();
        List<String> partsOfSpeech = new ArrayList<>();

        // Using the InputLibrary methods to load the sentences and tags.
        List<List<String>> wordsArray = InputLibrary.loadSentences(trainSentencesFile);
        List<List<String>> partsOfSpeechArray = InputLibrary.loadTags(trainTagsFile);

        // Cycling through the wordsArray to add the Strings to the ArrayList.
        for (List<String> item : wordsArray)
        {
            words.addAll(item);
        }

        // Cycling through the partsOfSpeechArray to add the Strings to the ArrayList.
        for (List<String> item : partsOfSpeechArray)
        {
            partsOfSpeech.addAll(item);
        }

        // Creating a new Hidden Markov Model and going through the training.
        markovModel = new HiddenMarkovModel();
        markovModel.trainObservations(partsOfSpeech, words);
        markovModel.trainTransitions(partsOfSpeechArray);
    }

    /**
     * Tag Sentence
     * A part of speech (POS) tagger labels each word in a sentence with its part of speech (noun, verb, etc.). The goal
     * of POS tagging is to take a sequence of words and produce the corresponding sequence of tags.
     * <p>
     * We only need to keep the current and next scores, which simplifies the representation we use in code.
     *
     * @param sentence The sentence to tag with parts of speech.
     */
    public static List<String> tagSentence(List<String> sentence)
    {
        // Checking to ensure that the sentence is not empty or null.
        if (sentence == null || sentence.isEmpty())
        {
            System.err.println("Error: Invalid Input");
            return null;
        }

        // Creating a List of Strings for the currentStates and adding "#".
        List<String> currentStates = new ArrayList<>();
        currentStates.add("#");

        // Creating a Map for the currentScores and adding ("#", 0.0).
        Map<String, Double> currentScores = new HashMap<>();
        currentScores.put("#", 0.0);

        /* The back trace needs to go all the way back: for observation i, for each state, what was the previous state
        at observation i-1 that produced the best score upon transition and observation.
         */
        List<Map<String, String>> backTraceList = new ArrayList<>();

        // Extracting the transitionMap and observationMap from the Hidden Markov Model.
        Map<String, Map<String, Double>> transitionMap = markovModel.getTransitionMap();
        Map<String, Map<String, Double>> observationMap = markovModel.getObservationMap();

        // Cycling through each word in the sentence.
        for (int i = 0; i < sentence.size(); i += 1)
        {
            // Extracting the word in the sentence.
            String word = sentence.get(i);

            // Creating a new List of nextStates and Map of nextScores.
            List<String> nextStates = new ArrayList<>();
            Map<String, Double> nextScores = new HashMap<>();

            // Adding a new HashMap to the backTraceList.
            backTraceList.add(new HashMap<>());

            // Cycling through each state in the List of current states.
            for (String currentState : currentStates)
            {
                // Check to see if the transitionMap contains the current state. If not, skip this cycle.
                if (!transitionMap.containsKey(currentState))
                    continue;

                /* After handling the special case of the start state, we start with the first observation and work
                forward, considering all possible states from which to come and looking forward to where they could go.
                 */

                // Cycling through each nextState in the List of nextStates, according to the transitionMap.
                for (String nextState : transitionMap.get(currentState).keySet())
                {
                    // Scoring the next state/observation according to the current score and transition score.
                    double nextScore = currentScores.get(currentState) + transitionMap.get(currentState).get(nextState);

                    // If the observationMap does not contain the next word, we add on the unseenPenalty.
                    if (!observationMap.get(nextState).containsKey(word))
                        nextScore += unseenPenalty;

                        // Otherwise, we simply add on the observation score, given from the observationMap.
                    else
                        nextScore += observationMap.get(nextState).get(word);

                    /* We find the max score (and keep track of which state gave it). Checking to see if the nextScores
                    Map has a value for the nextState or if the nextScore is greater than the value.
                     */
                    if (!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState))
                    {
                        // Adding the nextState to the List of nextStates.
                        nextStates.add(nextState);

                        // Updating the Map of nextScores.
                        nextScores.put(nextState, nextScore);

                        // Updating the backTraceList to reflect the best score for each observation.
                        backTraceList.get(i).put(nextState, currentState);
                    }
                }
            }

            // Updating the currentStates and currentScores appropriately.
            currentStates = nextStates;
            currentScores = nextScores;
        }

        // Finding the best last state, according to the final scores.
        String lastState = findMaxEndState(currentScores);

        // Returning the List of tags, according to the backTraceList and lastState.
        return findPath(backTraceList, lastState);
    }

    /**
     * Find Max End State - Helper function to determine the best tag to use for the last word.
     *
     * @param currentScores The Map containing the current scores.
     */
    public static String findMaxEndState(Map<String, Double> currentScores)
    {
        // Setting the initial maxScore as low as possible to ensure it is modified.
        double maxScore = Double.NEGATIVE_INFINITY;

        // Initializing the tag that has the best match for the ending observation.
        String bestTagMatch = "";

        // Cycling through the tags in the keySet of the currentScores Map.
        for (String tag : currentScores.keySet())
        {
            // Extracting the currentScore from the Map.
            double currentScore = currentScores.get(tag);

            // If the currentScore is greater than the maxScore...
            if (currentScore > maxScore)
            {
                // Update the maxScore and set the bestTagMatch to be the tag.
                maxScore = currentScore;
                bestTagMatch = tag;
            }
        }

        return bestTagMatch;
    }

    /**
     * Find Path - Helper function that allows the back trace to start from the state with the best score for the last
     * observation, working back to the start state.
     *
     * @param backTrackList The List of Maps containing the appropriate transitions from state to state.
     * @param lastState     The lastState to work backward from.
     */
    public static List<String> findPath(List<Map<String, String>> backTrackList, String lastState)
    {
        // Initializing a new ArrayList that will hold the tags (backward path through backTraceList).
        List<String> path = new ArrayList<>();

        // Ensuring that there is not an OutOfBounds Exception by expanding the ArrayList appropriately.
        for (int i = backTrackList.size() - 1; i >= 0; i -= 1)
            path.add("");

        // Cycling through the backTrackList in reverse.
        for (int i = backTrackList.size() - 1; i >= 0; i -= 1)
        {
            // Setting the lastState to the appropriate index.
            path.set(i, lastState);

            // Updating the lastState using the backTrackList.
            lastState = backTrackList.get(i).get(lastState);
        }

        return path;
    }
}