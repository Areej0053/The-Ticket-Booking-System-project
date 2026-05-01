import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BookingManagerTest {
    
    private BookingManager bookingManager;
    @Mock private IPaymentGateway paymentGateway;
    @Mock private INotificationService notificationService;
    @Mock private IEventRepository eventRepository;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingManager = new BookingManager(paymentGateway, notificationService, eventRepository);
    }
    
    // US-01: Happy Path (2 points)
    @Test
    void testHappyPath_ValidInput_PaymentSucceeds_NotSoldOut() {
        when(eventRepository.isSoldOut("event123")).thenReturn(false);
        when(paymentGateway.processPayment("user@example.com", 200)).thenReturn("TXN123");
        
        String result = bookingManager.processBooking("event123", "user@example.com", 2);
        
        assertEquals("Booking successful: TXN123", result);
        verify(eventRepository, times(1)).isSoldOut("event123");
        verify(paymentGateway, times(1)).processPayment("user@example.com", 200);
        verify(eventRepository, times(1)).saveBooking("event123", "user@example.com", 2, "TXN123");
        verify(notificationService, times(1)).sendConfirmation("user@example.com", "event123", 2);
    }
    
    // US-02: Invalid Paths (1 point)
    @Test
    void testInvalidInput_NothingCalled() {
        String result = bookingManager.processBooking("", "user@example.com", 2);
        
        assertEquals("Invalid input", result);
        verify(paymentGateway, never()).processPayment(anyString(), anyInt());
        verify(eventRepository, never()).saveBooking(anyString(), anyString(), anyInt(), anyString());
        verify(notificationService, never()).sendConfirmation(anyString(), anyString(), anyInt());
        verify(eventRepository, never()).isSoldOut(anyString());
    }
    
    // US-03: Sold Out Path (1 point)
    @Test
    void testEventSoldOut_OnlyIsSoldOutCalled() {
        when(eventRepository.isSoldOut("event123")).thenReturn(true);
        
        String result = bookingManager.processBooking("event123", "user@example.com", 2);
        
        assertEquals("Event sold out", result);
        verify(eventRepository, times(1)).isSoldOut("event123");
        verify(paymentGateway, never()).processPayment(anyString(), anyInt());
        verify(eventRepository, never()).saveBooking(anyString(), anyString(), anyInt(), anyString());
        verify(notificationService, never()).sendConfirmation(anyString(), anyString(), anyInt());
    }
}
