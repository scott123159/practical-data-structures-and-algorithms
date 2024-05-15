import edu.princeton.cs.algs4.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import com.google.gson.*;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

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
    private double[][] boxes;
    private double intersectionOverUnion;
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
        //return merged bounding boxes just as input in the format of
        //[up_left_x,up_left_y,width,height]
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes.length; j++) {
                if (calculateIntersectionOverUnion(boxes[i], boxes[j]) >= intersectionOverUnion)
                    uf.union(i, j);
            }
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
        final double[][] inputs = new double[][] {
                {0.012224696674745185, 0.07414885009335948, 0.8725301725898733, 0.6152125624926833},
                {0.012882554753731235, 0.18374197951131288, 0.8698524096958042, 0.8115385513927408},
                {0.019748434594135035, 0.471390010126292, 0.8718304712137692, 0.6319149952734502},
                {0.10930422516294433, 0.16787626159792102, 0.8531067645263916, 0.8606545197611064},
                {0.11909373354531881, 0.7274736348372605, 0.8617199601047276, 0.8454970188770002},
                {0.1371179313985583, 0.209621310169526, 0.7475368391410648, 0.7929518202691429},
                {0.1920332941858375, 0.4273578093022845, 0.7416181930319978, 0.8946734037517932},
                {0.23103923200898316, 0.3382881373669955, 0.6543663219683065, 0.8284470221508086},
                {0.28474849463468266, 0.9508479989386014, 0.7043836180569533, 0.7202402982811252},
                {0.31525088754278635, 0.6182065227845156, 0.6158633868711302, 0.6079342464243939},
        };
        ImageMerge sol = new ImageMerge(inputs, 0.44202325253758856);
        double[][] outputs = sol.mergeBox();
        ImageMerge.draw(inputs);
//        double[][] temp = sol.mergeBox();
//        ImageMerge.draw(new double[][] {
//                {0.012224696674745185, 0.07414885009335948, 0.8725301725898733, 0.6152125624926833},
//                {0.012882554753731235, 0.18374197951131288, 0.8698524096958042, 0.8115385513927408},
//                {0.019748434594135035, 0.471390010126292, 0.8718304712137692, 0.6319149952734502},
//                {0.10930422516294433, 0.16787626159792102, 0.8531067645263916, 0.8606545197611064},
//                {0.11909373354531881, 0.7274736348372605, 0.8617199601047276, 0.8454970188770002},
//                {0.1371179313985583, 0.209621310169526, 0.7475368391410648, 0.7929518202691429},
//                {0.1920332941858375, 0.4273578093022845, 0.7416181930319978, 0.8946734037517932},
//                {0.23103923200898316, 0.3382881373669955, 0.6543663219683065, 0.8284470221508086},
//                {0.28474849463468266, 0.9508479989386014, 0.7043836180569533, 0.7202402982811252},
//                {0.31525088754278635, 0.6182065227845156, 0.6158633868711302, 0.6079342464243939},
//                temp[0]
//        });
//        double maxX = .0, maxY = .0;
//        double max = .0;
//        for (int i = 0; i < x.length; i++) {
//            if (x[i][0] + x[i][2] > max) {
//                max = x[i][0] + x[i][2];
//                maxX = x[i][0];
//                maxY = x[i][2];
//            }
//        }
//        System.out.println(max + " " + maxX + " " + maxY);
    }
}