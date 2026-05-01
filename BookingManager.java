public class BookingManager {

    private final IPaymentGateway paymentGateway;
    private final INotificationService notificationService;
    private final IEventRepository eventRepository;

    public BookingManager(IPaymentGateway paymentGateway,
                          INotificationService notificationService,
                          IEventRepository eventRepository) {
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
        this.eventRepository = eventRepository;
    }

    public String processBooking(String eventId, String customerEmail, int tickets) {

        // Validation
        if (eventId == null || eventId.trim().isEmpty()
                || customerEmail==null || customerEmail.trim().isEmpty()
                || tickets <= 0) {
            return "Invalid input";
        }

        // Check availability
        if (eventRepository.isSoldOut(eventId)) {
            return "Event sold out";
        }

        // Payment
        String transactionId = paymentGateway.processPayment(customerEmail, tickets * 100);

        if (transactionId == null) {
            return "Payment failed";
        }

        // Save booking
        eventRepository.saveBooking(eventId, customerEmail, tickets, transactionId);

        // Notification
        notificationService.sendConfirmation(customerEmail, eventId, tickets);

        return "Booking successful: " + transactionId;
    }
}
    // IPaymentGateway.java
public interface IPaymentGateway {
    String processPayment(String email, int amount);
}

// INotificationService.java
public interface INotificationService {
    void sendConfirmation(String email, String eventId, int tickets);
}

// IEventRepository.java
public interface IEventRepository {
    boolean isSoldOut(String eventId);
    void saveBooking(String eventId, String email, int tickets, String transactionId);
}
