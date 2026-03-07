package com.tutorial.uprit.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentFilterResponse {

    private Long id;
    private String name;
    private String email;
    private String department;
    private Integer year;
    private Integer xp;
    private Integer level;
    private String role;
    private Integer totalPosts;
    private Integer verifiedPosts;
    private List<String> skills;
}
