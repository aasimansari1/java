import java.util.Scanner;

public class BankingApp {

   static Scanner scanner = new Scanner(System.in);
   static String accountNumber;
   static String accountHolderName;
   static double balance;

   public static void main(String[] args) {
       login();
       displayMenu();
   }

   public static void login() {
       System.out.println("Enter your account number: ");
       accountNumber = scanner.nextLine();
       System.out.println("Enter your account holder name: ");
       accountHolderName = scanner.nextLine();
       System.out.println("Enter your current balance: ");
       balance = scanner.nextDouble();
       System.out.println("Welcome " + accountHolderName + "!");
   }

   public static void displayMenu() {
       System.out.println("Menu: \n1. Check Balance \n2. Deposit \n3. Withdraw \n4. Exit");
       int choice = scanner.nextInt();
       performAction(choice);
   }

   public static void performAction(int choice) {
       switch (choice) {
           case 1:
               checkBalance();
               break;
           case 2:
               deposit();
               break;
           case 3:
               withdraw();
               break;
           case 4:
               exit();
               break;
           default:
               System.out.println("Invalid choice. Please choose again.");
               displayMenu();
               break;
       }
   }

   public static void checkBalance() {
       System.out.println("Your current balance is: " + balance);
       displayMenu();
   }

   public static void deposit() {
       System.out.println("Enter the amount to deposit: ");
       double amount = scanner.nextDouble();
       balance += amount;
       System.out.println("Deposit successful. Your new balance is: " + balance);
       displayMenu();
   }

   public static void withdraw() {
       System.out.println("Enter the amount to withdraw: ");
       double amount = scanner.nextDouble();
       if (amount > balance) {
           System.out.println("Insufficient balance. Please try again.");
       } else {
           balance -= amount;
           System.out.println("Withdrawal successful. Your new balance is: " + balance);
       }
       displayMenu();
   }

   public static void exit() {
       System.out.println("Thank you for using our services!");
       scanner.close();
   }
}