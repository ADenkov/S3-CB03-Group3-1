package com.secretary.secretaryapp.controller;

import com.secretary.secretaryapp.email.EmailService;
import com.secretary.secretaryapp.model.Appointment;
import com.secretary.secretaryapp.model.Client;
import com.secretary.secretaryapp.service.AppointmentService;
import com.secretary.secretaryapp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/clients")
@CrossOrigin(origins = {"*", "*"})
public class ClientController {
    @Autowired
    private ClientService clientService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendmail/{email}")
    public ResponseEntity<HttpStatus> sendmail(@PathVariable("email") String email) {

        try{
            emailService.sendMail(email, "Parking Spot", "Your Parking spot is...");
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(MailSendException s){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public @ResponseBody List<Client> getAllClients() {
       return clientService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable("id") long id) {
        Optional<Client> clientData = clientService.findById(id);

        return clientData.map(client -> new ResponseEntity<>(client, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/add")
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        try {
            clientService.save(client);
            return new ResponseEntity<>(client, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable("id") long id, @RequestBody Client client) {
        Optional<Client> clientData = clientService.findById(id);

        if (clientData.isPresent()) {
            Client _client = clientData.get();
            _client.setFirstName(client.getFirstName());
            _client.setLastName(client.getLastName());
            _client.setEmail(client.getEmail());
            _client.setLicensePlate(client.getLicensePlate());
            _client.setPhoneNumber(client.getPhoneNumber());
            clientService.save(_client);
            return new ResponseEntity<>(_client, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteClient(@PathVariable("id") long id) {
        try {
            clientService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<HttpStatus> deleteAllClients() {
        try {
            clientService.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/makeAppointment/{id}")
    public ResponseEntity<HttpStatus> makeAppointment(@PathVariable("id") long personID, @RequestBody Appointment appointment){
        try{
            appointmentService.makeAppointment(personID,appointment.getDate(),appointment.getTime());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getAppointmentsForDate/{date}")
    public ResponseEntity<List<Appointment>> getAppointmentsForDate(@PathVariable String date){
        try{
            List<Appointment> appointments = appointmentService.findAppointments(date);
            return new ResponseEntity<>(appointments,HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        }
    @GetMapping("shutdown")
    public void shutdown(){
        System.exit(0);
    }
}
