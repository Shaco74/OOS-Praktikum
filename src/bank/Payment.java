package bank;

import bank.exceptions.NumericValueInvalidException;

/**
 * Payment objects are used to represent payments made by customers
 */
public class Payment extends Transaction {

    /**
     * The incomingInterest is the interest on deposits
     * The outgoingInterest is the interest on withdrawals
     */
    private double incomingInterest;
    private double outgoingInterest;

    /**
     * Instantiate a new payment by coping another payment object
     *
     * @param payment the payment object to copy from
     */
    public Payment(Payment payment) throws NumericValueInvalidException {
        this(payment.date, payment.description, payment.getAmount(), payment.getIncomingInterest(), payment.getOutgoingInterest());
    }


    /**
     * Instantiate a new payment
     *
     * @param date             the date of the payment
     * @param description      the description
     * @param amount           the amount
     * @param incomingInterest the incoming interest on deposits
     * @param outgoingInterest the outgoing interest on withdrawals
     */
    public Payment(String date, String description, double amount, double incomingInterest, double outgoingInterest) throws NumericValueInvalidException {
        this(date, description, amount);
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);
    }

    /**
     * Instantiate a new payment
     * @throws NumericValueInvalidException if the amount is 0
     */
    public Payment(String date, String description, double amount) throws NumericValueInvalidException {
        super(date, description, amount);
    }

    /**
     * Get outgoing interest on withdrawals
     *
     * @return the outgoing interest
     */
    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    /**
     * Set outgoing interest on withdrawals
     *
     * @param outgoingInterest the outgoing interest
     * @throws NumericValueInvalidException if the interest is not between 0 and 1
     */
    public void setOutgoingInterest(double outgoingInterest) throws NumericValueInvalidException {
        if (outgoingInterest < 0 || outgoingInterest > 1) {
            throw new NumericValueInvalidException("Error: Negative Input for outgoing interest: Withdrawal!");
        }
        this.outgoingInterest = outgoingInterest;
    }

    /**
     * Get incoming interest on deposits
     *
     * @return the incoming interest
     */
    public double getIncomingInterest() {
        return incomingInterest;
    }

    /**
     * Set incoming interest on deposits
     *
     * @param incomingInterest the incoming interest
     * @throws NumericValueInvalidException if the interest is not between 0 and 1
     */
    public void setIncomingInterest(double incomingInterest) throws NumericValueInvalidException {
        if (incomingInterest < 0 || incomingInterest > 1) {
            throw new NumericValueInvalidException("Error: Negative Input for incoming interest: Deposit)!");

        } else {
            this.incomingInterest = incomingInterest;
        }
    }

    /**
     * Get amount of the payment
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set amount of the payment
     *
     * @param amount the amount
     * @throws NumericValueInvalidException if the amount is 0
     */
    @Override
    public void setAmount(double amount) throws NumericValueInvalidException {
        if (amount != 0) {
            this.amount = amount;
        }else{
            throw new NumericValueInvalidException("Error: 0 Input for Payment amount!");
        }
    }

    /**
     * Get the date of the payment
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Set the date of the payment
     *
     * @param date the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Get the description of the payment
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the payment
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Calculates the effective amount with interests applied
     *
     * @return the amount with applied interests
     */
    @Override
    public double calculate() {
        if (amount > 0) {
            //amount is positive
            return (amount - (amount * incomingInterest));
        } else {
            //amount is negative
            return (amount + (amount * outgoingInterest));
        }
    }

    /**
     * Converts the object to a printable formatted string
     *
     * @return the object string
     */
    @Override
    public String toString() {
        String newLine = System.getProperty("line.separator");
        return (super.toString() + "Incoming Interest: " + incomingInterest + newLine + "Outgoing Interest: " + outgoingInterest + newLine);
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
            if (obj instanceof Payment payment) {
                return (super.equals(payment) && incomingInterest == payment.incomingInterest && outgoingInterest == payment.outgoingInterest);
            } else {
                return false;
            }
        }
    }
}
