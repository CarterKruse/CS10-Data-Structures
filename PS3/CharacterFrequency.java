/**
 * Character Frequency
 * A class that holds the data used for Huffman encoding. As a tree node has only one item of data, this class stores
 * a character and a frequency (key-value pair).
 *
 * This class is the data type for the tree. Accessor methods are provided as needed.
 *
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */

public class CharacterFrequency
{
    // Instance Variables - A character and its frequency.
    private Character character;
    private Integer frequency;

    /**
     * Constructor - Assigns parameters to instance variables of the class.
     * @param character The character to store.
     * @param frequency The frequency of the character.
     */
    public CharacterFrequency(Character character, Integer frequency)
    {
        this.character = character;
        this.frequency = frequency;
    }

    // Getter Functions/Methods
    public Character getCharacter()
    {
        return character;
    }

    public Integer getFrequency()
    {
        return frequency;
    }

    // Setter Functions/Methods
    public void setCharacter(Character character)
    {
        this.character = character;
    }

    public void setFrequency(Integer frequency)
    {
        this.frequency = frequency;
    }

    // The toString() method is overwritten to print out the character and frequency of the key: value pair.
    @Override
    public String toString()
    {
        return "" + character + ": " + frequency;
    }
}