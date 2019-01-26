package project.hack36.hack362k19;

/**
 * Created by user on 1/26/19.
 */

public class RequestProcessStatus {

    private static boolean isProcessed;

    public static boolean isProcessed() {
        return isProcessed;
    }

    public static void setProcessed(boolean processed) {
        isProcessed = processed;
    }
}
