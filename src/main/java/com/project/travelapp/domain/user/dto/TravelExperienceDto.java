package com.project.travelapp.domain.user.dto;

import com.project.travelapp.domain.user.entity.TravelExperience;
import lombok.Getter;

@Getter
public class TravelExperienceDto {
    private String country;
    private Integer visitCount;
    private Integer stayMonths;

    public TravelExperienceDto(TravelExperience experience) {
        this.country = experience.getCountry();
        this.visitCount = experience.getVisitCount();
        this.stayMonths = experience.getStayMonths();
    }
}