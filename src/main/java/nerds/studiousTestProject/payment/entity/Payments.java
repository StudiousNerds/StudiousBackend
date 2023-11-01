package nerds.studiousTestProject.payment.entity;

import java.util.List;

public class Payments {
    private List<Payment> payments;

    public List<Payment> getPayments() {
        return payments;
    }

    public Payments(List<Payment> payments) {
        this.payments = payments;
    }

    public String getMethods() {
        return this.payments.stream().map(Payment::getMethod).distinct().toString();
    }

    public int getTotalPrice(){
        return this.payments.stream().mapToInt(Payment::getPrice).sum();
    }

}
