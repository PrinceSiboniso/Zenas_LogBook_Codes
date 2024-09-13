
 import java.util.Scanner;

 public class FactorialCalculator
 {
     public static void main(String[] args)
     {
         while (true) 
         {
             Scanner scanner = new Scanner(System.in);
             System.out.print("Enter a number to calculate its factorial: ");
             int num = scanner.nextInt();
             
             if (num>0) 
             {
                 String factString = "";
                 for(int i=num;i>=1;i--)
                 {
                     if(i>1) factString += i+"x";
                     else factString += i;
                 }
                 System.out.println("\n\n");
                 System.out.println("Factorial of "+num+": "+num+" = "+factString+" = "+calculateFactorial(num));
                 break;
             }	
             else 
             {
                 System.out.println("Invalid input. Please enter a positive integer.\n-");
                 continue;
             }
         }
         
         
     }
 
     public static int calculateFactorial(int n)
     {	
         if(n==0)
         {
             return 1;
         }
         else
         {
             return n*calculateFactorial(n-1);
         }
     }
 }