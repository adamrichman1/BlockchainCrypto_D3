import java.util.Arrays;

public class LaboonCrypt {

    private static int verbose;

    public static void main(String[] args) {
        // Check parameters
        if (invalidParameters(args)) {
            handleError();
        }

        // Initialize variables
        String inputString = args[0];
        verbose = (args.length == 1) ? 0 : initializeVerbosity(args[1]);
        String laboonHash = laboonHash(inputString);

        // Create matrix
        String[][] matrix = createMatrix(laboonHash);

        // Modify matrix
        modifyMatrix(matrix, inputString);

        // Concat matrix
        String concat = concatMatrix(matrix);

        // Final LaboonHash
        String finalLaboonCrypt = laboonHash(concat);

        System.out.println("LaboonCrypt hash: " + finalLaboonCrypt);
    }

    private static int initializeVerbosity(String verbosity) {
        switch (verbosity) {
            case "-verbose":
                return 1;
            case "-veryverbose":
                return 2;
            default:
                return 3;
        }
    }

    private static boolean invalidParameters(String[] args) {
        return args.length < 1 || args.length > 2 || (args.length == 2 && !args[1].equals("-verbose") &&
                !args[1].equals("-veryverbose") && !args[1].equals("-ultraverbose"));
    }

    private static String concatMatrix(String[][] matrix) {
        StringBuilder concat = new StringBuilder();
        for (String[] row : matrix) {
            for (String val : row) {
                concat.append(val);
            }
        }
        return concat.toString();
    }

    private static void modifyMatrix(String[][] matrix, String inputString) {
        int rowIndex = 0, colIndex = 0;
        for (int i = 0; i < inputString.length(); i++) {
            // Modify rowIndex and colIndex
            rowIndex = (rowIndex + inputString.charAt(i)*11) % 12;
            colIndex = (colIndex + 7 * (inputString.charAt(i)+3)) % 12;

            // Modify matrix value
            String oldVal = matrix[rowIndex][colIndex];
            matrix[rowIndex][colIndex] = laboonHash(matrix[rowIndex][colIndex]);

            if (verbose >= 2) {
                System.out.println("Moving " + inputString.charAt(i)*11 + " down and " + (7 * (inputString.charAt(i)+3))
                        + " right - modifying [" + rowIndex + ", " + colIndex + "] from " + oldVal + " to "
                        + matrix[rowIndex][colIndex]);
            }
        }
        if (verbose >= 1) {
            System.out.println("Final array:");
            printMatrix(matrix);
        }
    }

    private static void printMatrix(String[][] matrix) {
        for (String[] row: matrix) {
            for (String val: row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }

    private static String laboonHash(String inputString) {
        // Create blocks
        String[] blocks = createBlocks(inputString);

        // Print padded string
        if (verbose == 3) {
            printPaddedString(blocks);
            printBlocks(blocks);
        }

        // Apply compression function
        String lhs = "1AB0";
        for (String block: blocks) {
            if (verbose == 3) System.out.print("Iterating with " + lhs + " / " + block + " = ");
            lhs = applyCompressionFunctionC(lhs, block);
            if (verbose == 3) System.out.println(lhs);
        }
        if (verbose == 3) System.out.println("Final result: " + lhs);
        return lhs;
    }

    private static String[][] createMatrix(String laboonHash) {
        String[][] matrix = new String[12][12];
        matrix[0][0] = laboonHash;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (i != 0 || j != 0) {
                    if (j == 0) {
                        matrix[i][j] = laboonHash(matrix[i-1][matrix[i-1].length-1]);
                    }
                    else {
                        matrix[i][j] = laboonHash(matrix[i][j - 1]);
                    }
                }
            }
        }
        laboonHash(matrix[matrix.length-1][matrix.length-1]);
        if (verbose >= 1) {
            System.out.println("Initial array:");
            printMatrix(matrix);
        }
        return matrix;
    }

    private static void handleError() {
        System.err.println(">>> ERROR: Invalid command line arguments specified.");
        System.err.println("Usage:\n" +
                "java LaboonCrypt *string* *verbosity_flag*\n" +
                "Verbosity flag can be omitted for hash output only\n" +
                "Other options: -verbose -veryverbose -ultraverbose\n");
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
