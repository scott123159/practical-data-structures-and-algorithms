import java.util.Arrays; // Used to print the arrays

class member{
    int Level;
    int Range;
    int Index;

    member(int _level,int _range, int i){
        Level=_level;
        Range=_range;
        Index=i;
    }
}

class Mafia {
    public int[] result(int[] levels, int[] ranges) {
        int n = levels.length;
        member[] members = new member[n];
        int[] result = new int[n * 2];

        for (int i = 0; i < n; i++) {
            members[i] = new member(levels[i], ranges[i], i);
        }

        for (int i = 0; i < n; i++) {
            int currIndex = members[i].Index;
            int minIndex = currIndex, maxIndex = currIndex;
            int leftIndex = currIndex - 1;
            int rightIndex = currIndex + 1;
            while (leftIndex >= 0 && Math.abs(currIndex - leftIndex) <= members[i].Range) {
                if (members[leftIndex].Level >= members[currIndex].Level) break;
                minIndex = leftIndex;
                leftIndex--;
            }
            while (rightIndex < n && Math.abs(currIndex - rightIndex) <= members[i].Range) {
                if (members[rightIndex].Level >= members[currIndex].Level) break;
                maxIndex = rightIndex;
                rightIndex++;
            }
            result[2 * i] = minIndex;
            result[2 * i + 1] = maxIndex;
        }
        // Given the traits of each member and output
        // the leftmost and rightmost index of member
        // can be attacked by each member.
        return result;
        // complete the code by returning an int[]
        // flatten the results since we only need an 1-dimentional array.
    }

    public static void main(String[] args) {
        Mafia sol = new Mafia();
        System.out.println(Arrays.toString(
                sol.result(new int[] {11, 13, 11, 7, 15},
                        new int[] { 1,  8,  1, 7,  2})));
        // Output: [0, 0, 0, 3, 2, 3, 3, 3, 2, 4]
        //      => [a0, b0, a1, b1, a2, b2, a3, b3, a4, b4]
    }
}

class HW3 {
    public static void main(String[] args) {
    }
}
