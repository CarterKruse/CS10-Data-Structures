import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Testing Viterbi
 * To assess how good a model is, we can compute how many tags it gets right and how many it gets wrong on some test
 * sentences.
 * <p>
 * The test files use contain sets, one pair with the sentences and a corresponding one with the tags to be used for
 * training, and another pair with the sentences and tags for testing. Each line is a single sentence (or a headline),
 * cleanly separated by whitespace into words/tokens, with punctuation also thus separated out.
 * <p>
 * We use the train sentences and train tags files to generate the HMM, and then apply it to each line in the test
 * sentences file, comparing the results to the corresponding test tags line. We count the number of correct vs. incorrect
 * tag.
 *
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */
public class TestingViterbi
{
    public static void main(String[] args) throws IOException
    {
        // BROWN CORPUS
        // Loading in the testing sentences and tags as "testInput" and "testCompare", respectively.
        // List<List<String>> brownTestInput = InputLibrary.loadSentences("PS5/texts/brown-test-sentences.txt");
        // List<List<String>> brownTestCompare = InputLibrary.loadTags("PS5/texts/brown-test-tags.txt");

        // Training the HMM model for the Viterbi Algorithm based on the Brown corpus.
        // ViterbiAlgorithm.trainModel("PS5/texts/brown-train-sentences.txt", "PS5/texts/brown-train-tags.txt");

        ViterbiAlgorithm.trainModel("PS5/texts/seuss-train-sentences.txt", "PS5/texts/seuss-train-tags.txt");
        // testFromFiles(brownTestInput, brownTestCompare);

        // SIMPLE FILES

        // List<List<String>> testInput = InputLibrary.loadSentences("PS5/texts/simple-test-sentences.txt");
        // List<List<String>> testCompare = InputLibrary.loadTags("PS5/texts/simple-test-tags.txt");

        // Training the HMM model for the Viterbi Algorithm based on the simple files.
        // ViterbiAlgorithm.trainModel("PS5/texts/simple-train-sentences.txt", "PS5/texts/simple-train-tags.txt");
        // testFromFiles(testInput, testCompare);

        // HARD-CODED STRINGS

//        List<List<String>> hardCodedTestInput = new ArrayList<>();
//        List<List<String>> hardCodedTestCompare = new ArrayList<>();

        // List<String> hardCodedTestSentence = Arrays.asList("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", ".");
        // List<String> hardCodedTestTags = Arrays.asList("DET", "ADJ", "ADJ", "N", "V", "P", "DET", "ADJ", "N", ".");

        // List<String> hardCodedTestSentence = Arrays.asList("CS", "is", "an", "enjoyable", "course", ".");
        // List<String> hardCodedTestTags = Arrays.asList("NP", "V", "DET", "ADJ", "N", ".");

        // List<String> hardCodedTestSentence = Arrays.asList("My", "name", "is", "Carter", "Kruse", ".");
        // List<String> hardCodedTestTags = Arrays.asList("DET", "N", "V", "NP", "VP", ".");

        // List<String> hardCodedTestSentence = Arrays.asList("The", "purpose", "of", "our", "lives", "is", "to", "be", "happy", ".");
        // List<String> hardCodedTestTags = Arrays.asList("DET", "N", "P", "PRO", "N", "V", "TO", "V", "ADJ", ".");

        // List<String> hardCodedTestSentence = Arrays.asList("John", "walked", "the", "dog", "through", "the", "neighborhood", ".");
        // List<String> hardCodedTestTags = Arrays.asList("NP", "VD", "DET", "N", "P", "DET", "N", ".");

        // hardCodedTestInput.add(hardCodedTestSentence);
        // hardCodedTestCompare.add(hardCodedTestTags);

        // testFromFiles(hardCodedTestInput, hardCodedTestCompare);

        // CONSOLE
        testFromConsole();
    }

    /**
     * Test From Files - A file-based test method to evaluate the performance on a pair of test files (corresponding
     * lines with sentences and tags).
     *
     * @param observations The List of sentences (containing a List of Strings) of the words from a given input.
     * @param testTags     The List tag groups (containing a List of Strings) of the tags from a given input.
     */
    public static void testFromFiles(List<List<String>> observations, List<List<String>> testTags)
    {
        double total = 0.0;
        double matches = 0.0;

        // Cycling through the List of observations.
        for (int i = 0; i < observations.size(); i += 1)
        {
            // Extracting the List of Strings representing the words in a given sentence.
            List<String> sentence = observations.get(i);

            // Creating a new List of Strings of the tags, based on the sentence.
            List<String> tags = ViterbiAlgorithm.tagSentence(sentence);

            // Extracting the List of Strings representing the appropriate tags for a given sentence.
            List<String> givenTags = testTags.get(i);

            // Cycling through the words/tags.
            for (int j = 0; j < observations.get(i).size(); j += 1)
            {
                // If the tag we found using the Viterbi algorithm matches the given tag, we increase the number of matches.
                if (tags.get(j).equals(givenTags.get(j)))
                    matches += 1;

                // Regardless, we increase the total number of cases considered.
                total += 1;
            }
        }

        // Printing out the results to the console.
        System.out.println("Correct: " + matches);
        System.out.println("Incorrect: " + (total - matches));
        System.out.println("Percentage: " + (matches * 100 / total));
    }

    /**
     * Test From Console - Console-base test method that gives tags from an input line.
     */
    public static void testFromConsole()
    {
        // Initializing a new Scanner from System.in.
        Scanner scanner = new Scanner(System.in);

        // Waiting for user input.
        while (true)
        {
            // Prompt and extracting response from user.
            System.out.println("Enter A Sentence: ");
            String line = scanner.nextLine().toLowerCase();

            // Quiting the execution of the method.
            if (line.equals("q"))
                break;

            // Using the Viterbi algorithm to tag the sentence (words are split by spaces).
            List<String> consoleTags = ViterbiAlgorithm.tagSentence(List.of(line.split(" ")));

            // Printing out the tags.
            System.out.println(consoleTags);
            System.out.println();
        }
    }
}
