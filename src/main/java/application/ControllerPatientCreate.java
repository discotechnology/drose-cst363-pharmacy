package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import view.*;

/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */
@Controller
public class ControllerPatientCreate {

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private SequenceService sequenceService;

  @Autowired
  private DoctorRepository doctorRepository;


  @GetMapping("/patient/new")
  public String getNewPatientForm(Model model) {
    // return blank form for new patient registration
    model.addAttribute("patient", new PatientView());
    return "patient_register";
  }

  @PostMapping("/patient/new")
  public String createPatient(PatientView p, Model model) {
    try {
      Doctor doctor = doctorRepository.findByLastName(p.getPrimaryName());
      if (doctor == null) {
        model.addAttribute("message", "Doctor not found");
        model.addAttribute("patient", p);
        return "patient_register";
      }
      int newId = sequenceService.getNextSequence("PATIENT_SEQUENCE");
      Patient patient = new Patient();
      patient.setId(newId);
      patient.setFirstName(p.getFirstName());
      patient.setLastName(p.getLastName());
      patient.setBirthdate(p.getBirthdate());
      patient.setSsn(p.getSsn());
      patient.setStreet(p.getStreet());
      patient.setCity(p.getCity());
      patient.setState(p.getState());
      patient.setZipcode(p.getZipcode());
      patient.setPrimaryName(p.getPrimaryName());

      patientRepository.save(patient);
      p.setId(newId);

      // display patient data and the generated patient ID,  and success message
      model.addAttribute("message", "Registration successful.");
      model.addAttribute("patient", p);
      return "patient_show";
    } catch (Exception e) {
      model.addAttribute("message", "Error" + e.getMessage());
      model.addAttribute("patient", p);
      return "patient_register";
    }

  }
  /*
   * Request blank form to search for patient by id and name
   */
  @GetMapping("/patient/edit")
  public String getSearchForm(Model model) {
    model.addAttribute("patient", new PatientView());
    return "patient_get";
  }

  /*
   * Perform search for patient by patient id and name.
   */
  @PostMapping("/patient/show")
  public String showPatient(PatientView p, Model model) {
    Patient patient = patientRepository.findByIdAndLastName(p.getId(), p.getLastName());
    if(patient != null) {
      Doctor doctor = doctorRepository.findByLastName(patient.getPrimaryName());
      if(doctor == null) {
        model.addAttribute("message", "Doctor not found");
        model.addAttribute("patient", p);
        return "patient_get";
      }
      p.setFirstName(patient.getFirstName());
      p.setLastName(patient.getLastName());
      p.setBirthdate(patient.getBirthdate());
      p.setSsn(patient.getSsn());
      p.setStreet(patient.getStreet());
      p.setCity(patient.getCity());
      p.setState(patient.getState());
      p.setZipcode(patient.getZipcode());
      p.setPrimaryName(patient.getPrimaryName());
      model.addAttribute("message", "Patient found");
      model.addAttribute("patient", p);
      return "patient_show";


    } else{
      model.addAttribute("message", "Patient not found");
      model.addAttribute("patient", p);
      return "patient_get";

    }


  }


}