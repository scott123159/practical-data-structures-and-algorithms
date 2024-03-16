import edu.princeton.cs.algs4.UF;
import java.util.HashMap;
import java.util.Map;

class ImageSegmentation {
    final private int[] color;
    final private UF uf;
    public ImageSegmentation(int N, int[][] inputImage) {
        // Initialize an N-by-N image
        uf = new UF(N * N);
        color = new int[N * N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                color[i * N + j] = inputImage[i][j];
            }
        }
    }

    public int countDistinctSegments() {
        // Count the number of distinct segments in the image.
        final int n = (int)Math.sqrt(color.length);
        int numberOfZero = 0;
        final int[][] directions = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1},
        };
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int p = i * n + j;
                if (color[p] == 0) numberOfZero++;
                for (int[] dir : directions) {
                    int ni = i + dir[0];
                    int nj = j + dir[1];
                    if (ni >= 0 && ni < n && nj >= 0 && nj < n) {
                        int q = ni * n + nj;
                        if (color[p] == color[q] && color[p] != 0) {
                            uf.union(p, q);
                        }
                    }
                }
            }
        }
        return uf.count() - numberOfZero;
    }

    public int[] findLargestSegment() {
        // Find the largest connected segment and return an array
        // containing the number of pixels and the color of the segment.
        int n = (int) Math.sqrt(color.length);
        Map<Integer, Integer> map = new HashMap<>();

        // Calculate sizes of segments
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int p = i * n + j;
                if (color[p] != 0) {
                    int root = uf.find(p);
                    map.put(root, map.getOrDefault(root, 0) + 1);
                }
            }
        }
        // Find largest segment
        int largestSize = Integer.MIN_VALUE;
        int largestColor = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() > largestSize || (entry.getValue() == largestSize && color[entry.getKey()] < largestColor)) {
                largestSize = entry.getValue();
                largestColor = color[entry.getKey()];
            }
        }
        return new int[]{largestSize, largestColor};
    }
}

class HW1 {
    public static void main(String[] args) {
        // Example 1:
        int[][] inputImage1 = {
                {1, 0, 1},
                {0, 1, 0},
                {0, 0, 0},
        };

        System.out.println("Example 1:");

        ImageSegmentation s = new ImageSegmentation(3, inputImage1);
        System.out.println("Number of Distinct Segments: " + s.countDistinctSegments());

        int[] largest = s.findLargestSegment();
        System.out.println("Size of the Largest Segment: " + largest[0]);
        System.out.println("Color of the Largest Segment: " + largest[1]);


        // Example 2:
        int[][] inputImage2 = {
                {0, 0, 0, 3, 0},
                {0, 2, 3, 3, 0},
                {1, 2, 2, 0, 0},
                {1, 2, 2, 1, 1},
                {0, 0, 1, 1, 1}
        };

        System.out.println("\nExample 2:");

        s = new ImageSegmentation(5, inputImage2);
        System.out.println("Number of Distinct Segments: " + s.countDistinctSegments());

        largest = s.findLargestSegment();
        System.out.println("Size of the Largest Segment: " + largest[0]);
        System.out.println("Color of the Largest Segment: " + largest[1]);
    }
}