package tn.esprit.eventsproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;
import tn.esprit.eventsproject.services.EventServicesImpl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EventServicesImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @InjectMocks
    private EventServicesImpl eventServices;

    private Event event;
    private Participant participant;
    private Logistics logistics;

    @BeforeEach
    public void setUp() {
        // Setup test entities
        event = new Event();
        event.setDescription("Sample Event");
        event.setDateDebut(LocalDate.now());
        event.setDateFin(LocalDate.now().plusDays(1));
        event.setLogistics(new HashSet<>()); // Initialize the logistics set to avoid NullPointerException

        participant = new Participant();
        participant.setIdPart(1);
        participant.setNom("Tounsi");
        participant.setPrenom("Ahmed");

        logistics = new Logistics();
        logistics.setDescription("Sample Logistics");
        logistics.setPrixUnit(100);
        logistics.setQuantite(2);
        logistics.setReserve(true);
    }

    @Test
    public void testAddParticipant() {
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        Participant result = eventServices.addParticipant(participant);

        assertNotNull(result);
        assertEquals("Tounsi", result.getNom());
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    public void testAddAffectLog() {
        when(eventRepository.findByDescription("Sample Event")).thenReturn(event);
        when(logisticsRepository.save(any(Logistics.class))).thenReturn(logistics);

        Logistics result = eventServices.addAffectLog(logistics, "Sample Event");

        assertNotNull(result);
        assertTrue(event.getLogistics().contains(logistics));
        verify(eventRepository, times(1)).findByDescription("Sample Event");
        verify(logisticsRepository, times(1)).save(logistics);
    }

    @Test
    public void testGetLogisticsDates() {
        // Initialize the start and end date for the search range
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        // Mock the event repository to return the event when queried by date range
        when(eventRepository.findByDateDebutBetween(startDate, endDate)).thenReturn(List.of(event));

        // Add logistics to the event for testing purposes
        event.setLogistics(new HashSet<>());
        event.getLogistics().add(logistics);  // Add the logistics to the event

        // Call the method under test
        List<Logistics> result = eventServices.getLogisticsDates(startDate, endDate);

        // Ensure the result is not null
        assertNotNull(result, "The result should not be null");

        // Ensure the logistics list is not empty and contains the logistics added to the event
        assertFalse(result.isEmpty(), "The logistics list should not be empty");
        assertTrue(result.contains(logistics), "The logistics list should contain the logistics");

        // Verify that the repository method was called once
        verify(eventRepository, times(1)).findByDateDebutBetween(startDate, endDate);
    }

    @Test
    public void testCalculCout() {
        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR))
                .thenReturn(List.of(event));

        event.getLogistics().add(logistics);
        event.setCout(0f);

        eventServices.calculCout();

        assertEquals(200f, event.getCout(), 0.01);
        verify(eventRepository, times(1)).findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR);
        verify(eventRepository, times(1)).save(event);
    }
}
