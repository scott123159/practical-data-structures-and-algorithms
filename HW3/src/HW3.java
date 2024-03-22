import java.util.Arrays; // Used to print the arrays
import java.util.Stack;

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
        for (int i = 0; i < n; i++) {
            members[i] = new member(levels[i], ranges[i], i);
        }
        int[] answer = new int[n * 2];

        int[] a = new int[n];
        Stack<member> stack = new Stack<>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && stack.peek().Level < members[i].Level) {
                stack.pop();
            }
            a[i] = stack.isEmpty() ? -1 : stack.peek().Index;
            stack.push(members[i]);
        }
        stack.clear();
        int[] b = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && stack.peek().Level < members[i].Level) {
                stack.pop();
            }
            b[i] = stack.isEmpty() ? -1 : stack.peek().Index;
            stack.push(members[i]);
        }
        for (int i = 0; i < n; i++) {
            int leftIndex, rightIndex, rangeI = members[i].Range;
            leftIndex = Math.max(Math.max(0, i - rangeI), (a[i] != -1) ? a[i] + 1 : 0);
            rightIndex = Math.min(n - 1, Math.min(i + rangeI, (b[i] != -1) ? b[i] - 1 : n - 1));
            answer[i * 2] = leftIndex;
            answer[i * 2 + 1] = rightIndex;
        }
        return answer;
    }
}

class HW3 {
    public static void main(String[] args) {
        Mafia sol = new Mafia();
        System.out.println(Arrays.toString(
                sol.result(new int[] {11, 13, 11, 7, 15},
                        new int[] { 1,  8,  1, 7,  2})));
        // Output: [0, 0, 0, 3, 2, 3, 3, 3, 2, 4]
        //      => [a0, b0, a1, b1, a2, b2, a3, b3, a4, b4]
    }
}