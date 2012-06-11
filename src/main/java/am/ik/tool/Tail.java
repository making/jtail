package am.ik.tool;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.RED;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fusesource.jansi.AnsiConsole;

public class Tail {
    private static final int MAX_LINE_LENGTH = 1000;
    private static final long SLEEP_TIME = 500; // 500 m sec
    private static int numOfLine = 10;
    private static boolean fOption = false;
    private static boolean rOption = false;
    private static String fileName = null;
    private static InputStream targetStream = System.in;
    private static Pattern pattern = null;

    public static void main(String[] args) throws Exception {
        AnsiConsole.systemInstall();
        for (String s : args) {
            if ("-help".equals(s)) {
                System.err.println("Usage> java " + Tail.class.getName()
                        + " [-f] [-<num>] [-r regex] [filename]");
                return;
            } else if ("-f".equals(s))
                fOption = true;
            else if ("-r".equals(s))
                rOption = true;
            else if (s.startsWith("-"))
                numOfLine = -Integer.parseInt(s);
            else {
                if (rOption) {
                    pattern = Pattern.compile(s);
                    rOption = false;
                } else {
                    fileName = s;
                }
            }
        }

        if (fileName != null) {
            File target = new File(fileName);
            long len = target.length();
            targetStream = new FileInputStream(target);
            len -= MAX_LINE_LENGTH * numOfLine;
            if (len > 0)
                targetStream.skip(len);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(
                targetStream));
        String line;
        String[] lines = new String[numOfLine];
        int ip = 0;
        while ((line = br.readLine()) != null) {
            lines[ip] = line;
            if (++ip >= numOfLine)
                ip = 0;
        }
        int i = ip;
        do {
            if (lines[i] != null)
                println(lines[i]);
            if (++i >= numOfLine)
                i = 0;
        } while (i != ip);

        if (fOption && fileName != null) {
            while (true) {
                line = br.readLine();
                if (line != null) {
                    println(line);
                } else {
                    Thread.sleep(SLEEP_TIME);
                }
            }
        }
        AnsiConsole.systemUninstall();
        br.close();
    }

    public static void println(String line) {
        if (pattern != null) {
            Matcher m = pattern.matcher(line);
            if (m.find()) {
                // return;
                System.out.print(line.substring(0, m.start()));
                System.out.print(ansi().fg(RED)
                        .a(line.substring(m.start(), m.end())).reset());
                System.out.println(line.substring(m.end()));
                return;
            }
        }
        System.out.println(line);
    }
}
