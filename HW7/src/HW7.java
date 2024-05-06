import java.util.ArrayList;
import java.util.List;

class IntervalST<Key extends Comparable<Key>, Value>{
    private Node root;

    private class Node {
        private Key lo, hi, max;
        private Value val;
        private int size;
        private Node left, right;

        public Node(Key lo, Key hi, Value val) {
            // initializes the node if required.
            this.lo = lo;
            this.hi = hi;
            this.val = val;
        }
    }

    public IntervalST()
    {
        // initializes the tree if required.
    }

    public void inorder() {
        inorder(root);
    }

    private void inorder(Node x) {
        if (x == null) return;
        inorder(x.left);
        System.out.printf("[%d, %d] %s\n", x.lo, x.hi, x.val);
        inorder(x.right);
    }

    public void put(Key lo, Key hi, Value val)
    {
        // insert a new interval here.
        // lo    : the starting point of the interval. lo included
        // hi    : the ending point of the interval. hi included
        // val   : the value stored in the tree.
        root = put(root, lo, hi, val);
    }

    private Node put(Node x, Key lo, Key hi, Value val) {
        if (x == null) return new Node(lo, hi, val);

        int cmp = lo.compareTo(x.lo);

        if (cmp < 0)
            x.left = put(x.left, lo, hi, val);
        else if (cmp > 0)
            x.right = put(x.right, lo, hi, val);
        else {
            cmp = hi.compareTo(x.hi);
            if (cmp < 0) x.left = put(x.left, lo, hi, val);
            if (cmp > 0) x.right = put(x.right, lo, hi, val);
            if (cmp == 0) x.val = val;
        }

        return x;
    }

    private Node min(Node x) {
        if (x.left == null) {
            return x;
        }
        return min(x.left);
    }

    public void deleteMin()
    { root = deleteMin(root); }

    private Node deleteMin(Node x)
    {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        return x;
    }

    public void delete(Key lo, Key hi)
    {
        // remove an interval of [lo,hi]
        // do nothing if interval not found.
        root = delete(root, lo, hi);
    }

    private Node delete(Node x, Key lo, Key hi) {
        if (x == null) return null;

        int cmp = lo.compareTo(x.lo);

        if (cmp < 0)
            x.left = delete(x.left, lo, hi);
        else if (cmp > 0)
            x.right = delete(x.right, lo, hi);
        else {
            cmp = hi.compareTo(x.hi);
            if (cmp < 0) x.left = delete(x.left, lo, hi);
            else if (cmp > 0) x.right = delete(x.right, lo, hi);
            else {
                if (x.left == null) return x.right;
                if (x.right == null) return x.left;

                Node t = x;
                x = min(t.right);
                x.right = deleteMin(t.right);
                x.left = t.left;
            }
        }

        return x;
    }

    public List<Value> intersects(Key lo, Key hi)
    {
        // return the values of all intervals within the tree which intersect with [lo,hi].
        List<Value> answers = new ArrayList<>();
        inorder(root, lo, hi, answers);
        return answers;
    }

    private void inorder(Node x, Key lo, Key hi, List<Value> answers) {
        if (x == null) return;
        inorder(x.left, lo, hi, answers);
        int xLo = (Integer)x.lo;
        int xHi = (Integer)x.hi;
        int Lo = (Integer)lo;
        int Hi = (Integer)hi;
        if ((Lo <= xLo && xLo <= Hi) ||
            (Lo <= xHi && xHi <= Hi) ||
            (xLo <= Lo && Hi <= xHi)) answers.add(x.val);
        inorder(x.right, lo, hi, answers);
    }

    public static void main(String[]args)
    {
        // Example
        IntervalST<Integer, String> IST = new IntervalST<>();
        IST.put(2,5,"badminton");
        IST.put(1,5,"PDSA HW7");
        IST.put(3,5,"Lunch");
        IST.put(3,6,"Workout");
        IST.put(3,7,"Do nothing");
        IST.delete(2,5); // delete "badminton"
        System.out.println(IST.intersects(1,2));

        IST.put(8,8,"Dinner");
        System.out.println(IST.intersects(6,10));

        IST.put(3,7,"Do something"); // If an interval is identical to an existing node, then the value of that node is updated accordingly
        System.out.println(IST.intersects(7,7));

        IST.delete(3,7); // delete "Do something"
        System.out.println(IST.intersects(7,7));
    }
}