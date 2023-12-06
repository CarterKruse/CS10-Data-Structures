import java.io.*;
import java.util.*;

/**
 * Huffman Encoding
 * One of the earliest schemes for lossless file compression was invented by Huffman. Instead of using 7 bits to encode
 * each character, as ASCII does, it uses a variable-length encoding of characters. Frequently occurring characters
 * get shorter code words than infrequently occurring ones. (A code word is the sequence of 0's and 1's used to encode
 * the character.)
 *
 * Huffman encoding gives the smallest possible fixed encoding of a file. A fixed encoding means that a given letter is
 * represented by the same code wherever it appears in the file. Some more recent schemes that do better than
 * Huffman's are adaptive, which means that how a letter is encoded changes as the file is processed.
 *
 * The task is to generate a set of prefix-free codes whose lengths are inversely correlated with the frequency of the
 * encoded character. There are two clever parts of the algorithm, the use of a binary tree to generate the codes, and
 * the construction of the binary tree using a priority queue. Specifically we will construct a tree such that each
 * character is a leaf and the path from the root to that character gives that character's code word, where a left
 * child is interpreted as a 0 and a right child as a 1.
 *
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */

public class HuffmanEncoding
{
    // Instance Variables
    private BufferedReader input;
    private Map<Character, Integer> frequencyTable;
    private PriorityQueue<BinaryTree<CharacterFrequency>> treePriorityQueue;
    private Map<Character, String> characterCodeMap;

    /**
     * Generate Frequency Table
     * The first step in creating the code for a given file is to learn what the character frequencies are.
     *
     * This is fairly simple. First we create a Map with characters as keys and a frequencies as values. Then we read
     * the file one character at a time. We add the character to the map if it is not there and increment the
     * corresponding map value if it is there. At the end we will have a map that maps each character to the number of
     * times that it appears in the file.
     *
     * In Java, we have to use appropriate wrapper classes to store the character and the frequency, because we cannot
     * use primitives in a Map.
     */
    private void generateFrequencyTable() throws IOException
    {
        /* Instantiating a new Map, which serves as the frequency table. Holds a character as a key
        and an integer (frequency) as the value.
         */
        frequencyTable = new TreeMap<Character, Integer>();

        int characterValue = input.read(); // Reading in from the BufferedReader.

        // Checking to see if the BufferedReader has hit the end of the file.
        while (characterValue != -1)
        {
            // Casting the integer value of the character to a character.
            Character character = (char) characterValue;

            // Check to see if the frequency table (Map) already contains the character, which serves as the key.
            if (frequencyTable.containsKey(character))
            {
                // If so, update the frequency to be increased by one.
                frequencyTable.put(character, frequencyTable.get(character) + 1);
            }

            // Otherwise, place the key: value pair in the frequency table (Map).
            else
            {
                frequencyTable.put(character, 1);
            }

            // Read the next character.
            characterValue = input.read();
        }

        // Close the input file.
        input.close();
    }

    /**
     * Create Priority Queue
     * We now create an initial tree for each character. The data consists of two values, a character and its frequency.
     * We then add all these initial single-character trees to a priority queue. The priority queue is set up to return
     * the tree with the lowest frequency when asked to remove.
     *
     * Java's PriorityQueue class implements a heap-based priority queue. The things that we are comparing are the
     * frequency counts in the root nodes of two trees.
     */
    private void createPriorityQueue()
    {
        // Tree Comparator - We write a separate TreeComparator class that implements the Comparator interface.
        class TreeComparator implements Comparator<BinaryTree<CharacterFrequency>>
        {
            /* The only method that we implement is compare, which takes two tree nodes and returns -1, 0, or 1
            depending on whether the first has a smaller frequency count, the counts are equal, or the second has the
            smaller frequency count.
             */
            @Override
            public int compare(BinaryTree<CharacterFrequency> firstTree, BinaryTree<CharacterFrequency> secondTree)
            {
                return firstTree.getData().getFrequency().compareTo(secondTree.getData().getFrequency());
            }
        }

        // We create a new TreeComparator object and pass it to the PriorityQueue constructor.
        Comparator<BinaryTree<CharacterFrequency>> treeComparator = new TreeComparator();
        treePriorityQueue = new PriorityQueue<BinaryTree<CharacterFrequency>>(treeComparator);

        // For each character (key) in the set of all characters in the frequency table...
        for (Character key : frequencyTable.keySet())
        {
            // We create a Binary Tree of type CharacterFrequency with the character and frequency.
            BinaryTree<CharacterFrequency> initialTree = new BinaryTree<CharacterFrequency>(new CharacterFrequency(key, frequencyTable.get(key)));
            treePriorityQueue.add(initialTree); // Adding the Binary Tree to the Priority Queue.
        }
    }

    /**
     * Create Tree
     * We represent the paths to leaves in a tree as a sequence of bit values, '0' for following the left path to the
     * left child and '1' for following the right path. This way we create a variable length bit code for each leaf-node
     * character. We also see that a path to a leaf L cannot contain the bit code for a path to any other leaf L',
     * because if it did then L' would not be a leaf.
     *
     * In creating the tree we would like to have the lowest frequency characters be deepest in the tree, and hence have
     * the longest bit codes, and the highest frequency characters be near the top of the tree. We use our priority
     * queue to achieve this.
     */
    private void createTree()
    {
        // Checking if the size of the priority queue is one (there is a single character/tree).
        if (treePriorityQueue.size() == 1)
        {
            // If so, we only take the first (and only) tree from the priority queue.
            BinaryTree<CharacterFrequency> firstTree = treePriorityQueue.poll();

            // Creating a new tree by creating a new root node, attaching the first tree as the root's left subtree.
            CharacterFrequency rootNode = new CharacterFrequency(null, firstTree.getData().getFrequency());

            // Inserting the new tree into the priority queue (which will base its priority on its frequency value).
            treePriorityQueue.add(new BinaryTree<CharacterFrequency>(rootNode, firstTree, null));
        }

        else
        {
            // Checking to ensure the size of the priority queue is at least two.
            while (treePriorityQueue.size() > 1)
            {
                // Extracting the two lowest-frequency trees from the priority queue.
                BinaryTree<CharacterFrequency> firstTree = treePriorityQueue.poll();
                BinaryTree<CharacterFrequency> secondTree = treePriorityQueue.poll();

                /* Creating a new tree by creating a new root node, attaching the first tree as the root's left subtree,
                and attaching the second tree as the root's right subtree.

                Assigning to the new tree a frequency that equals the sum of the frequencies of the first and second tree.
                The character value at an inner node doesn't matter; that's only used for a leaf.
                 */
                CharacterFrequency rootNode = new CharacterFrequency(null, firstTree.getData().getFrequency() + secondTree.getData().getFrequency());

                // Inserting the new tree into the priority queue (which will base its priority on its frequency value).
                treePriorityQueue.add(new BinaryTree<CharacterFrequency>(rootNode, firstTree, secondTree));
            }
        }
    }

    /**
     * Create Character Code Map
     * The tree encodes all the information about the code for each character, but given a character it is a bother to
     * search through the tree to find it in a leaf and trace its path from the root. To make encoding fast we want a
     * Map that pairs characters with their code words. That is, we want to pair each character with a string of '0's
     * and '1's that describes the path from the root to that character.
     */
    private void createCharacterCodeMap()
    {
        // Creating a new Map to hold a character as a key and a string of 0's and 1's as the value.
        characterCodeMap = new TreeMap<Character, String>();
        String pathSoFar = "";

        // Checking to make sure that the Priority Queue is not empty.
        if (treePriorityQueue.peek() != null)
        {
            // Using a recursive function to generate the code map, using the "path so far" variable.
            createCharacterCodeMapHelper((TreeMap<Character, String>) characterCodeMap, pathSoFar, treePriorityQueue.peek());
        }
    }

    /**
     * Create Character Code Map Helper
     * We can construct the entire map during a single traversal of the tree. We just have to keep track of a
     * "path so far" parameter as we do the traversal.
     *
     * @param characterCodeMap The code map which contains the keys and values for all characters.
     * @param pathSoFar The path already taken when traversing the tree.
     * @param seedNode The node of the Binary Tree to consider during a given step in recursion.
     */
    private void createCharacterCodeMapHelper(TreeMap<Character, String> characterCodeMap, String pathSoFar, BinaryTree<CharacterFrequency> seedNode)
    {
        // If the node of the Binary Tree is a leaf...
        if (seedNode.isLeaf())
        {
            // Add the character and the path taken on the Binary Tree to the code map.
            characterCodeMap.put(seedNode.getData().getCharacter(), pathSoFar);
        }

        // If the node of the Binary Tree still has a left child...
        if (seedNode.hasLeft())
        {
            // Recurse by moving to the left child and adding the string "0" to the path so far.
            createCharacterCodeMapHelper(characterCodeMap, pathSoFar + "0", seedNode.getLeft());
        }

        // If the node of the Binary Tree still has a right child...
        if (seedNode.hasRight())
        {
            // Recurse by moving to the right child and adding the string "1" to the path so far.
            createCharacterCodeMapHelper(characterCodeMap, pathSoFar + "1", seedNode.getRight());
        }
    }

    /**
     * Compress File
     * To compress, we repeatedly read the next character in your text file, look up its code word in the code map,
     * and then write the sequence of 0's and 1's in that code word as bits to an output file.
     *
     * @param inputFilePath The file path for the input file.
     * @param compressedFilePath The file path for the compressed file.
     */
    public void compressFile(String inputFilePath, String compressedFilePath)
    {
        // As IOException is a checked exception, we include try and catch blocks.
        try
        {
            // Instantiating a new BufferedReader based on the input file.
            input = new BufferedReader(new FileReader(inputFilePath));

            // Creating a new BufferedBitWriter with the compressed file path.
            BufferedBitWriter compressedOutput = new BufferedBitWriter(compressedFilePath);

            // Calling all helper functions, in order.
            generateFrequencyTable();
            createPriorityQueue();
            createTree();
            createCharacterCodeMap();

            /* Re-instantiating a new BufferedReader based on the input file, as the generateFrequencyTable method
            will move the BufferedReader to the end of the text file.
             */
            input = new BufferedReader(new FileReader(inputFilePath));

            int characterValue = input.read(); // Reading in from the BufferedReader.

            // Checking to see if the BufferedReader has hit the end of the file.
            while (characterValue != -1)
            {
                // Casting the integer value of the character to a character.
                Character character = (char) characterValue;

                // Extracting the characterCode based on the Map we created in createCharacterCodeMap.
                String characterCode = characterCodeMap.get(character);

                // For each character in the string of the character code...
                for (Character c : characterCode.toCharArray())
                {
                    // We write the bit according to whether or not the character is 0 or 1.
                    compressedOutput.writeBit(c != '0');
                }

                // Read the next character.
                characterValue = input.read();
            }

            // Close the compressedOutput file.
            compressedOutput.close();
        }

        // Catching the IOException and handling it.
        catch (IOException e)
        {
            System.out.println("Error: " + e.getMessage());
        }

        // Even if the IOException is caught, the input file may not be closed, so we handle that.
        finally
        {
            // Checking to make sure the input file is not null.
            if (input != null)
            {
                // Closing the input file.
                try
                {
                    input.close();
                }

                // Catching an IOException, though this will never be the case.
                catch (IOException e)
                {
                    System.out.println("Error: " + e);
                }
            }
        }
    }

    /**
     * Decompress File
     * Decompression is similar to compression, in that we read from one file and write to another. To decode, run down
     * the code tree until we reach a leaf. Start at the root and read the first bit from the compressed file. If it is
     * a '0' go left and if it is a '1' go right. Repeatedly read a bit and go left or right until you reach a leaf.
     * We have now decoded your first character.
     *
     * Get the character out of the current tree node's data and write it to the output file. Then go back to the root
     * and repeat the process. When there are no more bits to read, the file is decompressed.
     *
     * @param compressedFilePath The file path for the compressed file.
     * @param decompressedFilePath The file path for the decompressed file.
     */
    public void decompressFile(String compressedFilePath, String decompressedFilePath)
    {
        // The try-catch handles the IO exceptions.
        try
        {
            // Creating a new BufferedBitReader with the compressed file path.
            BufferedBitReader compressedInput = new BufferedBitReader(compressedFilePath);

            // Creating a new BufferedWriter with the decompressed file path.
            BufferedWriter output = new BufferedWriter(new FileWriter(decompressedFilePath));

            // Creating a new Binary Tree that is based on the priority queue with the Binary Trees.
            BinaryTree<CharacterFrequency> codeTree = treePriorityQueue.peek();

            // Checking to make sure the Binary Tree is not null.
            if (codeTree != null)
            {
                // Checking to see if the BufferedBitReader has hit the end of the file.
                while (compressedInput.hasNext())
                {
                    // Reading in the bit as a boolean value.
                    boolean bit = compressedInput.readBit();

                    // If the bit is equal to 1...
                    if (bit)
                    {
                        // Check to see if the codeTree has a right child.
                        if (codeTree.hasRight())
                        {
                            // If so, we move to the right child of the node.
                            codeTree = codeTree.getRight();
                        }
                    }

                    // If the bit is equal to 0...
                    else
                    {
                        // Check to see if the codeTree has a left child.
                        if (codeTree.hasLeft())
                        {
                            // If so, we move to the left child of the node.
                            codeTree = codeTree.getLeft();
                        }
                    }

                    // If the node is a leaf...
                    if (codeTree.isLeaf())
                    {
                        // We write the character to the decompressed file.
                        output.write((int) codeTree.getData().getCharacter());

                        // Resetting the code tree to the overall Binary Tree based on the priority queue.
                        codeTree = treePriorityQueue.peek();
                    }
                }
            }

            // Close the compressedOutput and output files.
            compressedInput.close();
            output.close();
        }

        // Catching an IOException.
        catch (IOException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Testing Code - Takes in HuffmanEncodingTest.txt, and prints out the frequency table (Map), the priority queue,
     * and the character code map.
     */
    public static void main(String[] args) throws IOException
    {
        HuffmanEncoding huffmanEncoding = new HuffmanEncoding();

        huffmanEncoding.input = new BufferedReader(new FileReader("inputs/HuffmanEncodingTest.txt"));

        huffmanEncoding.generateFrequencyTable();
        System.out.println(huffmanEncoding.frequencyTable);

        huffmanEncoding.createPriorityQueue();
        huffmanEncoding.createTree();
        System.out.println(huffmanEncoding.treePriorityQueue);

        huffmanEncoding.createCharacterCodeMap();
        System.out.println(huffmanEncoding.characterCodeMap);
    }
}
