public interface IEventRepository {
    boolean isSoldOut(String eventId);
    void saveBooking(String eventId, String email, int tickets, String transactionId);
}
