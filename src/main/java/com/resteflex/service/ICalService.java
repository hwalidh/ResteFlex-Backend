package com.resteflex.service;

import com.resteflex.entity.Booking;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class ICalService {

    private static final String CALENDAR_DIR = "calendars";
    private static final String CALENDAR_FILE = CALENDAR_DIR + "/bookings.ics";

    public void addBookingToCalendar(Booking booking) {
        try {
            ensureDirectoryExists();
            Calendar calendar = buildCalendar();

            Date start = Date.from(booking.getCheckIn().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(booking.getCheckOut().atStartOfDay(ZoneId.systemDefault()).toInstant());

            VEvent event = new VEvent(new DateTime(start), new DateTime(end),
                    "Réservation: " + booking.getListing().getTitle());

            event.getProperties().add(new Uid(new RandomUidGenerator().generateUid().getValue()));
            event.getProperties().add(new Description(
                    "Email: " + booking.getEmail() +
                    "\nHôtes: " + booking.getGuests() +
                    "\nTotal: " + booking.getTotalPrice() + "€"));
            event.getProperties().add(new Location(booking.getListing().getLocation()));

            calendar.getComponents().add(event);
            saveCalendar(calendar);
            log.info("Booking {} added to iCal", booking.getId());
        } catch (Exception e) {
            log.error("Error adding booking to iCal: {}", e.getMessage());
        }
    }

    public void removeBookingFromCalendar(Booking booking) {
        try {
            ensureDirectoryExists();
            Calendar calendar = buildCalendar();
            String emailToRemove = booking.getEmail();
            calendar.getComponents().removeIf(component -> {
                if (!(component instanceof VEvent)) return false;
                VEvent event = (VEvent) component;
                Description desc = (Description) event.getProperties().getProperty("DESCRIPTION");
                return desc != null && desc.getValue().contains(emailToRemove);
            });
            saveCalendar(calendar);
            log.info("Booking {} removed from iCal", booking.getId());
        } catch (Exception e) {
            log.error("Error removing booking from iCal: {}", e.getMessage());
        }
    }

    private Calendar buildCalendar() {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//ResteFlex//Bookings//FR"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        return calendar;
    }

    private void saveCalendar(Calendar calendar) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(CALENDAR_FILE)) {
            new CalendarOutputter().output(calendar, fos);
        }
    }

    private void ensureDirectoryExists() {
        new File(CALENDAR_DIR).mkdirs();
    }
}
