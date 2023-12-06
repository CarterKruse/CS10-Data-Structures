import java.util.*;

/**
 * Hidden Markov Model
 * To accomplish POS tagging, we will use a hidden Markov model. In an HMM, the states are things we do not see and are
 * trying to infer, and the observations are what we do see. Thus, the observations are words in a sentence and the states
 * are tags because the text we will observe is not annotated with its part of speech tag.
 * <p>
 * We will proceed through a model by moving from state to state, producing one observation per state. In this "bigram"
 * model, each tag depends on the previous tag. Then each word depends on the tag. We let "#" be the tag before the start
 * of the sentence.
 * <p>
 * An HMM is defined by its states (here parts of speech tags), transitions (here tag to tag, with weights), and observations
 * (here tag to word, with weights). Probabilities are computed for the tags: for each tag, the frequencies of the transitions
 * out are divided by the total number of transitions out, and the frequencies of the words it tags are divided by the total
 * number of words it tags.
 *
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */
public class HiddenMarkovModel
{
    /* Though we think of the model as a graph, we do not need to use the Graph class. Instead, we keep (nested) Maps of
    transition and observation probabilities.

    The transitionMap keySet is the part of speech (origin), the keySet of the inner map is the part of speech (transition).
    The observationMap keySet is the part of speech, the keySet of the inner map is the word.
     */
    private Map<String, Map<String, Double>> transitionMap;
    private Map<String, Map<String, Double>> observationMap;

    /**
     * Constructor - Initializes the transitionMap and observationMap.
     */
    public HiddenMarkovModel()
    {
        transitionMap = new HashMap<>();
        observationMap = new HashMap<>();
    }

    /**
     * Getter - Transition Map
     */
    public Map<String, Map<String, Double>> getTransitionMap()
    {
        return transitionMap;
    }

    /**
     * Getter - Observation Map
     */
    public Map<String, Map<String, Double>> getObservationMap()
    {
        return observationMap;
    }

    /**
     * Train Observations
     * Making a pass through the training data to count the number of times we see each observation.
     *
     * @param partsOfSpeech A list of the parts of speech (for a sentence), given as Strings.
     * @param words         A list of the words (in a sentence), given as Strings.
     */
    public void trainObservations(List<String> partsOfSpeech, List<String> words)
    {
        // Checking to make sure that the size of both lists is the same and is at least 1.
        if (partsOfSpeech.size() < 1 || words.size() < 1 || partsOfSpeech.size() != words.size())
        {
            System.err.println("Invalid Input - Incorrect Training Data");
            return;
        }

        // Cycling through the list of the parts of speech.
        for (int i = 0; i < partsOfSpeech.size(); i += 1)
        {
            // For each state (part of speech) in the list...
            String state = partsOfSpeech.get(i);

            // If the observationMap does not contain the state...
            if (!observationMap.containsKey(state))
                observationMap.put(state, new HashMap<>()); // Insert a new Map into the observationMap.

            // Extracting a given Map from the observationMap.
            Map<String, Double> map = observationMap.get(state);

            // Checking to see if the map contains the word associated with the tag.
            if (!map.containsKey(words.get(i)))
                map.put(words.get(i), 0.0); // If not, insert a new value into the map.

            // Increase the frequency count by one for a given word in the map.
            map.put(words.get(i), map.get(words.get(i)) + 1.0);
        }

        // Cycling through the states (parts of speech) in the observationMap.
        for (String state : observationMap.keySet())
        {
            double totalInstances = 0;

            // Cycling through the words in the keySet of the inner Map.
            for (String word : observationMap.get(state).keySet())
            {
                // Increasing the number of total instances by the value for a given word.
                totalInstances += observationMap.get(state).get(word);
            }

            // Cycling through the words in the keySet of the inner Map.
            for (String word : observationMap.get(state).keySet())
            {
                // Normalizing each state's counts to probabilities, converting to log probabilities.
                double logScore = Math.log(observationMap.get(state).get(word) / totalInstances);

                // double logScore = observationMap.get(state).get(word);
                observationMap.get(state).put(word, logScore);
            }
        }

        // System.out.println(observationMap);
    }

    /**
     * Train Transitions
     * Making a pass through the training data to count the number of times we see each transition.
     *
     * @param partsOfSpeechTags A list of lists of the parts of speech, given as Strings.
     */
    public void trainTransitions(List<List<String>> partsOfSpeechTags)
    {
        // Checking to make sure that the size of the list is at least 1.
        if (partsOfSpeechTags.size() < 1)
        {
            System.err.println("Invalid Input - Incorrect Training Data");
            return;
        }

        // Cycling through the outer List.
        for (List<String> partsOfSpeech : partsOfSpeechTags)
        {
            String previous = "#";

            // Cycling through the list of the parts of speech.
            for (int i = 0; i < partsOfSpeech.size(); i += 1)
            {
                // For each state (part of speech) in the list...
                String state = partsOfSpeech.get(i);

                // If the transitionMap does not contain the state...
                if (!transitionMap.containsKey(previous))
                    transitionMap.put(previous, new HashMap<>()); // Insert a new Map into the transitionMap.

                // Extracting a given Map from the transitionMap, based on the previous state.
                Map<String, Double> map = transitionMap.get(previous);

                // Checking to see if the map contains the tag (associated with the transition).
                if (!map.containsKey(state))
                    map.put(state, 0.0); // If not, insert a new value into the map.

                // Increase the frequency count by one for a given word in the map.
                map.put(state, map.get(state) + 1.0);

                // Update the previous state.
                previous = state;
            }
        }

        // Cycling through the states (parts of speech) in the transitionMap.
        for (String state : transitionMap.keySet())
        {
            double totalInstances = 0;

            // Cycling through the tags in the keySet of the inner Map.
            for (String transition : transitionMap.get(state).keySet())
            {
                // Increasing the number of total instances by the value for a given tag.
                totalInstances += transitionMap.get(state).get(transition);
            }

            // Cycling through the tags in the keySet of the inner Map.
            for (String transition : transitionMap.get(state).keySet())
            {
                // Normalizing each state's counts to probabilities, converting to log probabilities.
                double logScore = Math.log(transitionMap.get(state).get(transition) / totalInstances);

                // double logScore = transitionMap.get(state).get(transition);
                transitionMap.get(state).put(transition, logScore);
            }
        }

        // System.out.println(transitionMap);
    }
}