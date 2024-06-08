import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

class User {
    String fullname;
    private int age;
    private double balance;
    private List<LotteryTicket> tickets;
    private int accountId;
    private static int luckyDipWins;
    private static int lastAccountId = 0;
    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    


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

    public void setBalance(double amount) {
        this.balance += amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setTicket(LotteryTicket ticket) {
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

    // This method is to allow user to select 6 unique number
    // Therefore , using HashSet is a good idea since HashSet not allow duplicate nubmer 
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
    private static final DateTimeFormatter DATE_FORMATTER = User.DATE_FORMATTER;
    private static double prizeFund = 0.0; // Initialize the prize fund
    private static List<User> users = new ArrayList<>();
    private static List<Integer> winningNumbers;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        // User menu 
        do {
            System.out.println("Welcome to the Lottery System!");
            System.out.println("Choose from these options:");
            System.out.println("1: Register");
            System.out.println("2: Create lottery tickets");
            System.out.println("3: Create Lucky-dip lottery ticket");
            System.out.println("4: View purchased tickets");
            System.out.println("5: Run lottery game (admin only)");
            System.out.println("6: Report results (admin only)");
            System.out.println("7: Check if individual tickets won");
            System.out.println("8: View game details (admin only)");
            System.out.println("9: Exit");
            System.out.println();

            try {
                System.out.print("Please enter the service by number: ");
                option = scanner.nextInt();
                switch (option) {
                    case 1:
                    // Implement register logic
                        registerUser(scanner);
                        break;
                    case 2:
                    // Implement create ticket logic
                        createLotteryTickets(scanner);
                        break;
                    // Implement create random ticket logic
                    case 3:
                        createLuckyDipTicket(scanner);
                        break;
                    // Implement view all user logic
                    case 4:
                        viewPurchasedTickets();
                        break;
                    // Implement game logic
                    case 5:
                        runLotteryGame(scanner);
                        break;
                    // Implement admin view logic
                    case 6:
                        reportResults();
                        break;
                    // Implement view base on id logic
                    case 7:
                        checkWinningTickets(scanner);
                        break;
                    // Implement admin prize fund view logic
                    case 8:
                        viewGameDetails();
                        break;
                    // Implement exit logic
                    case 9:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice! Please select again with options 1 to 9.");
                }
            // Avoid any incorrect input or null input exception
            } catch (InputMismatchException | NullPointerException e) {
                System.out.println("Invalid choice! Please select again with options 1 to 9 or Make sure the winning numbers date!");
                scanner.next();
            } 
        } while (option != 9);
    }


    // A method to allow user to register 
    private static void registerUser(Scanner scanner) {
        System.out.print("Enter your full name: ");
        String name = scanner.next();
        LocalDate birthDate = askForBirthDate(scanner , DATE_FORMATTER);
        // Using Data API to check the age , if the age less then 18 , exit the app with 0 sec
        if (isUnderage(birthDate)) {
            System.out.println("You must be at least 18 years old to register.");
            System.out.println("Exiting...");
            System.exit(0);
        }
        int accountId = users.size() + 1;
        User user = new User(name, calculateAge(birthDate));
        users.add(user);
        System.out.println("Registration successful. Your account ID is: " + accountId);
    }

    private static int calculateAge(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        return Period.between(birthDate, today).getYears();
    }

    // A method to setup the format of how user enter their DOB info
    private static LocalDate askForBirthDate(Scanner scanner , DateTimeFormatter formatter) {
        System.out.print("Enter your birthdate (yyyy-MM-dd): ");
        String birthDateInput = scanner.next();
        return LocalDate.parse(birthDateInput, formatter);
    }
    
    // A method to calclate user age
    private static boolean isUnderage(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        LocalDate eighteenYearsAgo = today.minusYears(18);
        return birthDate.isAfter(eighteenYearsAgo);
    }


    // A method to find user by id , and it should store it by using getAccountId()
    private static User findUserById(int accountId) {
        for (User user : users) {
            if (user.getAccountId() == accountId) {
                return user;
            }
        }
        return null;
    }
    

    //This method to follow user to enter:
    //1. How much they like to play (auto substract £2 as fee and put it into prize pool)
    //2. Number of tickets they like to buy
    //3. Using selectNumbers from LotteryTicket class to create 6 unqiue number , using HashSet
    //Once finish step 3 , system will auto access setTicket from User class 
    private static void createLotteryTickets(Scanner scanner) {
        System.out.print("Enter your account ID: ");
        int accountId = scanner.nextInt();
        User user = findUserById(accountId);
        if (user == null) {
            System.out.println("User not found!");
            return;
        } else { // if user not null
            double fee = TICKET_PRICE;
            System.out.print("Enter the fee (£" + fee + "): £");
            double balance = scanner.nextDouble();
            user.setBalance(balance);
            
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
                    for (int i = 0; i < 6; i++) { // 6 ticket numbers
                        int number;
                        boolean validNumber = false;
                        do {
                            // Prompt the user for each number
                            number = scanner.nextInt();
                            // Check if the number not within the valid range between 1 and 49
                            if (number < 1 || number > 49) {
                                System.out.println("Invalid number! Please enter a number between 1 and 49.");
                            } else if (chosenNumbers.contains(number)) {
                                // Check if the number is a duplicate number or not since set not allow duplicate
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
            user.setTicket(ticket);
            users.add(user); // Add the user to the list of users
            addToPrizeFund(fee); // Add the ticket fee to the prize fund
            System.out.println("-------------------------\n");
            System.out.println("Your ticket number is " + ticket.getNumbers());
            System.out.println("Current prize pool has: £" + prizeFund);
            System.out.println("-------------------------\n");
        } else {
            System.out.println("Insufficient balance to buy tickets!");
        }
    }
}
    
    //This method to follow user to enter:
    //1. How much they like to play (auto substract £2 as fee and put it into prize pool)
    //2. Using Random class to create 6 unqiue number , using HashSet
    //Once finish step 2 , system will auto access setTicket from User class 
    private static void createLuckyDipTicket(Scanner scanner) {
        System.out.print("Enter your account ID: ");
        int accountId = scanner.nextInt();
        User user = findUserById(accountId);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
    
        double fee = TICKET_PRICE;
        System.out.print("Enter the fee (£" + fee + "): £");
        double balance = scanner.nextDouble();

        if (balance < fee) {
            System.out.println("Insufficient balance to buy tickets!");
            return; // Exit the method if the balance is insufficient
        }
        
        // Move the user creation outside the loop to avoid only the latest user is retained in the users list which lead to no user found
        user.setBalance(balance);
        
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
            user.setTicket(ticket); // Add the ticket to the user's list of tickets
            System.out.println("Lucky-dip ticket created with numbers: " + ticket.getNumbers());
            addToPrizeFund(fee); // Add the ticket fee to the prize fund
            //System.out.println("Your balance after ticket purchase: £" + (user.getBalance() - fee));
            System.out.println("Current prize pool has: £" + prizeFund);
            System.out.println("-------------------------\n");
            System.out.println("\n");
            users.add(user); // Add the user to the list of users
            break;
        }  
    }
    
    
    
    // This method allow to add ticket fee which £2 to the prize fund
    public static void addToPrizeFund(double amount) {
        prizeFund += amount; // Add £2 to the prize fund
    }
    
    // A method to view all tickets by Id
    private static void viewPurchasedTickets() {
        if (users.isEmpty()) {
            System.out.println("\nNo users found.\n");
            return;
        }
    
        Set<Integer> processedUserIds = new HashSet<>(); // Set to keep track of processed user IDs
        for (User user : users) {
            int userId = user.getAccountId();
            if (!processedUserIds.contains(userId)) { // Check if user has already been processed
                System.out.println("------------");
                System.out.println("\nUser: " + userId);
                List<LotteryTicket> userTickets = user.getTickets();
                if (!userTickets.isEmpty()) {
                    for (LotteryTicket ticket : userTickets) { // Print user's tickets
                        System.out.println("Ticket Numbers: " + ticket.getNumbers());
                    }
                }
                processedUserIds.add(userId); // Mark user as processed
            }
        }
    
        System.out.println("------------\n");
    
        if (processedUserIds.isEmpty()) {
            System.out.println("No tickets purchased yet.");
        }
    }
    
    
    // A method to run game base on admin enter 6 unique numbers
    private static void runLotteryGame(Scanner scanner) {
        System.out.println("Enter winning numbers (6 unique numbers between 1 and 49):");
        winningNumbers = new ArrayList<>();
        Set<Integer> chosenNumbers = new HashSet<>();
        while (chosenNumbers.size() < 6) {
            int number = scanner.nextInt();
            if (number < 1 || number > 49) { // Check if the number not within the valid range between 1 and 49
                System.out.println("Invalid number! Please enter a number between 1 and 49.");
            } else if (!chosenNumbers.add(number)) { // check any duplicate number
                System.out.println("Number already chosen! Please enter a unique number.");
            }
        }
        winningNumbers.addAll(chosenNumbers);
        System.out.println("Winning numbers set as: " + winningNumbers);
    }

    // A method to calculate match number
    private static double checkTicketWinnings(User user, List<Integer> ticketNumbers) {
        int correctNumbers = 0; // a int to store match number
        for (int number : ticketNumbers) {
            if (winningNumbers.contains(number)) { // Once the number match , add 1 to the correctNumbers , max will be 6 since there only has 6 numers
                correctNumbers++;
            }
        }
        return calculateWinnings(correctNumbers); // Calculate the winnings based on the number of correct numbers
    }
    
    // A method to subtract jackpot(£6) from the prizeFund
    /* 
    private static double jackpotWinnings(int numPlayers) {
        double jackpot = prizeFund / numPlayers;
        if (jackpot < 6.0) {
            jackpot = 6.0;
        }
        prizeFund -= jackpot;
        return jackpot;
    }
    */

    // A method to allow admin to check each palyer ticket and winning status , and how much they won
    /*
     * 1 or fewer matches, results in no prize.
     * 2 correct numbers should get a prize of £1.
     * 3 correct numbers should get a prize of £2.
     * 4 correct numbers should get a price of £4.
     * 5 correct numbers should get a price of £8.
     * 6 correct numbers should win the jackpot.
     */
    private static void reportResults() {
        System.out.println("Winning Numbers: " + winningNumbers);
        double totalWinnings = 0.0;
        Set<User> processedUsers = new HashSet<>(); // Set to keep track of processed users
        for (User user : users) {
            if (!processedUsers.contains(user)) { // Check if user has already been processed
                System.out.println("-----------------------------------------------------");
                System.out.println("User: " + user.fullname);
                double userWinnings = 0.0;
                for (LotteryTicket ticket : user.getTickets()) {
                    List<Integer> numbers = ticket.getNumbers();
                    double correctNumbers = checkTicketWinnings(user, numbers);
                    double ticketWinnings = calculateWinnings(correctNumbers);
                    userWinnings += ticketWinnings; // Update the user's total winnings
                    if (correctNumbers != 2 && correctNumbers != 0) { // Check if correctNumbers is neither 2 nor 0
                        System.out.println("Ticket Numbers: " + numbers + " - Correct Numbers: " + correctNumbers + " - Winnings: £" + ticketWinnings);
                    } else if (correctNumbers == 6) {
                        System.out.println("Congratulations! You've won the jackpot!");
                    }
                }
                System.out.println("Total Winnings: £" + userWinnings); // Concatenate the double to a String here
                totalWinnings += userWinnings;
                processedUsers.add(user); // Mark user as processed
            }
        }
        if (totalWinnings < 6.0 && prizeFund > 0) {
            rollOverPrizeFund();
        }
    }
    
    // A method to allow calculate the prize for each matching case and subtract from prize fund
    /*
     * 1 or fewer matches, results in no prize.
     * 2 correct numbers should get a prize of £1.
     * 3 correct numbers should get a prize of £2.
     * 4 correct numbers should get a price of £4.
     * 5 correct numbers should get a price of £8.
     * 6 correct numbers should win the jackpot.
     */
    private static double calculateWinnings(double correctNumbers) {
        double winnings = 0.0;
        switch ((int) correctNumbers) { // Convert double to int for switch statement
            case 2:
                winnings = 1.0; // Prize for 2 correct numbers
                break;
            case 3:
                winnings = 2.0; // Prize for 3 correct numbers
                break;
            case 4:
                winnings = 4.0; // Prize for 4 correct numbers
                break;
            case 5:
                winnings = 8.0; // Prize for 5 correct numbers
                break;
            case 6:
                System.out.println("Congratulations! You've won the jackpot!");
                break;
            default:
                // No prize for 1 or fewer correct numbers
                break;
        }
    
        if (winnings > 0) {
            prizeFund -= winnings; // Deduct winnings from the prize fund
            System.out.println("Prize Fund after deduction: £" + prizeFund);
        }
    
        return winnings;
    }
    
    // A method to display no jackpot winning
    private static void rollOverPrizeFund() {
        System.out.println("No jackpot winners. Prize fund rolls over to the next game.");
    }

    // A method to display message base on how many match ticket numbers
    private static void checkWinningTickets(Scanner scanner) {
        System.out.print("Enter your account ID: ");
        int accountId = scanner.nextInt();
    
        User currentUser = null;
        for (User user : users) { // if user id not found
            if (user.getAccountId() == accountId) {
                currentUser = user;
                break;
            }
        }
        
        // if user id founded
        if (currentUser != null) {
            List<LotteryTicket> userTickets = currentUser.getTickets();
            if (!userTickets.isEmpty()) {
                System.out.print("Your ticket is: ");
                for (LotteryTicket ticket : userTickets) {
                    System.out.print(ticket.getNumbers());
                }
                double correctNumbers = checkTicketWinnings(currentUser, userTickets.get(0).getNumbers()); // checkTicketWinnings : A method to calculate match number
                double winnings = calculateWinnings(correctNumbers); // calculateWinnings : A method to allow calculate the prize for each matching case and subtract from prize fund

                int winningsAsInt = (int) winnings;

                switch (winningsAsInt) {
                    case 6:
                        System.out.println("Congratulations! You've won the jackpot!");
                        System.exit(0); //There have a bug while show display message but also display default message. Temporary soulation until fix the problem
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                        System.out.println("Congratulations! You've won £" + winnings + "!");
                        break;
                    default:
                        System.out.println("Sorry! You did not win this time.");
                        break;
                }

            } else {
                System.out.println("No tickets found for this user.");
                }
        } else {
            System.out.println("User not found!");
            }
    }
    
    // A method to display current prize fund we have 
    private static void viewGameDetails() {
        System.out.println("Prize Fund: £" + prizeFund);
        //System.out.println("Total Amount Given to Charity: £" + (TICKET_PRICE * users.size() - prizeFund));
        System.out.println("-------------------------\n");
    }
}