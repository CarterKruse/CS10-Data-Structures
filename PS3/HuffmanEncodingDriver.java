/**
 * Huffman Encoding Driver
 * A class that serves as the driver for Huffman compression and decompression.
 *
 * @author Carter Kruse & John DeForest, Dartmouth CS 10, Spring 2022
 */

public class HuffmanEncodingDriver
{
    /**
     * A static method is used to allow for testing of multiple text files.
     */
    private static void testCompressionDecompression(String inputFilePath)
    {
        // Removing the .txt from the end of the input file.
        String path = inputFilePath.substring(0, inputFilePath.length() - 4);

        // Creating the paths for the compressed and decompressed files.
        String compressedFilePath = path + "_compressed.txt";
        String decompressedFilePath = path + "_decompressed.txt";

        // Instantiating a new object of HuffmanEncoding and performing compression and decompression.
        HuffmanEncoding huffmanEncoding = new HuffmanEncoding();
        huffmanEncoding.compressFile(inputFilePath, compressedFilePath);
        huffmanEncoding.decompressFile(compressedFilePath, decompressedFilePath);
    }

    public static void main(String[] args)
    {
        // Use relative path names, with files in the "inputs" folder.
        testCompressionDecompression("inputs/BlankFile.txt");
        testCompressionDecompression("inputs/SingleCharacter.txt");
        testCompressionDecompression("inputs/SingleCharacterRepeated.txt");
        testCompressionDecompression("inputs/HuffmanEncodingTest.txt");
        testCompressionDecompression("inputs/USConstitution.txt");
        testCompressionDecompression("inputs/WarAndPeace.txt");
    }
}
