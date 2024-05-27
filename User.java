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
    private int accountId;
    private static int luckyDipWins;
    private static int lastAccountId = 0;

    public User(String fullname, int age) {
        this.fullname = fullname;
        this.age = age;
        this.balance = 0.0;
        this.tickets = new ArrayList<>();
        this.accountId = ++lastAccountId;
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

    public int getAccountId() {
        return accountId;
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
            System.out.println("Welcome to the Lottery System!");
            System.out.println("Choose from these options:");
            System.out.println("1: Create lottery tickets");
            System.out.println("2: Create Lucky-dip lottery ticket");
            System.out.println("3: View purchased tickets");
            System.out.println("4: Run lottery game (admin only)");
            System.out.println("5: Report results (admin only)");
            System.out.println("6: Check if individual tickets won");
            System.out.println("7: View game details (admin only)");
            System.out.println("8: Exit");
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
                        viewGameDetails();
                        break;
                    case 8:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice! Please select again with options 1 to 8.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid choice! Please select again with options 1 to 8.");
                scanner.next(); // Consume the invalid input
            }
        } while (option != 8);
    }
    

    //This method to follow user to enter:
    //1. How much they like to play (auto substract £2 as fee and put it into prize pool)
    //2. Number of tickets they like to buy
    //3. Using selectNumbers from LotteryTicket class to create 6 unqiue number , using HashSet
    //Once finish step 3 , system will auto access addTicket from User class 
    private static void createLotteryTickets(Scanner scanner) {
        System.out.print("Enter your full name: ");
        scanner.nextLine(); 
        String fullname = scanner.nextLine();
        System.out.print("Enter your age: ");  
        int age = scanner.nextInt();
    
        if (age < 18) {
            System.out.println("Only people 18 or over can register to play the lottery");
            System.out.println("Exiting......");
            System.exit(0); //forcing system to exit by 0 second
        } //What if the user enter something not Integer such as Double , Character , String , long , bit , flot?
    
    
        double fee = TICKET_PRICE;
        System.out.print("Enter the fee (£" + fee + "): £");
        double balance = scanner.nextDouble();
    
        User user = new User(fullname, age);
        user.addBalance(balance);
        
        if (user.getBalance() == fee) {
            System.out.println("Tickets purchased successfully!");
            System.out.println("Creating lottery ticket...");
            LotteryTicket ticket = new LotteryTicket();
            System.out.println("Enter 6 unique numbers (1-49):");
    
            
            Set<Integer> chosenNumbers = new HashSet<>();
            boolean validTicket = false;
    
            // Keep prompting the user until a valid ticket is entered
            while (!validTicket) {
                // Clear the chosenNumbers set for each new ticket
                chosenNumbers.clear();
                for (int i = 0; i < 6; i++) {
                    int number;
                    boolean validNumber = false;
                    do {
                        // Prompt the user for each number
                        number = scanner.nextInt();
                        // Check if the number is within the valid range
                        if (number < 1 || number > 49) {
                            System.out.println("Invalid number! Please enter a number between 1 and 49.");
                        } else if (chosenNumbers.contains(number)) {
                            // Check if the number is a duplicate number or not 
                            System.out.println("Duplicate number! Please enter a unique number.");
                        } else {
                            // If the number is valid, add it to the chosenNumbers set
                            chosenNumbers.add(number);
                            validNumber = true;
                        }
                    } while (!validNumber);
                }
                // If the ticket has 6 unique numbers, set validTicket to true to exit the loop
                if (chosenNumbers.size() == 6) {
                    validTicket = true;
                } 
            }
            
            // Once a valid ticket is created, add it to the user's list of tickets
            ticket.getNumbers().addAll(chosenNumbers);
            user.addTicket(ticket);
            // Add the user to the list of users
            users.add(user);
            // Add the ticket fee to the prize fund
            addToPrizeFund(fee);
            System.out.println("-------------------------\n");
            System.out.println("Your ticket number is " + ticket.getNumbers());
            System.out.println("Current prize pool has: £" + prizeFund);
            System.out.println("-------------------------\n");
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
        System.out.print("Enter the fee (£" + fee + "): £");
        double balance = scanner.nextDouble();

        if (balance < fee) {
            System.out.println("Insufficient balance to buy tickets!");
            return; // Exit the method if the balance is insufficient
        }
        
        // Move the user creation outside the loop to avoid only the latest user is retained in the users list which lead to no user found
        User user = new User(fullname, age);
        user.addBalance(balance);
        

        while (user.getBalance() == fee) {
            System.out.println("-------------------------\n");
            System.out.println("Tickets purchased successfully!");

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
            addToPrizeFund(fee);
            //System.out.println("Your balance after ticket purchase: £" + (user.getBalance() - fee));
            System.out.println("Current prize pool has: £" + prizeFund);
            System.out.println("-------------------------\n");
            System.out.println("\n");
            users.add(user); // Add the user to the list of users
            break;
        }  
    }
    
    
    
    

    // This method allow to add £2 from each ticket purchase to the prize fund
    public static void addToPrizeFund(double amount) {
        prizeFund += amount; // Add £2 to the prize fund
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

    //Has a problem
    private static double checkTicketWinnings(User user, List<Integer> ticketNumbers) {
        int correctNumbers = 0;
        for (int number : ticketNumbers) {
            if (winningNumbers.contains(number)) {
                correctNumbers++;
            }
        }
        double winnings = 0.0;
        switch (correctNumbers) {
            case 2:
                System.out.println("Congratulations! You've won a lucky dip for the next game!");
                break;
            case 3:
                System.out.println("Congratulations! You've won £2!");
                winnings = 2.0;
                break;
            case 4:
            case 5:
                System.out.println("Congratulations! You've won £4!");
                winnings = 4.0;
                break;
            case 6:
                System.out.println("Congratulations! You've won the jackpot!");
                winnings = jackpotWinnings(users.size());
                break;
            default:
                System.out.println("Sorry! You did not win this time.");
        }
        return winnings;
    }


    private static double jackpotWinnings(int numPlayers) {
        double jackpot = prizeFund / numPlayers;
        if (jackpot < 6.0) {
            jackpot = 6.0;
        }
        prizeFund -= jackpot;
        return jackpot;
    }

    private static void reportResults() {
        System.out.println("Winning Numbers: " + winningNumbers);
        double totalWinnings = 0.0;
        for (User user : users) {
            System.out.println("User: " + user.fullname);
            double userWinnings = 0.0;
            for (LotteryTicket ticket : user.getTickets()) {
                List<Integer> numbers = ticket.getNumbers();
                double ticketWinnings = checkTicketWinnings(user, numbers);
                userWinnings += ticketWinnings;
                if (ticketWinnings > 0) {
                    System.out.println("Ticket Numbers: " + numbers + " - Correct Numbers: " + (numbers.size() - (6 - numbers.size()))
                            + " - Winnings: £" + ticketWinnings);
                }
            }
            System.out.println("Total Winnings: £" + userWinnings);
            totalWinnings += userWinnings;
        }
        if (totalWinnings < 6.0 && prizeFund > 0) {
            rollOverPrizeFund();
        }
    }

    
    private static void rollOverPrizeFund() {
        System.out.println("No jackpot winners. Prize fund rolls over to the next game.");
    }

    private static void checkWinningTickets(Scanner scanner) {
        System.out.print("Enter your full name: ");
        scanner.nextLine();
        String fullname = scanner.nextLine();
        User currentUser = null;
        for (User user : users) {
            if (user.fullname.equals(fullname)) {
                currentUser = user;
                break;
            }
        }
        if (currentUser != null) {
            System.out.print("Enter the numbers of your ticket: ");
            List<Integer> ticketNumbers = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                int number = scanner.nextInt();
                ticketNumbers.add(number);
            }
            double winnings = checkTicketWinnings(currentUser, ticketNumbers);
            if (winnings > 0) {
                System.out.println("Congratulations! You've won £" + winnings + "!");
            } else {
                System.out.println("Sorry! You did not win this time.");
            }
        } else {
            System.out.println("User not found!");
        }
    }


    private static void viewGameDetails() {
        System.out.println("Prize Fund: £" + prizeFund);
        System.out.println("Total Amount Given to Charity: £" + (TICKET_PRICE * users.size() - prizeFund));
        System.out.println("-------------------------\n");
    }


    /* 
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
    */
}
