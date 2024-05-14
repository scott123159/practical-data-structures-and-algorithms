import edu.princeton.cs.algs4.StdDraw;

class ImageMerge {
    public double[][] mergeBox()
    {
        //return merged bounding boxes just as input in the format of
        //[up_left_x,up_left_y,width,height]
        return new double[][] {};
    }
    public ImageMerge(double[][] bbs, double iou_thresh){
        //bbs(bounding boxes): [up_left_x,up_left_y,width,height]
        //iou_threshold:          [0.0,1.0]
    }
    private double calculateIntersectionOverUnion(double[] box1, double[] box2) {
        double x1 = Math.max(box1[0], box2[0]);
        double y1 = Math.max(box1[1], box2[1]);
        double x2 = Math.min(box1[0] + box1[2], box2[0] + box2[2]);
        double y2 = Math.min(box1[1] + box1[3], box2[1] + box2[3]);
        double areaOfBox1 = box1[2] * box1[3];
        double areaOfBox2 = box2[2] * box2[3];
        double intersectionalArea = (x2 - x1) * (y2 - y1);
        double IoU = intersectionalArea / (areaOfBox1 + areaOfBox2 - intersectionalArea);
        return IoU < 0 ? 0 : IoU;
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
        ImageMerge sol = new ImageMerge(
                new double[][]{
                        {0.02,0.01,0.1,0.05},{0.0,0.0,0.1,0.05},{0.04,0.02,0.1,0.05},{0.06,0.03,0.1,0.05},{0.08,0.04,0.1,0.05},
                        {0.24,0.01,0.1,0.05},{0.20,0.0,0.1,0.05},{0.28,0.02,0.1,0.05},{0.32,0.03,0.1,0.05},{0.36,0.04,0.1,0.05},
                },
                0.5
        );
        double[][] temp = sol.mergeBox();
        //ImageMerge.draw(temp);
    }
}