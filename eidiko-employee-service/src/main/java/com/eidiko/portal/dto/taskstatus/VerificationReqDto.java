package com.eidiko.portal.dto.taskstatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationReqDto {

    private List< @NotNull(message = "Please select any task to verify ") Long> taskDetailsId;
    private long verifiedById;

}
