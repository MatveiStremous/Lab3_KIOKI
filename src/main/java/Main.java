import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Integer[] keys = genKeys();
        System.out.println("Закрытый ключ: " + keys[0]);
        System.out.println("Открытый ключ: (" + keys[1] + " " + keys[2] + " " + keys[3] + ")");

        codeFile("src/main/resources/test.txt", "src/main/resources/test2.txt", keys[1], keys[2], keys[3]);
        deCodeFile("src/main/resources/test2.txt", "src/main/resources/test3.txt", keys[0], keys[1]);
    }

    private static Integer deCode(Integer a, Integer b, Integer x, Integer p) {
        return b * step(BigInteger.valueOf(a),BigInteger.valueOf(p-1-x), BigInteger.valueOf(p)).intValue() % p;
    }

    private static Integer getRandomP() {
        List<Integer> list = new ArrayList<>();
        for (int i = 260; i < 10000; i += 1) {
            if (isPrime(i)) {
                list.add(i);
            }
        }
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }
        double s = Math.sqrt(number);
        for (int i = 2; i <= s; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    private static Integer[] genKeys() {
        Integer p = getRandomP();
        Random rand = new Random();
        Integer g = getPRoot(p);
        Integer x = rand.nextInt(p - 3) + 2;
        Integer y = step(BigInteger.valueOf(g),BigInteger.valueOf(x), BigInteger.valueOf(p)).intValue();
        return new Integer[]{x, p, g, y};
    }

    private static Integer[] code(Integer a, Integer p, Integer g, Integer y) {
        Integer k = 0;
        for (int i = 2; i < p - 1; i += 1) {
            if (nod(p - 1, i) == 1) {
                k = i;
                break;
            }
        }

        Integer a1 =  step(BigInteger.valueOf(g),BigInteger.valueOf(k), BigInteger.valueOf(p)).intValue();
        Integer b1 =  step(BigInteger.valueOf(y),BigInteger.valueOf(k), BigInteger.valueOf(p)).intValue() * a % p;
        return new Integer[]{a1, b1};
    }

    private static int nod(int a, int b) {
        int c;
        while (b > 0) {
            c = a % b;
            a = b;
            b = c;
        }
        return a;
    }

//    private static Integer step(Integer g, Integer x, Integer p) {
//        Integer ans = 1;
//        for (int i = 0; i < x; i++) {
//            ans *= g;
//            ans %= p;
//        }
//        return Math.toIntExact(ans);
//    }

    private static BigInteger step(BigInteger x, BigInteger n, BigInteger mod) {
        if (Objects.equals(n, BigInteger.ONE)) {
            return x;
        }
        if (n.remainder(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return step(x, n.divide(BigInteger.TWO), mod).pow(2).remainder(mod);
        }
        return (step(x, n.subtract(BigInteger.ONE), mod).multiply(x).remainder(mod));
    }

//    private static Integer step(Integer x, Integer y, Integer n) {
//        if (y == 0) return 1;
//        int z = step(x, y / 2, n);
//        if (y % 2 == 0)
//            return (z * z) % n;
//        else
//            return (x * z * z) % n;
//    }

    public static int ef(int n) {
        int result = n;
        for (int i = 2; i * i <= n; ++i) {
            if (n % i == 0) {
                while (n % i == 0) n /= i;
                result -= result / i;
            }
        }
        if (n > 1) result -= result / n;
        return result;
    }

    public static Integer getPRoot(Integer p) {
        for (Integer i = 0; i < p; i++)
            if (isPRoot(p, i))
                return i;
        return 0;
    }

    public static boolean isPRoot(long p, long a) {
        if (a == 0 || a == 1)
            return false;
        long last = 1;

        Set<Long> set = new HashSet<>();
        for (long i = 0; i < p - 1; i++) {
            last = (last * a) % p;
            if (set.contains(last)) // Если повтор
                return false;
            set.add(last);
        }
        return true;
    }

    public static void codeFile(String fromFilePath, String toFilePath, Integer p, Integer g, Integer y) {
        try {
            byte[] array = Files.readAllBytes(Paths.get(fromFilePath));
            Integer[] array2 = new Integer[array.length * 2];
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < array.length; i += 1) {
                Integer[] coded = code((array[i] + 256) % 256, p, g, y);
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

    public static void deCodeFile(String fromFilePath, String toFilePath, Integer x, Integer p) {
        try {
            Scanner scanner = new Scanner(Paths.get(fromFilePath));
            String s = "";
            Integer a = 0;
            Integer b = 0;
            List<Integer> list = new ArrayList<>();
            while (scanner.hasNextLine()) {
                s = scanner.nextLine();
                a = Integer.parseInt(s.split(" ")[0]);
                b = Integer.parseInt(s.split(" ")[1]);
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
