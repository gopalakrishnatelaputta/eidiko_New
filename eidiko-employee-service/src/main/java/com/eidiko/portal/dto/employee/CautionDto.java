package com.eidiko.portal.dto.employee;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CautionDto {


    private Long warningMailId;
    private String title;
    private String description;
    private Long warnedBy;
    private Timestamp sentDate;
    private long empId;
    private Date warningDate;
    private String severityLevel;
    private String warningLevel;

    private List<MultipartFile> attachments;
    

}
