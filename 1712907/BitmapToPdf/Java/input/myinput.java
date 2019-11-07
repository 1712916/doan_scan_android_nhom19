package input;

import java.io.*;

public class myinput{
    private static DataInputStream input = new DataInputStream(System.in);
    int a=5;
    public static byte readByte() {
        byte b;
        try {
            String str = input.readLine();
            b = Byte.parseByte(str);
            return b;
        } catch (IOException ioex) {
            System.out.println("Loi ra/vao" + ioex);
            return Byte.MAX_VALUE;
        }
    }

    public static short readShort() {
        short s;
        try {
            String str = input.readLine();
            s = Short.parseShort(str);
            return s;
        } catch (IOException ioex) {
            System.out.println("Loi ra/vao" + ioex);
            return Short.MAX_VALUE;
        }
    }

    public static int readInt() {
        int i;
        try {
            String str = input.readLine();
            i = Integer.parseInt(str);
            return i;
        } catch (IOException ioex) {
            System.out.println("Loi vao/ra" + ioex);
            return Integer.MAX_VALUE;
        }
    }

    public static float readFloat() {
        float f;
        try {
            String str = input.readLine();
            f = Float.parseFloat(str);
            return f;
        } catch (IOException ioex) {
            System.out.println("Loi vao/ra" + ioex);
            return Float.MAX_VALUE;
        }
    }

    public static double readDouble() {
        double d;
        try {
            String str = input.readLine();
            d = Double.parseDouble(str);
            return d;
        } catch (IOException ioex) {
            System.out.println("Loi vao/ra" + ioex);
            return Double.MAX_VALUE;
        }
    }

    public static char readChar() {
        String str;
        try {
            str = input.readLine();
            return str.charAt(0);
        } catch (IOException ioex) {
            System.out.println("Loi vao ra" + ioex);
            return '$';
        }
    }

    public static String readString() {
        String str;
        try {
            str = input.readLine();
            return str;
        } catch (Exception ioex) {
            System.out.println("Loi vao/ra" + ioex);
            return null;
        }

    }
}