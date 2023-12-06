import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Buffered Bit Writer
 * Writes bits to a file. Accumulates bits until it gets a byte, then writes it. On closing, writes an additional byte
 * holding the number of valid bits in the final byte written.
 *
 * @author Scot Drysdale
 * @author Chris Bailey-Kellogg, Spring 2016 (Bits are now boolean.)
 * @author CBK, Fall 2016 (Max to write to avoid filling filesystem when you have an infinite loop.)
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */
public class BufferedBitWriter
{
    private byte currentByte; // The byte that is being filled.
    private byte numBitsWritten; // The number of bits written to the current byte.
    public static int maxBytes = 1000000000; // So we can bail out if the file gets too big.
    private int totalBytes; // Exception occurs when this exceeds the max.
    private BufferedOutputStream output; // The output byte stream.

    /**
     * Constructor - Takes in the path name of the file to be written.
     *
     * @param pathName The path name of the file to be written.
     */
    public BufferedBitWriter(String pathName) throws FileNotFoundException
    {
        currentByte = 0;
        numBitsWritten = 0;
        totalBytes = 0;
        output = new BufferedOutputStream(new FileOutputStream(pathName));
    }

    /**
     * Writes a bit to the file (virtually). Takes in the bit to be written.
     *
     * @param bit The bit to be written.
     */
    public void writeBit(boolean bit) throws IOException
    {
        numBitsWritten += 1;
        currentByte |= (bit ? 1 : 0) << (8 - numBitsWritten);

        // Do we have a full byte?
        if (numBitsWritten == 8)
        {
            output.write(currentByte);

            // Reset the number of bits written and the current byte, along with updating the total number of bytes.
            numBitsWritten = 0;
            currentByte = 0;
            totalBytes += 1;

            // Checking the overflow condition.
            if (totalBytes >= maxBytes)
            {
                throw new IOException("File Overflow -> Do you have an infinite loop?");
            }
        }
    }

    /**
     * Closes this bitstream. Writes any partial byte, followed by the number of valid bits in the final byte.
     * The file will always have at least 2 bytes. Any file representing no bits will have two zero bytes.
     * If this is not called the file will not be correctly read by a BufferedBitReader.
     */
    public void close() throws IOException
    {
        output.write(currentByte);
        output.write(numBitsWritten);

        output.close();
    }
}
