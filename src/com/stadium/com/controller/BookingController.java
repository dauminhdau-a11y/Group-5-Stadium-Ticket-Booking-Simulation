package com.stadium.com.controller;

import com.stadium.com.model.Booking;
import com.stadium.com.repository.impl.BookingRepository;

public class BookingController {
    private final BookingRepository bookingRepository;
    public BookingController(BookingRepository bookingRepository) { this.bookingRepository = bookingRepository; }
    public boolean createBooking(Booking booking) { return bookingRepository.save(booking); }
}
