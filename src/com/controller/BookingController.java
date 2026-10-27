package com.controller;

import com.model.Booking;
import com.repository.impl.BookingRepository;

public class BookingController {
    private final BookingRepository bookingRepository;
    public BookingController(BookingRepository bookingRepository) { this.bookingRepository = bookingRepository; }
    public boolean createBooking(Booking booking) { return bookingRepository.save(booking); }
}
