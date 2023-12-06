import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Input Library
 * Class used to load the sentences and corresponding tags from given files.
 *
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */
public class InputLibrary
{
    // A list of lists is used to store both the sentences and tags, line by line.
    static List<List<String>> sentences;
    static List<List<String>> tags;

    static BufferedReader sentencesInput;
    static BufferedReader tagsInput;

    /**
     * Load Sentences
     * Takes in a file, and outputs a list of lists containing strings of the words in each sentence.
     */
    public static List<List<String>> loadSentences(String filename)
    {
        // Creating a new ArrayList to hold the interior lists.
        sentences = new ArrayList<>();

        try
        {
            // Initializing the BufferedReader with the appropriate file.
            sentencesInput = new BufferedReader(new FileReader(filename));

            String line;
            while ((line = sentencesInput.readLine()) != null && line.length() > 0) // Cycling through the input.
            {
                // Creating a new list containing the strings in the sentence, which are separated by spaces.
                List<String> sentence = List.of(line.split(" "));
                List<String> fixedSentence = new ArrayList<>();

                // Making every word lower case.
                for (String item : sentence)
                    fixedSentence.add(item.toLowerCase());
                    // item.toLowerCase();

                // Adding the list containing the strings to the larger list.
                sentences.add(fixedSentence);
            }
        }

        // Catching any IOException.
        catch (IOException e)
        {
            System.err.println("Error: " + e.getMessage());
        }

        return sentences;
    }

    /**
     * Load Tags
     * Takes in a file, and outputs a list of lists containing strings of the tags.
     */
    public static List<List<String>> loadTags(String filename)
    {
        // Creating a new ArrayList to hold the interior lists.
        tags = new ArrayList<>();

        try
        {
            // Initializing the BufferedReader with the appropriate file.
            tagsInput = new BufferedReader(new FileReader(filename));

            String line;
            while ((line = tagsInput.readLine()) != null && line.length() > 0) // Cycling through the input.
            {
                // Creating a new list containing the strings in the sentence, which are separated by spaces.
                List<String> tagList = List.of(line.split(" "));

                // Making every tag lower case.
                for (String item : tagList)
                    item.toLowerCase();

                // Adding the list containing the strings to the larger list.
                tags.add(tagList);
            }
        }

        // Catching any IOException.
        catch (IOException e)
        {
            System.err.println("Error: " + e.getMessage());
        }

        return tags;
    }
}

