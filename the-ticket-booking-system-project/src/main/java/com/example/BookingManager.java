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

        if (eventId == null || eventId.trim().isEmpty()
                || customerEmail==null || customerEmail.trim().isEmpty()
                || tickets <= 0) {
            return "Invalid input";
        }
        
        if (eventRepository.isSoldOut(eventId)) {
            return "Event sold out";
        }
    
        String transactionId = paymentGateway.processPayment(customerEmail, tickets * 100);

        if (transactionId == null) {
            return "Payment failed";
        }

        eventRepository.saveBooking(eventId, customerEmail, tickets, transactionId);

        notificationService.sendConfirmation(customerEmail, eventId, tickets);

        return "Booking successful: " + transactionId;
    }
}

