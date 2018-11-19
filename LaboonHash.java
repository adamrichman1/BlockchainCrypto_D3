
public class LaboonHash {

    private static boolean verbose;

    public static void main(String[] args) {
        // Check parameters
        if (invalidParameters(args)) {
            handleError();
        }

        // Initialize variables
        String inputString = args[0];
        verbose = args.length > 1;
        String laboonHash = laboonHash(inputString);
        System.out.println("LaboonHash hash = " + laboonHash);
    }

    private static boolean invalidParameters(String[] args) {
        return args.length < 1 || args.length > 2 || (args.length == 2 && !args[1].equals("-verbose"));
    }

    private static String laboonHash(String inputString) {
        // Create blocks
        String[] blocks = createBlocks(inputString);

        // Print padded string
        if (verbose) {
            printPaddedString(blocks);
            printBlocks(blocks);
        }

        // Apply compression function
        String lhs = "1AB0";
        for (String block: blocks) {
            if (verbose) System.out.print("Iterating with " + lhs + " / " + block + " = ");
            lhs = applyCompressionFunctionC(lhs, block);
            if (verbose) System.out.println(lhs);
        }
        if (verbose) System.out.println("Final result: " + lhs);
        return lhs;
    }

    private static void handleError() {
        System.err.println(">>> ERROR: Invalid command line arguments specified.");
        System.err.println("Usage:\n" +
                "java LaboonHash *string* *verbosity_flag*\n" +
                "Verbosity flag can be omitted for hash output only\n" +
                "Other options: -verbose\n");
        System.exit(1);
    }

    private static void printBlocks(String[] blocks) {
        System.out.println("Blocks:");
        for (String block: blocks) {
            System.out.println(block);
        }
    }

    private static void printPaddedString(String[] blocks) {
        System.out.print("Padded string: ");
        for (String s: blocks) {
            System.out.print(s);
        }
        System.out.println();
    }

    private static String[] createBlocks(String inputString) {
        int inputLength = inputString.length();
        int arraySize = (inputString.length() % 8 == 0) ? inputString.length() / 8 : inputString.length() / 8 + 1;
        String[] blocks = new String[arraySize];

        int i = 0;
        while (inputString.length() > 0) {
            if (inputString.length() < 8) {
                int n = 8 - (inputLength % 8);
                String concat = Long.toHexString(inputLength % Math.round((Math.pow(16, n)))).toUpperCase();
                StringBuilder padding = new StringBuilder();
                for (int j = 0; j < n-concat.length(); j++) {
                    padding.append("0");
                }
                blocks[i] = inputString.substring(0, inputString.length()) + padding.toString() + concat;
                break;
            }
            else {
                blocks[i++] = inputString.substring(0, 8);
                inputString = inputString.substring(8, inputString.length());
            }
        }
        return blocks;
    }

    private static String applyCompressionFunctionC(String lhs, String rhs) {
        int[] result = new int[4];

        // Add lhs & rhs
        for (int i = 0; i < lhs.length(); i++) {
            result[i] = lhs.charAt(i) + rhs.charAt(3-i);
        }

        // XOR result & rhs
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i] ^ rhs.charAt(7-i);
        }

        // XOR result & result
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i] ^ result[3-i];
        }

        // Return result in Hexadecimal
        StringBuilder sb = new StringBuilder();
        for (int r : result) {
            sb.append(Integer.toHexString(r % 16).toUpperCase());
        }
        return sb.toString();
    }
}