package entities.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
public class ZipfGenerator {
    private Random rnd;
    //private Random rnd = new Random(0);
    private int size;
    private double skew;
    private double bottom;

    public ZipfGenerator(int size, double skew) {
        this.rnd = new Random(System.currentTimeMillis());
        this.size = size;
        this.skew = skew;
//calculate the generalized harmonic number of order 'size' of 'skew'
//http://en.wikipedia.org/wiki/Harmonic_number
        for (int i = 1; i <= size; i++) {
            this.bottom += (1.0d / Math.pow(i, this.skew));
        }
    }

    /**
     * Method that returns a rank id between 0 and this.size (exclusive).
     * The frequency of returned rank id follows the Zipf distribution represented by this class.
     *
     * @return a rank id between 0 and this.size.
     * //     * @throws lptracegen.DistributionGenerator.DistributionException
     */
    public int next() {
        int rank = -1;
        double frequency = 0.0d;
        double dice = 0.0d;
        while (dice >= frequency) {
            rank = this.rnd.nextInt(this.size);
            frequency = getProbability(rank + 1);//(0 is not allowed for probability computation)
// dice = this.rnd.nextDouble() * getProbability(this.size/2);
            dice = this.rnd.nextDouble();
        }
        return rank;
    }

    /**
     * Method that computes the probability (0.0 .. 1.0) that a given rank occurs.
     * The rank cannot be zero.
     *
     * @param rank
     * @return probability that the given rank occurs (over 1)
     * //     * @throws lptracegen.DistributionGenerator.DistributionException
     */
    public double getProbability(int rank) {
        if (rank == 0) {
            throw new RuntimeException("getProbability - rank must be > 0");
        }
        return (1.0d / Math.pow(rank, this.skew)) / this.bottom;
    }

    /**
     * Method that returns a Zipf distribution
     * result[i] = probability that rank i occurs
     *
     * @return the zipf distribution
     * //     * @throws lptracegen.DistributionGenerator.DistributionException
     */
    public double[] getDistribution() {
        double[] result = new double[this.size];
        for (int i = 1; i <= this.size; i++) { //i==0 is not allowed to compute probability
            result[i - 1] = getProbability(i);
        }
        return result;
    }

    /**
     * Method that computes an array of length == this.size
     * with the occurrences for every rank i (following the Zipf)
     * result[i] = #occurrences of rank i
     * //     * @param size
     *
     * @return result[i] = #occurrences of rank i
     * //     * @throws lptracegen.DistributionGenerator.DistributionException
     */
    public int[] getRankArray(int totalEvents) {
        int[] result = new int[this.size];
        Arrays.fill(result, 1);
        for (int i = 0; i < totalEvents; i++) {
            int rank = next();
            result[rank] += 1;
        }
        return result;
    }

    public int[] getRankArray2(int totalEvents) {
        int[] result = new int[this.size];
        Arrays.fill(result, 1);
        int events = totalEvents - this.size;
        for (int i = 0; i < result.length; i++) {
            int tmp = (int) Math.round(getProbability(i + 1) * events);
            result[i] += tmp;
        }
        return result;
    }

    public int[] getRankArray3(int totalEvents) {
        int[] result = new int[this.size];
        Arrays.fill(result, 1);
        for (int i = 0; i < totalEvents; i++) {
            int rank = next();
            result[rank] += 1;
        }
        return result;
    }

    /* test drive */
    public static int[][] returnFileList(double skew, int instances, int files, int servers) {
        ZipfGenerator generator = new ZipfGenerator(files, skew);
        int c = 0;
        boolean bool = isEffic(skew,instances,files,servers);
        if (!bool) throw new RuntimeException("Inefficient coefs... change the numbers");
        int[][] serverContents;
        do {
            serverContents = new int[servers][instances];
            for (int i = 0; i < serverContents.length; i++) {
                boolean[] checkList = new boolean[files];
                for (int j = 0; j < serverContents[i].length; j++) {
                    int rank;
                    do {
                        rank = generator.next();
                    } while (checkList[rank]);
                    checkList[rank] = true;
                    serverContents[i][j] = rank;
                }
            }
            c++;
        }while (!isValidOccurences(serverContents, files)) ;

        return serverContents;
    }

    public static void main(String[] args) {

    }

    private static boolean isEffic(double skew, int instances, int files, int servers) {
        ZipfGenerator generator = new ZipfGenerator(files, skew);
        return generator.getProbability(files)*instances*servers>=1.0;

    }

    private static boolean isValidOccurences(int[][] serverContents, int files) {
        boolean[] checkList = new boolean[files];
        for (int i = 0; i < serverContents.length; i++) {
            for (int j = 0; j < serverContents[i].length; j++) {
                checkList[serverContents[i][j]] = true;
            }

        }
        for (int j = 0; j < checkList.length; j++) {
                if (!checkList[j]) return false;
        }
        return true;
    }
}
