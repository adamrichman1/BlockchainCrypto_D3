public class LaboonHash {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println(">>> ERROR: Invalid command line arguments specified.");
            System.exit(1);
        }
        String inputString = args[0];
        System.out.println(inputString);
        String[] blocks = createBlocks(inputString, inputString.length());

        String lhs = "1AB0";
        for (String block: blocks) {
            lhs = applyCompressionFunctionC(lhs, block);
        }

        System.out.println(lhs);
    }

    private static String[] createBlocks(String inputString, int length) {
        String[] blocks = new String[inputString.length()/8+1];

        int i = 0;
        while (true) {
            if (inputString.length() < 8) {
                int n = 8-(length%8);
                String concat = Long.toHexString(length % Math.round((Math.pow(16, n))));
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
            sb.append(Integer.toHexString(r % 16));
        }
        return sb.toString();
    }
}