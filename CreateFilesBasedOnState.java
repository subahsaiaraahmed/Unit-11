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

        // *** FIXED PATH — writes in your real folder ***
        Path inStateFile = Paths.get("D:\\COSC\\1437\\Unit 11\\InStateCusts.txt");
        Path outStateFile = Paths.get("D:\\COSC\\1437\\Unit 11\\OutOfStateCusts.txt");

        System.out.println("In-state file = " + inStateFile.toAbsolutePath());
        System.out.println("Out-of-state file = " + outStateFile.toAbsolutePath());

        final String ID_FORMAT = "000";
        final String NAME_FORMAT = "          "; 
        final int NAME_LENGTH = NAME_FORMAT.length();
        final String HOME_STATE = "WI";
        final String BALANCE_FORMAT = "0000.00";
        String delimiter = ",";

        String s = ID_FORMAT + delimiter + NAME_FORMAT + delimiter +
                HOME_STATE + delimiter + BALANCE_FORMAT +
                System.lineSeparator();

        final int REC_SIZE = s.length();

        FileChannel fcIn = null;
        FileChannel fcOut = null;

        String idString;
        int id;
        String name;
        String state;
        double balance;

        final String QUIT = "999";

        // create empty files
        createEmptyFile(inStateFile, s);
        createEmptyFile(outStateFile, s);

        try
        {
            fcIn = (FileChannel) Files.newByteChannel(inStateFile, CREATE, WRITE);
            fcOut = (FileChannel) Files.newByteChannel(outStateFile, CREATE, WRITE);

            System.out.print("Enter customer account number >> ");
            idString = input.nextLine();

            while (!idString.equals(QUIT))
            {
                id = Integer.parseInt(idString);

                System.out.print("Enter name for customer >> ");
                name = input.nextLine();

                StringBuilder sb = new StringBuilder(name);
                sb.setLength(NAME_LENGTH);
                name = sb.toString();

                System.out.print("Enter state >> ");
                state = input.nextLine();

                System.out.print("Enter balance >> ");
                balance = input.nextDouble();
                input.nextLine();

                DecimalFormat df = new DecimalFormat(BALANCE_FORMAT);

                s = idString + delimiter + name + delimiter + state + delimiter +
                        df.format(balance) + System.lineSeparator();

                byte[] data = s.getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(data);

                // DEBUG — shows exactly where the record goes
                if (state.equals(HOME_STATE))
                {
                    System.out.println("Record = " + s);

                    fcIn.position(id * REC_SIZE);
                    fcIn.write(buffer);
                }
                else
                {
                    System.out.println("Record = " + s);

                    fcOut.position(id * REC_SIZE);
                    fcOut.write(buffer);
                }

                System.out.print("Enter next customer account number or 999 to quit >> ");
                idString = input.nextLine();
            }

            fcIn.close();
            fcOut.close();
        }
        catch (Exception e)
        {
            System.out.println("Error writing records: " + e);
        }
    }

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
