package nerds.studiousTestProject.studycafe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class FindStudycafeRequest {
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime endTime;
    private Integer headCount;
}