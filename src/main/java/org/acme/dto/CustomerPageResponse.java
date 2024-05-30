package org.acme.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomerPageResponse {

    private List<CustomerResponse> records;

    private int total_records;

    private int current_page;

    private int total_pages;

    private Integer next_page;

    private Integer prev_page;
}
