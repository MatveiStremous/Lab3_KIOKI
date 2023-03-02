import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SecondMain {
    public static void main(String[] args) {
        BigInteger[] keys = genKeys();
        System.out.println("Закрытый ключ: " + keys[0]);
        System.out.println("Открытый ключ: (" + keys[1] + " " + keys[2] + " " + keys[3] + ")");

        codeFile("src/main/resources/test.txt", "src/main/resources/test2.txt", keys[1], keys[2], keys[3]);
        deCodeFile("src/main/resources/test2.txt", "src/main/resources/test3.txt", keys[0], keys[1]);
    }

    private static Integer deCode(BigInteger a, BigInteger b, BigInteger x, BigInteger p) {
        return b.multiply(a.modPow(p.subtract(BigInteger.ONE).subtract(x), p)).remainder(p).intValue();
    }

    private static BigInteger[] genKeys() {
        BigInteger p = BigInteger.probablePrime(100, new Random());
        while (p.compareTo(BigInteger.valueOf(500)) < 0) {
            p = BigInteger.probablePrime(100, new Random());
        }
        Random rand = new Random();
        BigInteger g = BigInteger.TWO;
        BigInteger x = BigInteger.valueOf(rand.nextInt(p.subtract(BigInteger.valueOf(3)).remainder(BigInteger.valueOf(Integer.MAX_VALUE)).intValue()) + 2);
        BigInteger y = g.modPow(x, p);
        return new BigInteger[]{x, p, g, y};
    }

    public static BigInteger getPRoot(BigInteger p) {
        BigInteger q = p.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2));
        for (BigInteger a = BigInteger.valueOf(2); a.compareTo(p) < 0; a = a.add(BigInteger.ONE)) {
            if (a.modPow(q, p).equals(BigInteger.ONE)) {
                continue;
            }
            if (a.modPow(q.multiply(BigInteger.valueOf(2)), p).equals(BigInteger.ONE)) {
                continue;
            }
            return a;
        }
        return BigInteger.ZERO;
    }


    private static BigInteger[] code(Integer a, BigInteger p, BigInteger g, BigInteger y) {
        BigInteger k = BigInteger.valueOf(0);
        for (Integer i = 2; i < p.intValue() - 1; i += 1) {
            if (BigInteger.valueOf(i).gcd(p.subtract(BigInteger.ONE)).compareTo(BigInteger.ONE) == 0) {
                k = BigInteger.valueOf(i);
                break;
            }
        }
        BigInteger a1 = g.modPow(k, p);
        BigInteger b1 = y.modPow(k, p).multiply(BigInteger.valueOf(a)).remainder(p);
        return new BigInteger[]{a1, b1};
    }

    public static void codeFile(String fromFilePath, String toFilePath, BigInteger p, BigInteger g, BigInteger y) {
        try {
            byte[] array = Files.readAllBytes(Paths.get(fromFilePath));
            BigInteger[] array2 = new BigInteger[array.length * 2];
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < array.length; i += 1) {
                BigInteger[] coded = code((array[i] + 256) % 256, p, g, y);
                array2[2 * i] = coded[0];
                array2[2 * i + 1] = coded[1];
            }

            File file = new File(toFilePath);
            FileWriter writer = new FileWriter(toFilePath, false);
            for (int i = 0; i < array2.length; i += 2) {
                writer.write(String.valueOf(array2[i]));
                writer.write(" ");
                writer.write(String.valueOf(array2[i + 1]));
                writer.write("\n");
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deCodeFile(String fromFilePath, String toFilePath, BigInteger x, BigInteger p) {
        try {
            Scanner scanner = new Scanner(Paths.get(fromFilePath));
            String s = "";
            BigInteger a;
            BigInteger b;
            List<Integer> list = new ArrayList<>();
            while (scanner.hasNextLine()) {
                s = scanner.nextLine();
                a = new BigInteger(s.split(" ")[0]);
                b = new BigInteger(s.split(" ")[1]);
                list.add(deCode(a, b, x, p));
            }

            byte[] array2 = new byte[list.size()];

            for (int i = 0; i < list.size(); i += 1) {
                array2[i] = list.get(i).byteValue();
            }
            File file = new File(toFilePath);
            OutputStream outStream = new FileOutputStream(file);
            outStream.write(array2);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
