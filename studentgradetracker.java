import java.util.ArrayList;
import java.util.Scanner;

public class studentgradetracker {

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        ArrayList<Integer> grades = new ArrayList<>();
        
        System.out.println("Enter the number of students:");
        int n = sc.nextInt();
        
        // Input grades
        for (int i = 0; i < n; i++) 
        {
            System.out.println("Enter grade for student " + (i + 1) + ":");
            int grade = sc.nextInt();
            grades.add(grade);
        }
        
        // Compute average
        double sum = 0;
        for (int grade : grades) {
            sum += grade;
        }
        double avg = sum / n;
        
        // Find highest and lowest grades
        int highest = grades.get(0);
        int lowest = grades.get(0);
        for (int grade : grades) {
            if (grade > highest) {
                highest = grade;
            }
            if (grade < lowest) {
                lowest = grade;
            }
        }
        
        // Output results
        System.out.println("Average grade: " + avg);
        System.out.println("Highest grade: " + highest);
        System.out.println("Lowest grade: " + lowest);
        
        sc.close();
    }
}