import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


class User {
    String fullname;
    private int age;
    private double balance;
    private List<LotteryTicket> tickets;

    public User(String fullname, int age) {
        this.fullname = fullname;
        this.age = age;
        this.balance = 0.0;
        this.tickets = new ArrayList<>();
    }

    public void deductBalance(double amount) {
        this.balance -= amount;
    }

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public double getBalance() {
        return balance;
    }

    public void addTicket(LotteryTicket ticket) {
        tickets.add(ticket);
    }

    public List<LotteryTicket> getTickets() {
        return tickets;
    }
}

class LotteryTicket {
    private List<Integer> numbers;

    public LotteryTicket() {
        numbers = new ArrayList<>();
    }

    //This method is to allow user to select 6 unique number
    //Therefore , using HashSet is a good idea since HashSet not allow duplicate nubmer 
    public void selectNumbers(Scanner scanner) {
        System.out.println("Enter 6 unique numbers (1-49):");
        Set<Integer> chosenNumbers = new HashSet<>();
        while (chosenNumbers.size() < 6) {
            int number = scanner.nextInt();
            if (number < 1 || number > 49) {
                System.out.println("Invalid number! Please enter a number between 1 and 49.");
            } else if (!chosenNumbers.add(number)) {
                System.out.println("Number already chosen! Please enter a unique number.");
            }
        }
        numbers.addAll(chosenNumbers);
        System.out.println("Ticket created with numbers: " + numbers);
    }

    public List<Integer> getNumbers() {
        return numbers;
    }
}

class LotteryGame {
    private static final double TICKET_PRICE = 2.0; // Initialize the prize fee
    private static final int MIN_CORRECT_NUMBERS_FOR_PRIZE = 2;
    private static double prizeFund = 0.0; // Initialize the prize fund
    private static List<User> users = new ArrayList<>();
    private static List<Integer> winningNumbers;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        do {
            System.out.println("Weclome to Lottery System!");
            System.out.println("\nChoose from these choices");
            System.out.println("-------------------------\n");
            System.out.println("1: Create lottery tickets");
            System.out.println("2: Create Lucky-dip lottery ticket");
            System.out.println("3: View purchased tickets");
            System.out.println("4: Run lottery game (admin only)");
            System.out.println("5: Report results (admin only)");
            System.out.println("6: Check if individual tickets won");
            System.out.println("7: Exit");
            System.out.println("-------------------------\n");
            System.out.println();
            
            try {
                System.out.print("Please enter the service by number: ");
                option = scanner.nextInt();
                switch (option) {
                    case 1:
                        createLotteryTickets(scanner);
                        break;
                    case 2:
                        createLuckyDipTicket(scanner);
                        break;
                    case 3:
                        viewPurchasedTickets();
                        break;
                    case 4:
                        runLotteryGame(scanner);
                        break;
                    case 5:
                        reportResults();
                        break;
                    case 6:
                        checkWinningTickets(scanner);
                        break;
                case 7:
                    System.out.println("Exiting...");
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid choice! Please select again wtih option 1 to 7!");
                scanner.next(); // Consume the invalid input
                System.out.println("-------------------------\n");
                System.out.println("\n");
            }
        } while (option != 7);
    }

    //This method to follow user to enter:
    //1. How much they like to play (auto substract £2 as fee and put it into prize pool)
    //2. Number of tickets they like to buy
    //3. Using selectNumbers from LotteryTicket class to create 6 unqiue number , using HashSet
    //Once finish step 3 , system will auto access addTicket from User class 
    private static void createLotteryTickets(Scanner scanner) {
        System.out.print("Enter your full name:");
        scanner.nextLine(); 
        String fullname = scanner.nextLine();
        System.out.print("Enter your age: ");  
        int age = scanner.nextInt();

        if (age < 18) {
            System.out.println("Only people 18 or over can register to play the lottery");
            System.out.println("Exiting......");
            System.exit(0); //forcing system to exit by 0 second
        } //What if the user enter something not Integer such as Double , Character , String , long , bit , flot?

        System.out.print("Enter the balance you want to play with:");
        double balance = scanner.nextDouble();
        
        User user = new User(fullname, age);
        user.addBalance(balance);
        
        double fee = TICKET_PRICE;
            if (user.getBalance() >= fee) {
                user.deductBalance(fee);
                System.out.println("Tickets purchased successfully!");
                System.out.println("Remaining balance: £" + user.getBalance());
                System.out.println("Ticket fee deducted: £" + fee);
                System.out.println("Your balance after ticket purchase: £" + (user.getBalance() - fee));
                System.out.println("Enter the number of tickets you want to buy:");
                int numTickets = scanner.nextInt();
                for (int i = 0; i < numTickets; i++) {
                    LotteryTicket ticket = new LotteryTicket();
                    ticket.selectNumbers(scanner);
                    user.addTicket(ticket);
                }
                users.add(user);
            } else {
                System.out.println("Insufficient balance to buy tickets!");
        }
    }
    
    

    private static void createLuckyDipTicket(Scanner scanner) {
        System.out.print("Enter your full name: ");
        scanner.nextLine(); 
        String fullname = scanner.nextLine();
        System.out.print("Enter your age: ");
        int age = scanner.nextInt();
        
        if (age < 18) {
            System.out.println("Only people 18 or over can register to play the lottery");
            System.out.println("Exiting......");
            System.exit(0); // Exit the program if user is under 18
        }
    
        double fee = TICKET_PRICE;
    
        // Move the user creation outside the loop
        User user = new User(fullname, age);
    
        while (true) {
            System.out.print("Enter the balance you want to play with: ");
            double balance = scanner.nextDouble();
    
            if (balance < 3) {
                System.out.println("Minimum balance must be £3 or more. Please try again.");
            } else if (balance < fee) {
                System.out.println("Insufficient balance to buy tickets!");
                break; // Exit loop if balance is insufficient
            } else {
                user.addBalance(balance);
    
                if (user.getBalance() >= fee) {
                    user.deductBalance(fee);
                    addToPrizeFund();
                    System.out.println("-------------------------\n");
                    System.out.println("Tickets purchased successfully!");
                    System.out.println("Remaining balance: £" + user.getBalance());
                    System.out.println("Ticket fee deducted: £" + fee);
                    System.out.println("Creating lucky-dip ticket...");
                    LotteryTicket ticket = new LotteryTicket();
                    Random random = new Random();
    
                    // Generate 6 unique random numbers
                    Set<Integer> chosenNumbers = new HashSet<>();
                    while (chosenNumbers.size() < 6) {
                        int randomNumber = random.nextInt(49) + 1;
                        chosenNumbers.add(randomNumber);
                    }
    
                    ticket.getNumbers().addAll(chosenNumbers);
                    user.addTicket(ticket); // Add the ticket to the user's list of tickets
                    System.out.println("Lucky-dip ticket created with numbers: " + ticket.getNumbers());
                    System.out.println("Your balance after ticket purchase: £" + (user.getBalance() - fee));
                    System.out.println("Current prize pool has: £" + prizeFund);
                    System.out.println("-------------------------\n");
                    System.out.println("\n");
                    users.add(user); // Add the user to the list of users
                    break;
                }
            }
        }
    }
    
    

    // This method allow to add £2 from each ticket purchase to the prize fund
    public static void addToPrizeFund() {
        prizeFund += 2.0; // Add £2 to the prize fund
    }
    
    private static void viewPurchasedTickets() {
        if (users.isEmpty()) {
            System.out.println("\nNo users found.");
            System.out.println("\n");
            return;
        }
    
        boolean foundTickets = false;
        for (User user : users) {
            System.out.println("------------");
            System.out.println("\nUser: " + user.fullname);
            List<LotteryTicket> userTickets = user.getTickets();
            if (!userTickets.isEmpty()) {
                foundTickets = true;
                for (LotteryTicket ticket : userTickets) {
                    System.out.println("Ticket Numbers: " + ticket.getNumbers());
                }
            }
            System.out.println("------------\n");
        }
    
        if (!foundTickets) {
            System.out.println("No tickets purchased yet.");
        }
    }
    
    

    private static void runLotteryGame(Scanner scanner) {
        System.out.println("Enter winning numbers (6 unique numbers between 1 and 49):");
        winningNumbers = new ArrayList<>();
        Set<Integer> chosenNumbers = new HashSet<>();
        while (chosenNumbers.size() < 6) {
            int number = scanner.nextInt();
            if (number < 1 || number > 49) {
                System.out.println("Invalid number! Please enter a number between 1 and 49.");
            } else if (!chosenNumbers.add(number)) {
                System.out.println("Number already chosen! Please enter a unique number.");
            }
        }
        winningNumbers.addAll(chosenNumbers);
        System.out.println("Winning numbers set as: " + winningNumbers);
    }

    private static void reportResults() {
        System.out.println("Winning Numbers: " + winningNumbers);
        // Check each user's tickets for winning numbers
        for (User user : users) {
            System.out.println("User name : " + user.fullname);
            for (LotteryTicket ticket : user.getTickets()) {
                List<Integer> numbers = ticket.getNumbers();
                int correctNumbers = 0;
                for (int number : numbers) {
                    if (winningNumbers.contains(number)) {
                        correctNumbers++;
                    }
                }
                if (correctNumbers >= MIN_CORRECT_NUMBERS_FOR_PRIZE) {
                    System.out.println("Ticket Numbers: " + numbers + " - Correct Numbers: " + correctNumbers);
                }
            }
        }
    }

    private static void checkWinningTickets(Scanner scanner) {
        System.out.println("Enter your full name:");
        scanner.nextLine(); // Consume newline
        String fullname = scanner.nextLine();
        User currentUser = null;
        for (User user : users) {
            if (user.fullname.equals(fullname)) {
                currentUser = user;
                break;
            }
        }
        if (currentUser != null) {
            System.out.println("Enter the numbers of your ticket:");
            List<Integer> ticketNumbers = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                int number = scanner.nextInt();
                ticketNumbers.add(number);
            }
            int correctNumbers = 0;
            for (int number : ticketNumbers) {
                if (winningNumbers.contains(number)) {
                    correctNumbers++;
                }
            }
            if (correctNumbers >= MIN_CORRECT_NUMBERS_FOR_PRIZE) {
                System.out.println("Congratulations! You have won with " + correctNumbers + " correct numbers!");
            } else {
                System.out.println("Sorry! You did not win this time.");
            }
        } else {
            System.out.println("User not found!");
        }
    }
}
