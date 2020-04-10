package source;

import com.oocourse.TimableOutput;

public class SafeOutput {
    public static synchronized void println(String s) {
        TimableOutput.println(s);
    }
}
