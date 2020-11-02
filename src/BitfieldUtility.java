public class BitfieldUtility {

    public static void setBit(byte[] _bitField, int index, boolean value) {
        if (value)
            _bitField[index >> 3] |= 1 << (index & 0x7);
        else
            _bitField[index >> 3] &= ~(1 << (index & 0x7));

        /*
         * Reference
         * https://stackoverflow.com/questions/47544596/efficient-way-to-manipulate-bits
         * -in-byte-array-representing-a-bitfield
         */
    }

    public static boolean getBit(byte[] _bitField, int index) {
        return (_bitField[index >> 3] & 1 << (index & 0x7)) != 0;
        /*
         * Reference
         * https://stackoverflow.com/questions/47544596/efficient-way-to-manipulate-bits
         * -in-byte-array-representing-a-bitfield
         */
    }

    public static void printBitfield(byte[] b) {
        for (int i = 0; i < b.length * 8; i++) {
            if (getBit(b, i)) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
        }
        System.out.println("");
    }
}