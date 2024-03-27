import java.util.*;

import edu.princeton.cs.algs4.Point2D;

class ObservationStationAnalysis {
    private Stack<Point2D> hull;
    private List<Point2D> stations;
    public ObservationStationAnalysis(ArrayList<Point2D> stations) {
        // you can do something in Constructor or not
        this.stations = stations;
        GrahamScan(this.stations);
    }

    private void GrahamScan(List<Point2D> points) {
        if (points == null) throw new IllegalArgumentException("argument is null");
        if (points.isEmpty()) throw new IllegalArgumentException("array is of length 0");
        hull = new Stack<>();
        // defensive copy
        int n = points.size();

        // preprocess so that a[0] has lowest y-coordinate; break ties by x-coordinate
        // a[0] is an extreme point of the convex hull
        // (alternatively, could do easily in linear time)
        points.sort(null);
        points.subList(1, points.size()).sort(points.get(0).polarOrder());

        // sort by polar angle with respect to base point a[0],
        // breaking ties by distance to a[0]

        hull.push(points.get(0));       // a[0] is first extreme point

        // find index k1 of first point not equal to a[0]
        int k1;
        for (k1 = 1; k1 < n; k1++)
            if (!points.get(0).equals(points.get(k1))) break;
        if (k1 == n) return;        // all points equal

        // find index k2 of first point not collinear with a[0] and a[k1]
        int k2;
        for (k2 = k1+1; k2 < n; k2++)
            if (Point2D.ccw(points.get(0), points.get(k1), points.get(k2)) != 0) break;
        hull.push(points.get(k2 - 1));    // a[k2-1] is second extreme point

        // Graham scan; note that a[n-1] is extreme point different from a[0]
        for (int i = k2; i < n; i++) {
            Point2D top = hull.pop();
            while (Point2D.ccw(hull.peek(), top, points.get(i
            )) <= 0) {
                top = hull.pop();
            }
            hull.push(top);
            hull.push(points.get(i));
        }

    }

    public Point2D[] findFarthestStations() {
        double maxDist = .0;
        int maxI = 0;
        int maxJ = 0;
        for (int i = 0; i < hull.size(); i++) {
            for (int j = i + 1; j < hull.size(); j++) {
                if (hull.get(i).distanceTo(hull.get(j)) > maxDist) {
                    maxDist = hull.get(i).distanceTo(hull.get(j));
                    maxI = i;
                    maxJ = j;
                }
            }
        }
        // find the farthest two stations
        Point2D[] farthest = new Point2D[2];
        farthest[0] = hull.get(maxI);
        farthest[1] = hull.get(maxJ);
        Arrays.sort(farthest, Point2D.R_ORDER);
        return farthest;
    }

    public double coverageArea() {
        double area = 0.0;
        // calculate the area surrounded by the existing stations
        Point2D referencePoint = hull.get(0); // 選擇第一個點作為參考點

        // 循環遍歷點列表，計算並加總三角形的面積
        for (int i = 0; i < hull.size(); i++) {
            Point2D point1 = hull.get(i);
            Point2D point2 = hull.get((i + 1) % hull.size());
            area += point1.x() * point2.y() - point2.x() * point1.y();
        }
        return Math.abs(area / 2.0);
    }

    public void addNewStation(Point2D newStation) {
        stations.add(newStation);
        GrahamScan(stations);
    }

    public static void main(String[] args) throws Exception {

        ArrayList<Point2D> stationCoordinates = new ArrayList<>();
        stationCoordinates.add(new Point2D(0, 0));
        stationCoordinates.add(new Point2D(2, 0));
        stationCoordinates.add(new Point2D(3, 2));
        stationCoordinates.add(new Point2D(2, 6));
        stationCoordinates.add(new Point2D(0, 4));
        stationCoordinates.add(new Point2D(1, 1));
        stationCoordinates.add(new Point2D(2, 2));
        stationCoordinates.add(new Point2D(2, 2));

        ObservationStationAnalysis Analysis = new ObservationStationAnalysis(stationCoordinates);
        System.out.println("Farthest Station A: "+Analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: "+Analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: "+Analysis.coverageArea());

        System.out.println("Add Station (10, 3): ");
        Analysis.addNewStation(new Point2D(10, 3));

        System.out.println("Farthest Station A: "+Analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: "+Analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: "+Analysis.coverageArea());
    }
}