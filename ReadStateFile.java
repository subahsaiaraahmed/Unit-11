import java.nio.file.*;
import java.io.*;
import java.nio.file.attribute.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.util.Scanner;
import static java.nio.file.StandardOpenOption.*;

public class ReadStateFile
{
    public static void main(String[] args)
    {
        Scanner kb = new Scanner(System.in);

        System.out.print("Enter file name to use (InStateCusts.txt or OutOfStateCusts.txt) >> ");
        String fileName = "D:\\COSC\\1437\\Unit 11\\" + kb.nextLine();

        Path file = Paths.get(fileName);

        System.out.println("DEBUG → Reading from: " + file.toAbsolutePath());

        String sample = "000,          ,WI,0000.00" + System.lineSeparator();
        final int REC_SIZE = sample.length();

        byte[] data = sample.getBytes();
        final String EMPTY = "000";

        double totalBalance = 0;

        // File attributes
        try
        {
            BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
            System.out.println("\nFile info:");
            System.out.println("Created: " + attr.creationTime());
            System.out.println("Size:    " + attr.size());
        }
        catch (IOException e)
        {
            System.out.println("Could not read file attributes.");
        }

        // Sequential read
        try (BufferedReader reader = Files.newBufferedReader(file))
        {
            System.out.println("\nAll non-default records:\n");

            String line = reader.readLine();

            while (line != null)
            {
                String[] parts = line.split(",");

                if (!parts[0].equals(EMPTY))
                {
                    System.out.println("ID #" + parts[0] + " " +
                            parts[1].trim() + " " + parts[2] +
                            " $" + parts[3]);

                    totalBalance += Double.parseDouble(parts[3]);
                }

                line = reader.readLine();
            }

            System.out.println("Total of all balances: $" + totalBalance);
        }
        catch (Exception e)
        {
            System.out.println("Error reading file.");
        }

        // Random access
        try
        {
            FileChannel fc = FileChannel.open(file, READ);
            ByteBuffer buffer = ByteBuffer.wrap(data);

            System.out.print("\nEnter account number to find >> ");
            int acct = kb.nextInt();

            fc.position(acct * REC_SIZE);
            fc.read(buffer);

            String record = new String(data);

            System.out.println("Record: " + record);
        }
        catch (Exception e)
        {
            System.out.println("Error reading record.");
        }
    }
}
