
public final class YRBQueries {

    public static final String GET_CUSTOMER_INFO = "SELECT cid, name, city FROM yrb_customer WHERE cid = ?";

    public static final String UPDATE_CUSTOMER_INFO = "UPDATE yrb_customer SET name = ?, city = ? WHERE cid = ?";

    public static final String ALL_CATEGORIES = "SELECT cat FROM yrb_category ORDER BY cat";

    /**
     * Purchase-able means: The book is being offered by some club that the customer is a member of.
     * Using Distinct since the same book could be offered by multiple clubs that a customer is member of.
     * Note: The query will still return more than one books with same title but different year, as required.
     */
    public static final String FIND_PURCHASEABLE_BOOKS =
        "SELECT DISTINCT B.title, B.year, B.language, B.cat, B.weight \n" +
                "FROM (yrb_book B join yrb_offer O \n" +
                "on O.title = B.title and O.year = B.year) \n" +
                "join yrb_member M on M.club = O.club \n" +
                "where M.cid = ? and B.cat = ? and B.title = ? \n";

    /**
     * Finds the club offering the minimum price that a customer is member of.
     * EDGE CASE: There could be ties on minimum price.
     *            Example: Customer is a member of multiple clubs AND more than one of those clubs are offering the book
     *            at minimum price. Solution here is to just limit the result set to 1 since all such satisfy the
     *            price requirement.
     * Note: Orderby is processed before Limit in SQL
     */
    public static final String FIND_CLUB_OFFERING_MIN_PRICE =
            "SELECT O.club, O.price \n" +
                    "FROM yrb_offer O join yrb_member M on M.club = O.club \n" +
                    "where M.cid = ? and O.title = ? and O.year = ? \n" +
                    "order by O.price ASC \n" +
                    "limit 1 \n";

    public static final String REGISTER_PURCHASE =
            "INSERT INTO yrb_purchase (cid, club, title, year, when, qnty) VALUES (?, ?, ?, ?, ?, ?)";

}
