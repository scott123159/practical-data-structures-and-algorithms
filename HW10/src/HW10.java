import java.util.*;

class RoadToCastle {
    int totalCost;
    List<int[]> path;
    static class Cell {
        int x, y, cost;
        Cell(int x, int y, int cost) {
            this.x = x;
            this.y = y;
            this.cost = cost;
        }
    }

    public List<int[]> shortest_path(){
        //return int[] in the format of {Y,X}
//        List<int[]> list = new ArrayList<>();
//        list.add(new int[] {1, 1});
//        list.add(new int[] {1, 2});
//        list.add(new int[] {1, 3});
        Collections.reverse(path);
        return path;
    }
    public int shortest_path_len(){
        return totalCost;
    }
    public RoadToCastle(int[][] map, int[] init_pos, int[] target_pos){
        //map: [Y][X]
        //init_pos: 0:Y, 1:X
        //target_pos: 0:Y, 1:X
        int n = map.length;
        int m = map[0].length;
        totalCost = 0;
        path = new ArrayList<>();
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        PriorityQueue<Cell> pq = new PriorityQueue<>(Comparator.comparingInt(cell -> cell.cost));
        pq.add(new Cell(init_pos[0], init_pos[1], 0));
        int[][] costs = new int[n][m];
        int[][][] prev = new int[n][m][2];  // 用於記錄前驅節點
        for (int[] row : costs) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        costs[init_pos[0]][init_pos[1]] = 0;
        prev[init_pos[0]][init_pos[1]] = null;
        while (!pq.isEmpty()) {
            Cell current = pq.poll();
            int x = current.x;
            int y = current.y;
            int cost = current.cost;

            if (x == target_pos[0] && y == target_pos[1]) {
                totalCost = cost;
                break;
            }

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if (nx >= 0 && ny >= 0 && nx < n && ny < m && map[nx][ny] != 0) {
                    int newCost = cost + (map[nx][ny] == 3 ? 5 : 1);

                    if (newCost < costs[nx][ny]) {
                        costs[nx][ny] = newCost;
                        pq.add(new Cell(nx, ny, newCost));
                        prev[nx][ny] = new int[]{x, y};  // 記錄前驅節點
                    }
                }
            }
        }
        for (int[] pos = target_pos; pos != null; pos = prev[pos[0]][pos[1]])
            path.add(pos);
    }
    public static void main(String[] args) {
        RoadToCastle sol = new RoadToCastle(new int[][]{
                {0,0,0,0,0},
                {0,2,3,2,0},  //map[1][2]=3
                {0,2,0,2,0},
                {0,2,0,2,0},
                {0,2,2,2,0},
                {0,0,0,0,0}
        },
                new int[]{1,1},
                new int[]{1,3}
                );
        System.out.println(sol.shortest_path_len());
        List<int[]> path = sol.shortest_path();
        for(int[] coor : path)
            System.out.println("x: "+Integer.toString(coor[0]) + " y: "+Integer.toString(coor[1]));

        //ans: best_path:{{1, 1}, {1, 2}, {1, 3}}
        //Path 1 (the best): [1, 1] [1, 2] [1, 3] -> 0+5+1 = 6, cost to reach init_pos is zero!
        //Path 2 (example of other paths): [1, 1] [2, 1] [3, 1] [4, 1] [4, 2] [4, 3] [3, 3] [2, 3] [1, 3] -> 8
    }
}