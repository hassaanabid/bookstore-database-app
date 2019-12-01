import java.sql.*;
import java.util.*;

/**
 *  Name: Hassaan Abid
 *  ID: 214243935
 *  EECS Account: ha104
 *  Part B - Database Application
 *  File: YRB.java
 *
 *  Class YRB (York River Bookseller) - contains the core application logic.
 *  It implements the actions that the user can perform as well as it handles the user's input.
 *  The typical actions include finding and purchasing books.
 */
public class YRB {

    // Customer and shopping data
    String seperator = "---------------------------------------------------------------------";
    private String TITLE = "************* YRB Online Bookstore *************";
    private Scanner scanner = null;

    private Connect db;
    private Connection conDB;

    private double clubBookPrice;
    private short currentCustomerCid;
    private short selectedBookYear;
    private short bookQuantity;
    private int selectedCategory;
    private int selectedBookIndex;
    private String selectedTitle;
    private String offeringClub;
    private Map<Integer, String> catMap;
    private Map<Integer, List<String>> booksMap;

    /**
     * YRB Constructor
     * Initialize the databse connection and starts the application.
     */
    public YRB() {
        scanner = new Scanner(System.in);

        db = new Connect();

        conDB = db.getConDB();
        System.out.println(TITLE);
        System.out.println();
        System.out.println();

        execute(Routine.fetchCustomerInfo);
        db.closeConnection();
    }

    /**
     * Main Client
     * @param args no args required
     */
    public static void main(String[] args) {
        YRB yrb = new YRB();
    }


    /**
     * Defines the scope of possible routines within the application
     */
    public enum Routine {
        fetchCustomerInfo,
        updateCustomerInfo,
        displaySelectCategory,
        displaySelectBooks,
        displayPriceCalTprice;
    }

    /**
     * Handles the workflow of the application by decoupling the states.
     * @param routine a process to execute.
     */
    private void execute(Routine routine) {
        switch (routine) {
            case fetchCustomerInfo:
                this.fetchCustomerInfo();
                break;

            case updateCustomerInfo:
                this.updateCustomerInfo();
                break;

            case displaySelectCategory:
                this.displaySelectCategory();
                break;

            case displaySelectBooks:
                this.displaySelectBooks();
                break;

            case displayPriceCalTprice:
                this.displayPriceCalTprice();
                break;

            default:
                break;
        }

    }

    /**
     * Utility method
     * Check if the user would like to continue or exit the application.
     */
    private void checkToContinueTranscation() {
        while (true) {
            System.out.print("Would you like to continue? (Y/N) ");
            String eStr = scanner.nextLine();
            eStr = eStr.trim();

            if (eStr.equals("N") || eStr.equals("n")) {
                System.out.println("\nExiting now.\n");
                System.exit(0);
            } else if (eStr.equals("Y") || eStr.equals("y")) {
                break;
            } else {
                System.out.println("Please make a valid decision i.e. (Y/N)");
            }
        }
        System.out.println();
    }

    /**
     * Utility method
     * Gets user input as a string
     * @param prompt
     * @return
     */
    public String requestInput(String prompt) {
        System.out.print(prompt);
        String inStr = scanner.nextLine();
        inStr = inStr.trim();
        return inStr;
    }

    /**
     * Parses the year from Book's properties
     * @param value The properties of a book represented as a list of strings.
     *              Format of values: ["Title", "Year", "Language", "Category", "Weight"]
     * @return year of the Book
     */
    private short parseBookYear(List<String> value) {
        return Short.parseShort(value.get(1));
    }

    /**
     * Converts properties of a book to a string representation.
     * @param value book properties in String notation.
     * @return
     */
    private String bookPropertiesToString(List<String> value) {
        // "Title", "Year", "Language", "Category", "Weight"
        String res = String.format(("%20s %6s %16s %16s %6s"),
                value.get(0),
                value.get(1),
                value.get(2),
                value.get(3),
                value.get(4));
        return res;
    }


    /**
     * Gets the customers information from the database
     * Handles invalid inputs
     */
    private void fetchCustomerInfo() {
        int cid = -1;
        while (true) {
            try {
                String strCid = requestInput("Customer Id: ");
                cid = Integer.parseInt(strCid);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input please enter an integer cid.");
            }
        }

        try {
            PreparedStatement querySt = conDB.prepareStatement(YRBQueries.GET_CUSTOMER_INFO);
            querySt.setInt(1, cid);
            ResultSet answers = querySt.executeQuery();
            if (answers.next()) {
                String cId = answers.getString("cid");
                String cName = answers.getString("name");
                String cCity = answers.getString("city");

                System.out.println("\n" + seperator);
                System.out.print("Customer Information Retrieved\n" +
                        "ID : " + cId + "\n" +
                        "Name : " + cName + "\n" +
                        "City : " + cCity + "\n"
                );
                System.out.println(seperator + "\n");
                currentCustomerCid = Short.parseShort(cId);
                execute(Routine.updateCustomerInfo);
            } else {
                System.out.println("ERROR: Customer id not found. Try Again.");
                fetchCustomerInfo();
            }
            answers.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    /**
     * Updates the user information in the database
     */
    private void updateCustomerInfo() {
        String ans = requestInput("Would you like to update the customer information? (Y/N) ");
        if (ans.equals("y") || ans.equals("Y")) {
            String inName = requestInput("Customer Name: ");
            String inCity = requestInput("Customer City: ");

            PreparedStatement querySt = null;
            try {

                querySt = conDB.prepareStatement(YRBQueries.UPDATE_CUSTOMER_INFO);

                querySt.setString(1, inName);
                querySt.setString(2, inCity);
                querySt.setString(3, String.valueOf(this.currentCustomerCid));

                int rowsAffected = querySt.executeUpdate();
                this.db.commit();
                if (rowsAffected == 1) {
                    System.out.println("Update Successful !");
                }
                execute(Routine.displaySelectCategory);
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(0);
            }

        } else if (ans.equals("n") || ans.equals("N")) {
            execute(Routine.displaySelectCategory);
        } else {
            System.out.println("ERROR: incorrect response, enter either Y or N.");
            updateCustomerInfo();
        }
    }

    /**
     * Displays all categories and lets user chose a category.
     * Handles invalid inputs.
     */
    private void displaySelectCategory() {
        System.out.println("\n ************* Book Categories ************* \n");

        this.catMap = new TreeMap<Integer, String>();
        int count = 1;
        try {
            PreparedStatement querySt = conDB.prepareStatement(YRBQueries.ALL_CATEGORIES);
            ResultSet answers = querySt.executeQuery();
            while (answers.next()) {
                catMap.put(count, answers.getString(1));
                count++;
            }
            answers.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // display categories
        System.out.printf("\n%5s \t%s\n", "Number", "Category Name");
        System.out.println(this.seperator);
        for (Map.Entry<Integer, String> e : catMap.entrySet()) {
            System.out.printf("%5s. \t%s\n", e.getKey().toString(), e.getValue());
        }
        System.out.println();

        int inCat = -1;
        while (true) {
            try {
                String inCategory = this.requestInput("Choose a category number: ");
                System.out.println();
                inCat = Integer.parseInt(inCategory);
                if (catMap.containsKey(inCat)) {
                    System.out.println("Category " + catMap.get(inCat) + " is selected.");
                    System.out.println();
                    this.selectedCategory = inCat;
                    this.checkToContinueTranscation();
                    break;
                } else {
                    System.out.println("Please chose a valid category number");
                }
            } catch (Exception e) {
                System.out.println("Invalid input please enter an integer category number.");
            }
        }
        execute(Routine.displaySelectBooks);

    }

    /**
     * Lets the user enter a title and searches for books that a user can buy within the chosen category.
     * Only books that the club of the user is offering are shown.
     * If the title is not available the user may choose a different category and a different title.
     * Handles invalid inputs.
     */
    private void displaySelectBooks() {
        String strInTitle = requestInput("Title: ");
        this.selectedTitle = strInTitle;
        this.booksMap = new TreeMap<Integer, List<String>>();
        int count = 1;
        boolean foundBooks = false;
        // QUERY database
        try {
            PreparedStatement querySt = conDB.prepareStatement(YRBQueries.FIND_PURCHASEABLE_BOOKS);

            // M.cid = ? and B.cat = ? and B.title = ?
            querySt.setString(1, String.valueOf(this.currentCustomerCid));
            querySt.setString(2, catMap.get(this.selectedCategory));
            querySt.setString(3, strInTitle);
            ResultSet answers = querySt.executeQuery();
            while (answers.next()) {
                foundBooks = true;
                List<String> bookProps = new ArrayList<String>(
                        Arrays.asList(
                                //  B.title, B.year, B.language, B.cat, B.weight
                                answers.getString(1),
                                String.valueOf(answers.getShort(2)),
                                answers.getString(3),
                                answers.getString(4),
                                String.valueOf(answers.getShort(5))
                        ));

                booksMap.put(count, bookProps);
                count++;
            }
            answers.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        if (!foundBooks) {
            // No book found
            System.out.println("Unfortunately, The book is not offered by your clubs.");
            this.checkToContinueTranscation();
            execute(Routine.displaySelectCategory);
        } else {
            // display result
            System.out.println("\n ************* Books Found ************* \n");

            System.out.printf(("%4s %20s %6s %16s %16s %6s"), "Number", "Title", "Year", "Language", "Category", "Weight");
            System.out.println();
            System.out.println(this.seperator);
            for (Map.Entry<Integer, List<String>> e : booksMap.entrySet()) {
                System.out.printf("%4s. %s\n", e.getKey().toString(), bookPropertiesToString(e.getValue()));
            }
            System.out.println();

            // BOOK_TITLES_IN_CATEGORY
            while (true) {
                try {
                    String strBookNum = requestInput("Enter a book number to purchase: ");
                    int intBookNum = Integer.parseInt(strBookNum);
                    if (booksMap.containsKey(intBookNum)) {
                        System.out.println();
                        System.out.println("Book number " + strBookNum + " is selected.\n" +
                                "Book details: " +
                                bookPropertiesToString(booksMap.get(intBookNum)) + "\n");
                        this.selectedBookIndex = intBookNum;
                        this.selectedBookYear = parseBookYear(booksMap.get(intBookNum));
                        this.checkToContinueTranscation();
                        break;
                    } else {
                        System.out.println("Please chose a valid book number");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input please enter an integer book number.");
                }
            }
            execute(Routine.displayPriceCalTprice);
        }
    }

    /**
     * Calculates the total prices for the order.
     * Lets the user choose if they would like to complete the purchase.
     * After the purchase the user may continue shopping or exit.
     */
    private void displayPriceCalTprice() {
        // Find minimum price for the book
        // QUERY database
        try {
            PreparedStatement querySt = conDB.prepareStatement(YRBQueries.FIND_CLUB_OFFERING_MIN_PRICE);
            // M.cid = ? and O.title = ? and O.year = ?
            querySt.setString(1, String.valueOf(this.currentCustomerCid));
            querySt.setString(2, String.valueOf(this.selectedTitle));
            querySt.setString(3, String.valueOf(this.selectedBookYear));

            ResultSet answers = querySt.executeQuery();

            if (answers.next()) {
                this.offeringClub = answers.getString(1);
                String strClubBookPrice = answers.getString(2);
                this.clubBookPrice = Double.parseDouble(strClubBookPrice);
            } else {
                System.out.println("Book not found, something went wrong.");
                System.exit(0);
            }
            answers.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // Display Total price to user
        System.out.println("The minimum price for this book is : " + this.clubBookPrice + "\n");
        System.out.println("Club offering this price : " + this.offeringClub + "\n");
        while (true) {
            try {
                String strBookQuantity = requestInput("Enter books quantity: ");
                this.bookQuantity = Short.parseShort(strBookQuantity);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input please enter a valid number as quantity.");
            }
        }
        double totalPurchaseAmount = this.bookQuantity * this.clubBookPrice;
        System.out.println(String.format("The total price for your order will be : $ %.2f \n", totalPurchaseAmount));
        System.out.println();

        // ask if they would like to buy
        while (true) {
            String strPurchaseDecision = requestInput("Would you like to purchase the book/books? (Y/N) ");
            if (strPurchaseDecision.equals("Y") || strPurchaseDecision.equals("y")) {
                // insert purchase into database
                try {
                    PreparedStatement querySt = conDB.prepareStatement(YRBQueries.REGISTER_PURCHASE);

                    // (cid, club, title, year, when, qnty) VALUES (?, ?, ?, ?, ?, ?)
                    querySt.setShort(1, this.currentCustomerCid);
                    querySt.setString(2, this.offeringClub);
                    querySt.setString(3, this.selectedTitle);
                    querySt.setShort(4, this.selectedBookYear);
                    querySt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    querySt.setShort(6, this.bookQuantity);

                    querySt.execute();
                    this.db.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
                System.out.println("\nThank you for your purchase.\n");
                this.checkToContinueTranscation();
                execute(Routine.displaySelectCategory);
                break;
            } else if (strPurchaseDecision.equals("N") || strPurchaseDecision.equals("n")) {
                while (true) {
                    String strContinueShopping = requestInput("Would you like to continue shopping? (Y/N) ");
                    if (strContinueShopping.equals("Y") || strContinueShopping.equals("y")) {
                        // chose another category and title
                        execute(Routine.displaySelectCategory);
                        break;
                    } else if (strContinueShopping.equals("N") || strContinueShopping.equals("n")) {
                        System.exit(0);
                    } else {
                        System.out.println("Please make a valid decision i.e. (Y/N)");
                    }
                }
                break;
            } else {
                System.out.println("Please make a valid decision i.e. (Y/N)");
            }
        }


    }

}
