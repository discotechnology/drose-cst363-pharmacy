package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.DrugRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.model.Prescription;
import application.model.PrescriptionRepository;
import application.service.SequenceService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PrescriptionView;

@Controller
public class ControllerPrescriptionCreate {

  @Autowired
  DoctorRepository doctorRepository;

  @Autowired
  PatientRepository patientRepository;

  @Autowired
  PrescriptionRepository prescriptionRepository;

  @Autowired
  SequenceService sequence;
  @Autowired
  private DrugRepository drugRepository;

  @GetMapping("/prescription/new")
  public String getNewPrescriptionForm(Model model) {
    model.addAttribute("prescription", new Prescription());
    return "prescription_create";
  }


  @PostMapping("/prescription")
  public String createPrescription(PrescriptionView prescription, Model model) {

    //Validation section - return to form with error message if failed
    try {
      if(doctorRepository.findById(prescription.getDoctorId()) == null) {
        throw(new RuntimeException("Doctor ID does not exist."));
        }
      else {
        Doctor doctor = doctorRepository.findById(prescription.getDoctorId());
        if(!doctor.getFirstName().equals(prescription.getDoctorFirstName()) ||
            !doctor.getLastName().equals(prescription.getDoctorLastName())) {
          throw(new RuntimeException("Doctor name does not match Doctor ID."));
        }
      }

      if(patientRepository.findById(prescription.getPatientId()) == null) {
        throw(new RuntimeException("Patient ID does not exist."));
      }
      else {
        Patient patient = patientRepository.findById(prescription.getPatientId());
        if(!patient.getFirstName().equals(prescription.getPatientFirstName()) ||
            !patient.getLastName().equals(prescription.getPatientLastName())) {
          throw(new RuntimeException("Patient name does not match Patient ID."));
        }
      }

      if (drugRepository.findByName(prescription.getDrugName()) == null) {
        throw(new RuntimeException("Drug not found."));
      }

      if (prescription.getQuantity() <= 0) {
        throw(new RuntimeException("Quantity must be greater than zero."));
      }

      if (prescription.getRefills() <= 0) {
        throw(new RuntimeException("Refills must be greater than zero."));
      }
    } catch (RuntimeException e) {
      model.addAttribute("message", e.getMessage());
      model.addAttribute("prescription", prescription);
      return "prescription_create";
    }

    int id = sequence.getNextSequence("PRESCRIPTION_SEQUENCE");
    Prescription newRX = new Prescription();

    //Collect info from form
    newRX.setRxid(id);
    newRX.setDoctorId(prescription.getDoctorId());
    newRX.setDoctorFirstName(prescription.getDoctorFirstName());
    newRX.setDoctorLastName(prescription.getDoctorLastName());
    newRX.setPatientId(prescription.getPatientId());
    newRX.setPatientFirstName(prescription.getPatientFirstName());
    newRX.setPatientLastName(prescription.getPatientLastName());
    newRX.setDrugName(prescription.getDrugName());
    newRX.setQuantity(prescription.getQuantity());
    newRX.setRefills(prescription.getRefills());
    newRX.setDateCreated(LocalDate.now().toString());

    prescriptionRepository.insert(newRX);
    prescription.setRxid(id);
    model.addAttribute("message", "Prescription created.");
    model.addAttribute("prescription", prescription);
    return "prescription_show";
  }
}
