import java.nio.file.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Scanner;
import static java.nio.file.StandardOpenOption.*;

public class CreateFilesBasedOnState
{
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);

        // where the files will be saved
        Path inStateFile = Paths.get("C:\\java\\chapter13\\InStateCusts.txt");
        Path outStateFile = Paths.get("C:\\java\\chapter13\\OutOfStateCusts.txt");

        // record formatting rules
        final String ID_FORMAT = "000";       // 3-digit account number
        final String NAME_FORMAT = "          "; // 10 spaces for name
        final int NAME_LENGTH = NAME_FORMAT.length();
        final String HOME_STATE = "WI";       // in-state
        final String BALANCE_FORMAT = "0000.00"; // formatted balance
        String delimiter = ",";

        // build a sample dummy record to figure out record size
        String s = ID_FORMAT + delimiter + NAME_FORMAT + delimiter +
                HOME_STATE + delimiter + BALANCE_FORMAT +
                System.lineSeparator();

        final int REC_SIZE = s.length();   // very important for random access

        // these will be used during data entry
        FileChannel fcIn = null;
        FileChannel fcOut = null;

        String idString;
        int id;
        String name;
        String state;
        double balance;

        final String QUIT = "999";

        // create both empty files with 1,000 default records
        createEmptyFile(inStateFile, s);
        createEmptyFile(outStateFile, s);

        // begin user data-entry section
        try
        {
            // open both files for writing
            fcIn = (FileChannel) Files.newByteChannel(inStateFile, CREATE, WRITE);
            fcOut = (FileChannel) Files.newByteChannel(outStateFile, CREATE, WRITE);

            System.out.print("Enter customer account number >> ");
            idString = input.nextLine();

            // loop until the user enters 999
            while (!idString.equals(QUIT))
            {
                id = Integer.parseInt(idString);

                // get customer's name
                System.out.print("Enter name for customer >> ");
                name = input.nextLine();

                // ensure name is exactly NAME_LENGTH characters
                StringBuilder sb = new StringBuilder(name);
                sb.setLength(NAME_LENGTH);
                name = sb.toString();

                // get customer's state
                System.out.print("Enter state >> ");
                state = input.nextLine();

                // get balance
                System.out.print("Enter balance >> ");
                balance = input.nextDouble();
                input.nextLine(); // clear leftover enter key

                DecimalFormat df = new DecimalFormat(BALANCE_FORMAT);

                // build final record for writing
                s = idString + delimiter + name + delimiter + state + delimiter +
                        df.format(balance) + System.lineSeparator();

                // convert to bytes
                byte[] data = s.getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(data);

                // write record to the correct file
                if (state.equals(HOME_STATE))
                {
                    fcIn.position(id * REC_SIZE);
                    fcIn.write(buffer);
                }
                else
                {
                    fcOut.position(id * REC_SIZE);
                    fcOut.write(buffer);
                }

                // ask for another
                System.out.print("Enter next customer account number or 999 to quit >> ");
                idString = input.nextLine();
            }

            // close channels when done
            fcIn.close();
            fcOut.close();

        }
        catch (Exception e)
        {
            System.out.println("Error writing records: " + e);
        }
    }

    // creates a file with 1,000 default records
    public static void createEmptyFile(Path file, String s)
    {
        final int NUMRECS = 1000;

        try
        {
            OutputStream out = new BufferedOutputStream(
                    Files.newOutputStream(file, CREATE));

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

            for (int count = 0; count < NUMRECS; count++)
            {
                writer.write(s, 0, s.length());
            }

            writer.close();
        }
        catch (Exception e)
        {
            System.out.println("Error creating file: " + e);
        }
    }
}
