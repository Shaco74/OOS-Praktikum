package bank;

import bank.exceptions.NumericValueInvalidException;

public class IncomingTransfer extends Transfer {
    /**
     * Instantiates a new transfer by copying another transfer object
     *
     * @param transfer the transfer object to copy from
     * @throws NumericValueInvalidException if the transfer is invalid
     */
    public IncomingTransfer(Transfer transfer) throws NumericValueInvalidException {
        super(transfer);
    }

    /**
     * Instantiates a new transfer
     *
     * @param date        the date
     * @param description the description
     * @param amount      the amount
     */
    public IncomingTransfer(String date, String description, double amount) throws NumericValueInvalidException {
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
    public IncomingTransfer(String date, String description, double amount, String sender, String recipient) throws NumericValueInvalidException {
        super(date, description, amount, sender, recipient);
    }

    @Override
    public double calculate(){
        return getAmount();
    }

}
