import java.util.*;
import edu.princeton.cs.algs4.MaxPQ;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import com.google.gson.*;

class OutputFormat{
    Integer[][] scores;
    List<int[]> answer;
}

class test_Exam{
    static boolean deepEquals(List<int[]> answer,List<int[]> answer2)
    {
        if(answer.size() != answer2.size())
            return false;
        for(int i = 0; i< answer.size(); ++i)
        {
            int[] a = answer.get(i);
            int[] b = answer2.get(i);
            if(!Arrays.equals(a, b))
            {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[] datas;
        int num_ac = 0;
        List<int[]> user_ans;
        OutputFormat data;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                user_ans = Exam.getPassedList(data.scores);
                System.out.print("Sample"+i+": ");

                if(deepEquals(user_ans, data.answer))
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("Data:      " + Arrays.deepToString(data.scores));
                    System.out.println("Test_ans:  " + Arrays.deepToString(data.answer.toArray()));
                    System.out.println("User_ans:  " + Arrays.deepToString(user_ans.toArray()));
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

class Exam {
    public static List<int[]> getPassedList(Integer[][] scores)
    {
        //input:
        //    scores: int[subject][id]
        //    eg. scores[0][0] -> subject: 0, ID: 0
        //        scores[1][5] -> subject: 1, ID: 5

        //return:
        //    return a List of {ID, totalScore}
        //    sorted in descending order of the total score
        int subjectCount = scores.length;
        int studentCount = scores[0].length;
        int[] passed = new int[studentCount];
        int quota = (int)Math.ceil(studentCount * .2);
        for (int i = 0; i < subjectCount; i++) {
            MaxPQ<int[]> maxPQ = new MaxPQ<>(
                    (o1, o2) -> o1[1] == o2[1] ?
                    Integer.compare(o2[0], o1[0]) :
                    Integer.compare(o1[1], o2[1])
            );
            for (int j = 0; j < studentCount; j++) {
                maxPQ.insert(new int[] {j, scores[i][j]});
            }
            for (int j = 0; j < quota; j++) {
                passed[maxPQ.delMax()[0]] += 1;
            }
        }
        MaxPQ<int[]> maxPQ = new MaxPQ<>(
                (o1, o2) -> o1[1] == o2[1] ?
                        Integer.compare(o2[0], o1[0]) :
                        Integer.compare(o1[1], o2[1])
        );
        for (int i = 0; i < studentCount; i++) {
            int sum = 0;
            if (passed[i] == subjectCount) {
                for (Integer[] score : scores) {
                    sum += score[i];
                }
                maxPQ.insert(new int[] {i, sum});
            }
        }
        int size = maxPQ.size();
        List<int[]> results = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            results.add(maxPQ.delMax());
        }
        return results;
    }
    public static void main(String[] args) {
        List<int[]> ans = getPassedList(new Integer[][]
                {
                        // ID:[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
                        {73, 62, 75, 43, 15, 74, 67, 24, 36, 47, 13, 73, 54, 5, 71, 24, 45, 39, 30, 15, 74, 73, 21, 18, 45, 67, 89, 36, 82, 69},
                        {84, 70, 69, 48, 18, 2, 68, 29, 42, 94, 18, 77, 40, 44, 73, 27, 90, 38, 27, 18, 32, 60, 11, 17, 54, 68, 87, 31, 77, 75},
                        {77, 63, 63, 46, 18, 69, 64, 6, 48, 26, 12, 77, 48, 80, 73, 24, 92, 43, 36, 24, 12, 71, 16, 18, 54, 63, 94, 35, 76, 71},
                }
        );
        for(int[] student : ans)
            System.out.print(Arrays.toString(student));
        // 11 students * 0.2 = 2.2 -> Top 3 students
        // Output -> [6, 182][2, 178][1, 172]

        System.out.println(); // For typesetting

        ans = getPassedList(new Integer[][]
                {
                        // ID:[0, 1, 2, 3, 4, 5]
                        {67,82,64,32,65,76},
                        {42,90,80,12,76,58},
                }
        );
        for(int[] student : ans)
            System.out.print(Arrays.toString(student));
        // 6 students * 0.2 = 1.2 -> Top 2 students
        // Output -> [1, 172]
    }
}