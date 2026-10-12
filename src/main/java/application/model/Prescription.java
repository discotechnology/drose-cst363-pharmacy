package application.model;

import java.util.ArrayList;
import org.springframework.data.annotation.Id;

/*
 * Entity class for prescription and fill request data to mongo collection
 */

public class Prescription {
	
	public static class FillRequest {
		
		private int pharmacyID;    
		private String dateFilled;    
		private String cost;
		
		
		public int getPharmacyID() {
			return pharmacyID;
		}
		public void setPharmacyID(int pharmacyID) {
			this.pharmacyID = pharmacyID;
		}
		public String getDateFilled() {
			return dateFilled;
		}
		public void setDateFilled(String dateFilled) {
			this.dateFilled = dateFilled;
		}
		public String getCost() {
			return cost;
		}
		public void setCost(String cost) {
			this.cost = cost;
		}
		@Override
		public String toString() {
			return "PrescriptionFill [pharmacyID=" + pharmacyID + ", dateFilled=" + dateFilled + ", cost=" + cost + "]";
		}
	}
	
	@Id
	private int rxid;   
	// following fields are set when doctor creates a prescription.
	private String drugName;
	private int quantity;
	private int patientId;
	private String patientFirstName;
	private String patientLastName;
	private int doctorId;
	private String doctorFirstName;
	private String doctorLastName;
	private String dateCreated;
	private int refills;
	private ArrayList<FillRequest> fills = new ArrayList<>();

	public String getPatientFirstName() {
		return patientFirstName;
	}

	public void setPatientFirstName(String patientFirstName) {
		this.patientFirstName = patientFirstName;
	}

	public String getPatientLastName() {
		return patientLastName;
	}

	public void setPatientLastName(String patientLastName) {
		this.patientLastName = patientLastName;
	}

	public String getDoctorFirstName() {
		return doctorFirstName;
	}

	public void setDoctorFirstName(String doctorFirstName) {
		this.doctorFirstName = doctorFirstName;
	}

	public String getDoctorLastName() {
		return doctorLastName;
	}

	public void setDoctorLastName(String doctorLastName) {
		this.doctorLastName = doctorLastName;
	}

	public int getRxid() {
		return rxid;
	}
	public void setRxid(int rxid) {
		this.rxid = rxid;
	}
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	public int getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public int getRefills() {
		return refills;
	}
	public void setRefills(int refills) {
		this.refills = refills;
	}
	public ArrayList<FillRequest> getFills() {
		return fills;
	}
	public void setFills(ArrayList<FillRequest> fills) {
		this.fills = fills;
	}
	@Override
	public String toString() {
		return "PrescriptionData [rxid=" + rxid + ", drugName=" + drugName + ", quantity=" + quantity + ", patientId="
				+ patientId + ", doctorId=" + doctorId + ", dateCreated=" + dateCreated + ", refills=" + refills
				+ ", fills=" + fills + "]";
	}

}
