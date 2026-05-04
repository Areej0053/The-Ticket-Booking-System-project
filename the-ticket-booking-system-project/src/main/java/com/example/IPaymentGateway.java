public interface IPaymentGateway {
    String processPayment(String email, int amount);
}
