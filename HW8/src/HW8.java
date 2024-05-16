import edu.princeton.cs.algs4.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import com.google.gson.*;
import edu.princeton.cs.algs4.Queue;

class OutputFormat2{
    double[][] box;
    double iou;
    double[][] answer;
}

class test{
    private static boolean deepEquals(double[][] test_ans, double[][] user_ans)
    {
        if(test_ans.length != user_ans.length)
            return false;
        for(int i = 0; i < user_ans.length; ++i)
        {
            if(user_ans[i].length != test_ans[i].length)
                return false;
            for(int j = 0; j < user_ans[i].length; ++j)
            {
                if(Math.abs(test_ans[i][j]-user_ans[i][j]) > 0.00000000001)
                    return false;
            }
        }
        return true;
    }
    public static void draw(double[][] user, double[][] test)
    {
        StdDraw.setCanvasSize(960,540);
        for(double[] box : user)
        {
            StdDraw.setPenColor(StdDraw.BLACK);
            double half_width = (box[2]/2.0);
            double half_height = (box[3]/2.0);
            double center_x = box[0]+ half_width;
            double center_y = box[1] + half_height;
            //StdDraw use y = 0 at the bottom, 1-center_y to flip

            StdDraw.rectangle(center_x, 1-center_y, half_width,half_height);
        }
        for(double[] box : test)
        {
            StdDraw.setPenColor(StdDraw.BOOK_RED);
            double half_width = (box[2]/2.0);
            double half_height = (box[3]/2.0);
            double center_x = box[0]+ half_width;
            double center_y = box[1] + half_height;
            //StdDraw use y = 0 at the bottom, 1-center_y to flip

            StdDraw.rectangle(center_x, 1-center_y, half_width,half_height);
        }
    }
    public static void main(String[] args) throws InterruptedException
    {
        Gson gson = new Gson();
        OutputFormat2[] datas;
        OutputFormat2 data;
        int num_ac = 0;

        double[][] user_ans;
        ImageMerge sol;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat2[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                sol = new ImageMerge(data.box, data.iou);
                user_ans = sol.mergeBox();
                System.out.print("Sample"+i+": ");
                if(deepEquals(user_ans, data.answer))
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("Data:      " + "\n    iou: "+data.iou + "\n" +
                            "    box: "+Arrays.deepToString(data.box));
                    System.out.println("Test_ans:  " + Arrays.deepToString(data.answer));
                    System.out.println("User_ans:  " + Arrays.deepToString(user_ans));
                    System.out.println("");
                    draw(user_ans,data.answer);
                    Thread.sleep(5000);
                }
            }
            System.out.println("Score: "+num_ac+"/"+datas.length);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
class ImageMerge {
    private class Event {
        private double time;
        private String action;
        private Box box;
        public Event(double time, String action, Box box) {
            this.time = time;
            this.action = action;
            this.box = box;
        }
    }
    private class Box {
        private int id;
        private double[] box;
        public Box(int id, double[] box) {
            this.id = id;
            this.box = box;
        }
    }
    private double[][] boxes;
    private double intersectionOverUnion;
    private PriorityQueue<Event> eventQueue;
    private Map<Integer, Box> boxesMap;
    private UF uf;
    private double[] merge(double[] b1, double[] b2) {
        return new double[] {
                Math.min(b1[0], b2[0]),
                Math.min(b1[1], b2[1]),
                Math.max(b1[0] + b1[2], b2[0] + b2[2]) - Math.min(b1[0], b2[0]),
                Math.max(b1[1] + b1[3], b2[1] + b2[3]) - Math.min(b1[1], b2[1]),
        };
    }
    private double calculateIntersectionOverUnion(double[] box1, double[] box2) {
        double x1 = Math.max(box1[0], box2[0]);
        double y1 = Math.max(box1[1], box2[1]);
        double x2 = Math.min(box1[0] + box1[2], box2[0] + box2[2]);
        double y2 = Math.min(box1[1] + box1[3], box2[1] + box2[3]);
        double areaOfBox1 = box1[2] * box1[3];
        double areaOfBox2 = box2[2] * box2[3];
        if (x2 - x1 <= 0 || y2 - y1 <= 0) return 0;
        double intersectionalArea = (x2 - x1) * (y2 - y1);
        return intersectionalArea / (areaOfBox1 + areaOfBox2 - intersectionalArea);
    }
    public double[][] mergeBox()
    {
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.poll();
            if (event.action == "add") {
                for (Map.Entry<Integer, Box> entry : boxesMap.entrySet()) {
                    if (calculateIntersectionOverUnion(event.box.box, entry.getValue().box) >= intersectionOverUnion)
                        uf.union(event.box.id, entry.getKey());
                }
                boxesMap.put(event.box.id, event.box);
            }
            if (event.action == "remove") boxesMap.remove(event.box.id);
        }
        Map<Integer, double[]> map = new HashMap<>();
        for (int i = 0; i < boxes.length; i++) {
            int root = uf.find(i);
            boxes[root] = merge(boxes[i], boxes[root]);
            map.put(root, boxes[root]);
        }
        int i = 0;
        double[][] results = new double[uf.count()][4];
        for (int key : map.keySet()) results[i++] = map.get(key);
        Comparator<double[]> comparator = Comparator
                .comparingDouble((double[] box) -> box[0])
                .thenComparingDouble(box -> box[1])
                .thenComparingDouble(box -> box[2])
                .thenComparingDouble(box -> box[3]);
        Arrays.sort(results, comparator);
        return results;
    }
    public ImageMerge(double[][] bbs, double iou_thresh){
        //bbs(bounding boxes): [up_left_x,up_left_y,width,height]
        //iou_threshold:          [0.0,1.0]
        boxes = bbs;
        intersectionOverUnion = iou_thresh;
        uf = new UF(boxes.length);
        eventQueue = new PriorityQueue<>(Comparator.comparingDouble(event -> event.time));
        boxesMap = new HashMap<>();
        for (int i = 0; i < boxes.length; i++) {
            eventQueue.add(new Event(boxes[i][0], "add", new Box(i, boxes[i])));
            eventQueue.add(new Event(boxes[i][0] + boxes[i][2], "remove", new Box(i, boxes[i])));
        }
    }
    public static void draw(double[][] bbs)
    {
        // ** NO NEED TO MODIFY THIS FUNCTION, WE WON'T CALL THIS **
        // ** DEBUG ONLY, USE THIS FUNCTION TO DRAW THE BOX OUT**
        StdDraw.setCanvasSize(960,540);
        for(double[] box : bbs)
        {
            double half_width = (box[2]/2.0);
            double half_height = (box[3]/2.0);
            double center_x = box[0]+ half_width;
            double center_y = box[1] + half_height;
            //StdDraw use y = 0 at the bottom, 1-center_y to flip
            StdDraw.rectangle(center_x, 1-center_y, half_width,half_height);
        }
    }
    public static void main(String[] args) {
    }
}