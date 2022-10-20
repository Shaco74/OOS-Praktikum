package bank;

/**
 * Abstract transaction class representing the general transactions
 */
public abstract class Transaction {
    /**
     * The date of the transaction
     * The description of the transaction
     * The amount of the transaction
     */
    protected String date;
    protected String description;
    protected double amount;

    /**
     * Constructor setting the date and description of a transaction
     *
     * @param date the date
     * @param description the description
     */
    public Transaction(String date, String description, double amount) {
        this.date = date;
        this.description = description;
        setAmount(amount);
    }

    /**
     * Get the description of the transaction
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the transaction
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the date of the transaction
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Set the date of the transaction
     *
     * @param date the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Get the amount of the transaction
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set amount of the transaction
     *
     * @param amount the amount
     */
    public abstract void setAmount(double amount);
}
