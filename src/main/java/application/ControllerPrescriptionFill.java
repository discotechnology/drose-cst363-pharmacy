package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.DrugRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.model.Pharmacy;
import application.model.PharmacyRepository;
import application.model.Prescription;
import application.model.PrescriptionRepository;
import application.service.SequenceService;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import view.*;


@Controller
public class ControllerPrescriptionFill {

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private DoctorRepository doctorRepository;

  @Autowired
  private PrescriptionRepository prescriptionRepository;

  @Autowired
  private PharmacyRepository pharmacyRepository;

  @Autowired
  private DrugRepository drugRepository;

  @Autowired
  private SequenceService sequenceService;

  /*
   * Patient requests form to fill prescription.
   */
  @GetMapping("/prescription/fill")
  public String getfillForm(Model model) {
    model.addAttribute("prescription", new PrescriptionView());
    return "prescription_fill";
  }

  // process data from prescription_fill form
  @PostMapping("/prescription/fill")
  public String processFillForm(PrescriptionView p, Model model) {
    try {
      /*
       * valid pharmacy name and address, get pharmacy id and phone
       */
      Pharmacy pharmacy = pharmacyRepository.findByNameAndAddress(
          p.getPharmacyName(), p.getPharmacyAddress());

      if (pharmacy == null) {
        //pharmacy not found
        throw new RuntimeException("Pharmacy name and/or address not found");
      } else {
        p.setPharmacyID(pharmacy.getId());
        p.setPharmacyPhone(pharmacy.getPhone());
      }

      // find the patient information
      // find the prescription
      Prescription prescription = prescriptionRepository.findByRxid(p.getRxid());
      Patient patient;
      if (prescription == null) {
        throw new RuntimeException(
            String.format("Prescription with RXID \"%d\" not found", p.getRxid())
        );
      } else {
        patient = patientRepository.findByIdAndLastName(
            prescription.getPatientId(), p.getPatientLastName());
        if (patient == null
            || patient.getId() != prescription.getPatientId()
            || !patient.getLastName().equals(p.getPatientLastName())) {
          throw new RuntimeException(
              String.format("Prescription with RXID \"%d\" and patient last name \"%s\" not found",
                  p.getRxid(), p.getPatientLastName())
          );
        }
      }

      p.setDoctorId(prescription.getDoctorId());
      p.setPatientId(prescription.getPatientId());
      p.setPatientFirstName(patient.getFirstName());
      p.setDrugName(prescription.getDrugName());
      p.setQuantity(prescription.getQuantity());
      p.setRefills(prescription.getRefills());

      /*
       * have we exceeded the number of allowed refills
       * the first fill is not considered a refill.
       */
      var fills = prescription.getFills();
      p.setRefillsRemaining(prescription.getRefills() - fills.size());
      p.setRefills(p.getRefillsRemaining());
      if (p.getRefillsRemaining() < 0) {
        throw new RuntimeException("Number of allowed refills exceeded");
      }

      /*
       * get doctor information
       */
      Doctor doctor = doctorRepository.findById(prescription.getDoctorId());
      p.setDoctorLastName(doctor.getLastName());
      p.setDoctorFirstName(doctor.getFirstName());

      /*
       * calculate cost of prescription
       */
      Pharmacy.DrugCost drugCost = pharmacy.getDrugCosts().stream()
          .filter(c -> c.getDrugName().equals(prescription.getDrugName()))
          .findFirst().orElse(null);

      if (drugCost == null) {
        throw new RuntimeException(
            String.format("Drug %s not found at %s",
                p.getDrugName(), p.getPharmacyName()));
      } else {
        p.setCost(String.valueOf(drugCost.getCost()));
      }

      // save updated prescription
      Prescription.FillRequest fillRequest = new Prescription.FillRequest();
      fillRequest.setPharmacyID(pharmacy.getId());
      fillRequest.setCost(String.valueOf(drugCost.getCost()));

      Timestamp ts = Timestamp.valueOf(LocalDateTime.now());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a");
      fillRequest.setDateFilled(formatter.format(ts.toLocalDateTime()));
      p.setDateFilled(formatter.format(ts.toLocalDateTime()));

      fills.add(fillRequest);

      prescriptionRepository.save(prescription);

      // show the updated prescription with the most recent fill information
      model.addAttribute("message", "Prescription filled.");
      model.addAttribute("prescription", p);
      return "prescription_show";

    } catch (RuntimeException e) {
      model.addAttribute("message", e.getMessage());
      model.addAttribute("prescription", p);
      return "prescription_fill";
    }
  }
}