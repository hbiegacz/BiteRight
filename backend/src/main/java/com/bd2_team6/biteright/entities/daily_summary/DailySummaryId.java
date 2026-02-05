package com.bd2_team6.biteright.entities.daily_summary;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
/// Class representing the double primary key of the DailySummary entity
public class DailySummaryId implements Serializable {
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "summary_date")
    private Date summaryDate;
}
