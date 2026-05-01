public class BookingManager {
    private final IPaymentGateway paymentGateway;
    private final INotificationService notificationService;
    private final IEventRepository eventRepository;

    // Constructor Injection for Section 1
    public BookingManager(IPaymentGateway paymentGateway, 
                          INotificationService notificationService, 
                          IEventRepository eventRepository) {
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
        this.eventRepository = eventRepository;
    }

    public void processBooking(String eventId, double amount, String email) {
        // Logic to be added by teammates
    }
}

interface IPaymentGateway { String processPayment(double amount); }
interface INotificationService { void sendConfirmation(String email, String message); }
interface IEventRepository { boolean isSoldOut(String eventId); void saveBooking(String eventId); }