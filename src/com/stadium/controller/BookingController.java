package com.stadium.controller;

import com.stadium.model.Booking;
import com.stadium.repository.impl.BookingRepository;

public class BookingController {
    private final BookingRepository bookingRepository;
    public BookingController(BookingRepository bookingRepository) { this.bookingRepository = bookingRepository; }
    public boolean createBooking(Booking booking) { return bookingRepository.save(booking); }
}
