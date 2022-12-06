package cgcp.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Simple class with some utility methods.
 *
 * @author Hoang Tran
 */
public class Util {

	/***
     * The static NAL value
     */
	public static int NAL_ = -1;
    
	/**
     * Convert a Long number to a readable string
     *
     * @param value		the long number
     * @return the human-friendly number as a String
     */
    public static String longToString(long value) {
        return value >= 1e11 ? String.format("%.0fG", value / 1e9)
          : value >= 1e7 ? String.format("%.0fM", value / 1e6)
          : (value >= 1e4) ? String.format("%.0fK", value / 1e3)
          : Long.toString(value);
    }

    /**
     * Print the table header
     *
     * @param output  the output stream
     */
    public static void safePrintHeader(PrintStream output) {
    	System.out.printf("    /------------------------------------------\\\n");
        System.out.printf("    | %5s | %6s | %8s | %12s |\n", "Iter", "Time", "S*", "*");
        System.out.printf("    |-------|--------|----------|--------------|\n");
    }

    /**
     * Print the table footer
     *
     * @param output  the output stream
     */
    public static void safePrintFooter(PrintStream output) {
    	System.out.printf("    \\------------------------------------------/\n\n");
    }
    
    /**
     * Print the current solution status
     *
     * @param output		the output stream
     * @param nIters		the current iteration number
     * @param startTime		the starting time
     * @param bestCost		the best cost
     * @param special		some informative string
     */
    public static void safePrintStatus(PrintStream output, long nIters, long startTime, double cost, String special) {
    	
        if (output != null) {
        	output.printf("    | %5s | %6.2f | %8.6f | %12s |\n",
        			longToString(nIters),
        			(System.currentTimeMillis() - startTime) / 1000.0,
        			cost, special);
        }
    }

    /**
     * Print the text maintaining the table style
     *
     * @param output		the output stream
     * @param nIters		the current iteration number
     * @param startTime		the starting time
     * @param text			text to print inside the table
     */
    public static void safePrintText(PrintStream output, long nIters, long startTime, String text) {
        if (output != null) {
        	output.printf("    | %5s | %6.2f | %23s |\n",
        			longToString(nIters),
        			(System.currentTimeMillis() - startTime) / 1000.0,
        			text);
        }
    }
    
    /**
     * Create and return a file reader
     *
     * @param path	the file path
     * @return the file reader
     */
    public static BufferedReader getFileReader(String path) throws IOException {
    	BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
    	
    	return reader;
    }
    
    /**
     * Create and return a file writer
     *
     * @param path	the file path
     * @return the file writer
     */
    public static BufferedWriter getFileWriter(String path) throws IOException {
    	BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8);
    	
    	return writer;
    }

    /**
     * Append text to a file
     *
     * @param path	the file path
     * @param path	the text to append
     */
    public static void appendTextToFile(String filePath, String text) throws IOException, InterruptedException {
    	ByteBuffer buffer = ByteBuffer.wrap(text.getBytes());
    	
    	Path path = Paths.get(filePath);
    	if (!Files.exists(path)) Files.createFile(path);
    	FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        FileLock lock = null;
        do {
        	Thread.sleep(1000);
        	lock = fileChannel.tryLock();
        } while (lock == null);
        
        fileChannel.write(buffer);
        fileChannel.close();
    }
    
}