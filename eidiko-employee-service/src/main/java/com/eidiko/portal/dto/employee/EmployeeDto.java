package com.eidiko.portal.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

	
	private long empId;

	@NotBlank(message = "Employee Name is Mandatory")
	private String empName;
    @NotBlank(message = "mandatory")
	@Email(message = "Please Enter Valid Email Id")
	private String emailId;

	@PastOrPresent
	private Date dateOfJoining;

	private Timestamp modifiedDate;

	@Pattern(regexp = "\\d{10}",message="Phone Number must have 10 digits")
	private String contactNo;

	private long createdBy;
	private boolean isDeleted;
	private String status;

    private String location;
    private String workingfrom;
	
     private String designation;
     
     private int band;
     
     private int eligibleLeaves;

	 private LocalDate dateOfBirth;
}
