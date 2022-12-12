package bank;

import bank.exceptions.NumericValueInvalidException;

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
     * @throws NumericValueInvalidException if the amount is negative
     */
    public Transfer(Transfer transfer) throws NumericValueInvalidException {
        this(transfer.date, transfer.description, transfer.getAmount(), transfer.sender, transfer.recipient);
    }

    /**
     * Instantiates a new transfer
     *
     * @param date        the date
     * @param description the description
     * @param amount      the amount
     * @throws NumericValueInvalidException if the amount is negative
     */
    public Transfer(String date, String description, double amount) throws NumericValueInvalidException {
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
     * @throws NumericValueInvalidException if the amount is negative
     */
    public Transfer(String date, String description, double amount, String sender, String recipient) throws NumericValueInvalidException {
        this(date, description, amount);
        this.sender = sender;
        this.recipient = recipient;
    }

    /**
     * Set the amount of the transfer
     *
     * @param amount the amount
     * @throws NumericValueInvalidException if the amount is negative
     */
    @Override
    public void setAmount(double amount) throws NumericValueInvalidException {
        if (amount < 0) {
            throw new NumericValueInvalidException("Error: Negative Input for Transfer amount!");
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

    /**
     * Calculates the effective amount
     *
     * @return the effective amount
     */
    @Override
    public double calculate() {
        return amount;
    }

    /**
     * Converts the object to a printable formatted string
     *
     * @return the object string
     */
    @Override
    public String toString() {
        String newLine = System.getProperty("line.separator");
        return (super.toString() + "Sender: " + sender + newLine + "Recipient: " + recipient + newLine);
    }

    /**
     * Compares an object to the current object and returns true if they share the same attributes
     *
     * @param obj the object to be compared with
     * @return true if they are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else {
            if (obj instanceof Transfer transfer) {
                return super.equals(transfer) && sender.equals(transfer.sender) && recipient.equals(transfer.recipient);
            } else {
                return false;
            }
        }
    }
}

