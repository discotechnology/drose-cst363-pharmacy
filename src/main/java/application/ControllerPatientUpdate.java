package application;

import application.model.Patient;
import application.model.PatientRepository;
import application.model.Doctor;
import application.model.DoctorRepository;
import view.PatientView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ControllerPatientUpdate {

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private DoctorRepository doctorRepository;

  @GetMapping("/patient/edit/{id}")
  public String getUpdateForm(@PathVariable int id, Model model) {

    Patient patient = patientRepository.findById(id);
    if (patient == null) {
      model.addAttribute("message", "Patient not found.");
      return "index";
    }

    PatientView pv = new PatientView();
    pv.setId(patient.getId());
    pv.setFirstName(patient.getFirstName());
    pv.setLastName(patient.getLastName());
    pv.setBirthdate(patient.getBirthdate());
    pv.setSsn(patient.getSsn());
    pv.setStreet(patient.getStreet());
    pv.setCity(patient.getCity());
    pv.setState(patient.getState());
    pv.setZipcode(patient.getZipcode());
    pv.setPrimaryName(patient.getPrimaryName());

    model.addAttribute("message", "Patient loaded successfully.");
    model.addAttribute("patient", pv);

    return "patient_edit";
  }

  // POST: Submit changes from form
  @PostMapping("/patient/edit")
  public String updatePatient(@ModelAttribute PatientView p, Model model) {

    Patient patient = patientRepository.findById(p.getId());
    if (patient == null) {

      model.addAttribute("message", "Patient not found.");
      model.addAttribute("patient", p);
      return "patient_edit";
    }

    Doctor doctor = doctorRepository.findByLastName(p.getPrimaryName());
    if (doctor == null) {

      model.addAttribute("message", "Error: Doctor not found.");
      model.addAttribute("patient", p);
      return "patient_edit";
    }

    patient.setStreet(p.getStreet());
    patient.setCity(p.getCity());
    patient.setState(p.getState());
    patient.setZipcode(p.getZipcode());
    patient.setPrimaryName(p.getPrimaryName());

    patientRepository.save(patient);

    model.addAttribute("message", "Patient updated successfully.");
    model.addAttribute("patient", p);
    return "patient_show";
  }
}