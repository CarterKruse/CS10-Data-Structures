import java.io.*;

/**
 * Buffered Bit Reader
 * Reads bits from a file, one at a time. Assumes that the last byte of the file contains the number of valid bits in
 * the previous byte.
 *
 * Throws an exception when EOF, with hasNext() method to test before reading (or could try/catch).
 *
 * @author Scot Drysdale
 * @author Chris Bailey-Kellogg, Spring 2016 (Now returns a boolean instead of an int).
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */
public class BufferedBitReader
{
    /* Note that we need to look ahead 3 bytes, because when the third byte is -1 (EOF indicator) then the second
    byte is a count of the number of valid bits in the first byte.
     */

    int current; // Current byte being returned, bit by bit.
    int next; // Next byte to be returned, which could be a count.
    int afterNext; // Byte two after the current byte.
    int bitMask; // Shows which bit to return.

    BufferedInputStream input;

    /**
     * Constructor - Takes in the path name of the file to open.
     *
     * @param pathName The path name of the file to open.
     */
    public BufferedBitReader(String pathName) throws IOException
    {
        input = new BufferedInputStream(new FileInputStream(pathName));

        current = input.read();

        if (current == -1)
            throw new EOFException("File did not have two bytes.");

        next = input.read();

        if (next == -1)
            throw new EOFException("File did not have two bytes.");

        afterNext = input.read();
        bitMask = 128; // A 1 in leftmost bit position.
    }

    /**
     * Test to decide whether to read the next bit.
     * Input Loop: while (reader.hasNext()) { boolean bit = reader.readBit(); }
     *
     * @return Whether there remains a bit to get (else it's end of file, EOF).
     */
    public boolean hasNext()
    {
        return afterNext != -1 || next != 0;
    }

    /**
     * Reads a bit and returns it as a false or a true.
     * Throws an exception if there isn't one, so use hasNext() to check first, or else catch the EOFException.
     *
     * @return The bit read.
     */
    public boolean readBit() throws IOException
    {
        boolean returnBit; // Hold the bit to return.

        if (afterNext == -1) // Are we emptying the last byte?
        {
            // When afterNext == -1... next is the count of bits remaining.

            if (next == 0) // No more bits in the final byte to return.
            {
                throw new EOFException("No more bits.");
            }

            else
            {
                returnBit = (bitMask & current) != 0;

                next -= 1; // One fewer bit to return.
                bitMask = bitMask >> 1; // Shift to mask next bit.
                return returnBit;
            }
        }

        else
        {
            returnBit = (bitMask & current) != 0;

            bitMask = bitMask >> 1; // Shift to mask next bit.

            // Finished returning this byte?
            if (bitMask == 0)
            {
                bitMask = 128; // Leftmost bit next
                current = next;
                next = afterNext;
                afterNext = input.read();
            }

            return returnBit;
        }
    }

    /**
     * Close this bitReader.
     */
    public void close() throws IOException
    {
        input.close();
    }
}
