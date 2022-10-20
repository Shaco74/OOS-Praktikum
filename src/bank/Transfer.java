package bank;

/**
 * Transfer objects are used to represent transfers between accounts
 */
public class Transfer extends Transaction {
    /**
     * The sender of the transfer
     * The recipient of the transfer
     */
    private String sender;
    private String recipient;

    /**
     * Instantiates a new transfer by copying another transfer object
     *
     * @param transfer the transfer object to copy from
     */
    public Transfer(Transfer transfer) {
        this(transfer.date, transfer.description, transfer.getAmount(), transfer.sender, transfer.recipient);
    }

    /**
     * Instantiates a new transfer
     *
     * @param date        the date
     * @param description the description
     * @param amount      the amount
     */
    public Transfer(String date, String description, double amount) {
        super(date, description, amount);
    }

    /**
     * Instantiates a new transfer
     *
     * @param date        the date
     * @param description the description
     * @param amount      the amount
     * @param sender      the sender
     * @param recipient   the recipient
     */
    public Transfer(String date, String description, double amount, String sender, String recipient) {
        this(date, description, amount);
        this.sender = sender;
        this.recipient = recipient;
    }

    /**
     * Print object to console
     */
    public void printObject() {
        System.out.println("Date: " + date);
        System.out.println("Description: " + description);
        System.out.println("Amount: " + amount);
        System.out.println("Sender: " + sender);
        System.out.println("Recipient: " + recipient);
    }


    /**
     * Set the amount of the transfer
     *
     * @param amount the amount
     */
    @Override
    public void setAmount(double amount) {
        if (amount < 0) {
            System.out.println("Error: Negative Input for Transfer amount!");
        } else {
            this.amount = amount;
        }
    }

    /**
     * Get the sender of the transfer
     *
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Set the sender of the transfer
     *
     * @param sender the sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Get the recipient of the transfer
     *
     * @return the recipient
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Set the recipient of the Transfer
     *
     * @param recipient the recipient
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}

