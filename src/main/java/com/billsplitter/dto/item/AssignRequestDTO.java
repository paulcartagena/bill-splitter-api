package com.billsplitter.dto.item;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignRequestDTO {
    List<Long> participantIds;
}
