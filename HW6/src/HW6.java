import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

import com.google.gson.*;

class OutputFormat{
    int[] answer;
    String func;
    int[] args;
}

class test{
    static boolean run_and_check(OutputFormat[] data, RoadStatus roadStat)
    {
        for(OutputFormat cmd : data)
        {
            if(cmd.func.equals("addCar"))
            {
                roadStat.addCar(cmd.args[0], cmd.args[1], cmd.args[2]);
            }
            else if(cmd.func.equals("roadStatus"))
            {
                int[] arr = roadStat.roadStatus(cmd.args[0]);
                if(!Arrays.equals(arr,cmd.answer))
                    return false;
            }
        }
        return true;
    }
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[][] datas;
        OutputFormat[] data;
        int num_ac = 0;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[][].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];

                System.out.print("Sample"+i+": ");
                if(run_and_check(data, new RoadStatus()))
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("");
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

class RoadStatus {
    private final int[] carsOnRoad;
    private int currentGreenRoad; // 沒有任何綠燈時為-1
    private int currentTime;
    private int timer;

    public RoadStatus() {
        carsOnRoad = new int[3];
        currentGreenRoad = -1;
        currentTime = 0;
        timer = 0;
    }

    public void addCar(int time, int roadId, int numCars) {
        updateTime(time);
        carsOnRoad[roadId] += numCars;
        if (currentGreenRoad == -1) decideGreenLight();
    }

    public int[] roadStatus(int time) {
        updateTime(time);
        return carsOnRoad;
    }

    private void updateTime(int targetTime) {
        while (currentTime < targetTime) {
            processTimeUnit();
            currentTime++;
        }
    }

    private void processTimeUnit() {
        if (currentGreenRoad != -1) {
            if ((timer == currentTime || carsOnRoad[currentGreenRoad] == 0)) {
                decideGreenLight();
            }
            if (currentGreenRoad != -1 ) carsOnRoad[currentGreenRoad]--;
        }
    }

    private void decideGreenLight() {
        int max = carsOnRoad[0];
        currentGreenRoad = 0;

        for (int i = 1; i < carsOnRoad.length; i++) {
            if (carsOnRoad[i] > max) {
                max = carsOnRoad[i];
                currentGreenRoad = i;
            }
        }
        timer = max + currentTime;
        if (max == 0) currentGreenRoad = -1;
    }

    public static void main(String[] args) {
        // Test cases here
        System.out.println("Example 1: ");
        RoadStatus sol1 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        sol1.addCar(0, 0, 2);
        System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
        sol1.addCar(0, 1, 3);
        System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol1.roadStatus(1)));
        sol1.addCar(2, 0, 4);
        for (int i = 2; i < 12; ++i)
            System.out.println(i + ": " + Arrays.toString(sol1.roadStatus(i)));
        //______________________________________________________________________
        // Example 2
        RoadStatus sol2 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 2: ");
        sol2.addCar(0, 0, 2);
        System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
        sol2.addCar(0, 0, 1);
        System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol2.roadStatus(1)));
        sol2.addCar(2, 1, 2);
        for (int i = 2; i < 7; ++i)
            System.out.println(i + ": " + Arrays.toString(sol2.roadStatus(i)));
        //______________________________________________________________________
        // Example 3
        RoadStatus sol3 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 3: ");
        sol3.addCar(0, 0, 1);
        System.out.println("0: " + Arrays.toString(sol3.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol3.roadStatus(1)));
        System.out.println("2: " + Arrays.toString(sol3.roadStatus(2)));
        sol3.addCar(3, 1, 1);
        System.out.println("3: " + Arrays.toString(sol3.roadStatus(3)));
        sol3.addCar(3, 1, 1);
        System.out.println("3: " + Arrays.toString(sol3.roadStatus(3)));
        sol3.addCar(4, 0, 2);
        for (int i = 4; i < 10; i++) {
            System.out.println(i + ": " + Arrays.toString(sol3.roadStatus(i)));
        }
        RoadStatus sol4 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 4: ");
        sol4.addCar(6, 1, 5);
        System.out.println("6: " + Arrays.toString(sol4.roadStatus(6)));
        sol4.addCar(9, 0, 8);
        sol4.addCar(10, 0, 5);
        sol4.addCar(10, 1, 2);
        System.out.println("13: " + Arrays.toString(sol4.roadStatus(13)));
        sol4.addCar(15, 2, 9);
        sol4.addCar(19, 0, 9);
        sol4.addCar(23, 1, 3);
        System.out.println("25: " + Arrays.toString(sol4.roadStatus(25)));
        sol4.addCar(26, 0, 9);
        sol4.addCar(26, 2, 7);
        sol4.addCar(26, 2, 6);
        System.out.println("25: " + Arrays.toString(sol4.roadStatus(27)));
        sol4.addCar(27, 2, 2);
        sol4.addCar(29, 0, 6);
        sol4.addCar(29, 2, 8);
        sol4.addCar(32, 1, 7);
        sol4.addCar(35, 2, 7);
        sol4.addCar(37, 2, 8);
        System.out.println("25: " + Arrays.toString(sol4.roadStatus(41)));
    }
}